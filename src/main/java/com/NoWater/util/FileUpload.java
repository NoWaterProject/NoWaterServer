package com.NoWater.util;

import com.NoWater.model.Photo;
import com.NoWater.model.Product;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.request.DelFileRequest;
import com.qcloud.cos.request.UploadFileRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.cglib.core.ProcessArrayCallback;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李鹏飞 on 2016/12/5 0005.
 */

public final class FileUpload {

    public static ArrayList<String> handleFile(MultipartFile[] pictureFile, String fileDir) {
        try {
            return saveImgs(pictureFile, fileDir);  //保存图片
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<String> saveImgs(MultipartFile[] pictureFile, String fileDir) throws IllegalStateException, IOException {
        if (pictureFile == null || pictureFile.length <= 0) {//数组无图片
            return null;
        }

        ArrayList<String> filenameList = new ArrayList<>();

        for (MultipartFile multipartFile : pictureFile) {
            String fileName = Uuid.getUuid();
            filenameList.add(saveImg(multipartFile, fileDir, fileName));
        }
        return filenameList;
    }

    /**
     * 保存单张图片
     */
    private static String saveImg(MultipartFile multipartFile, String fileDir, String fileName) throws IllegalStateException, IOException {
        if (multipartFile != null && multipartFile.getSize() > 0) {
            //有图
            String originalFilename = multipartFile.getOriginalFilename();
            if (!(originalFilename.endsWith(".jpg") || originalFilename.endsWith(".jpeg")
                    || originalFilename.endsWith(".png") || originalFilename.endsWith(".JPG")
                    || originalFilename.endsWith(".JPEG") || originalFilename.endsWith(".PNG"))) {
                throw new RuntimeException("Format Error" + originalFilename);
            }
            String extensions = originalFilename.substring(originalFilename.indexOf("."));
            LogHelper.info("extensions: " + extensions);
            //商品图片
            String filePath = "/tmp/" + fileDir;
            File dir = new File(filePath);
            //目录不存在则创建目录
            if (!dir.exists()) {
                dir.mkdir();
            }
            //保存文件
            multipartFile.transferTo(new File(filePath + "/" + fileName + extensions));
            LogHelper.info("success save file: " + filePath + "/" + fileName + extensions);
            return fileName + extensions;
        }
        return "";
    }

    public static int UploadToCOS(ArrayList<String> addFileNameList, String userId, String belongId, int photoType) {
        for (int i = 0; i < addFileNameList.size(); i++) {
            String fileName = addFileNameList.get(i);
            String path = "/tmp/" + userId + "/" + fileName;
            LogHelper.info("addFile:" + path);

            String cmd = "/usr/bin/python bin/COS_API.py upload " + "/" + fileName + " " + path;
            LogHelper.info("add file cmd:" + cmd);
            String rmCmd = "rm -f " + path;
            try {
                Process proc = Runtime.getRuntime().exec(cmd);
                proc.waitFor();
                Process proc2 = Runtime.getRuntime().exec(rmCmd);
                proc2.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }

            DBUtil db = new DBUtil();
            List<Object> insertPhoto = new ArrayList<>();
            insertPhoto.add(fileName);
            insertPhoto.add(belongId);
            insertPhoto.add("http://koprvhdix117-10038234.file.myqcloud.com/" + fileName);
            insertPhoto.add(photoType);
            String insertPhotoSQL = "insert into `photo` (file_name, belong_id, url, photo_type) values (?, ?, ?, ?)";
            db.insertUpdateDeleteExute(insertPhotoSQL, insertPhoto);
        }
        return 1;
    }

    public static int DeleteFileCOS(ArrayList<String> deleteFileNameList) {
        for (int i = 0; i < deleteFileNameList.size(); i++) {
            String fileName = deleteFileNameList.get(i);
            String path = "/" + deleteFileNameList.get(i);
            String cmd = "/usr/bin/python /root/src/base/COS_API.py delete " + path;
            LogHelper.info("delete cmd:" + cmd);
            try {
                Process procDelete = Runtime.getRuntime().exec(cmd);
                procDelete.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
                return -2;
            }

            DBUtil db = new DBUtil();
            String deleteSQL = "update `photo` set `is_del` = 1 where `file_name` = ?";
            List<Object> deleteFile = new ArrayList<>();
            deleteFile.add(fileName);
            db.insertUpdateDeleteExute(deleteSQL, deleteFile);
        }
        return 1;
    }

    public static ArrayList<String> jsonToArrayList(String jsonString) {
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        int jsonArraySize = jsonArray.size();
        ArrayList<String> returnFileName = new ArrayList<>();
        for (int i = 0; i < jsonArraySize; i++) {
            String fileName = jsonArray.getString(i);
            returnFileName.add(fileName);
        }
        return returnFileName;
    }

    public static int fileExists(String userId, ArrayList<String> fileNameList) {
        for (int i = 0; i < fileNameList.size(); i++) {
            String fileName = fileNameList.get(i);
            String path = "/tmp/" + userId + "/" + fileName;
            File dir = new File(path);
            if (!dir.exists()) {
                return -4;
            }
        }
        return 0;
    }

    public static ArrayList<String> dbFileNameList(String belong_id, int photoType) {
        String fileNameSQL = "select file_name from photo where belong_id = ? and is_del = 0 and photo_type = ?";
        List<Object> getFileNameList = new ArrayList<>();
        getFileNameList.add(belong_id);
        getFileNameList.add(photoType);
        DBUtil db = new DBUtil();
        List<Photo> dbFileList;
        try {
            dbFileList = db.queryInfo(fileNameSQL, getFileNameList, Photo.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        ArrayList<String> deleteFileNameList = new ArrayList<>();
        for (int i = 0; i < dbFileList.size(); i++) {
            deleteFileNameList.add(dbFileList.get(i).getFile_name());
        }
        return deleteFileNameList;
    }
}
