package com.NoWater.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.request.DelFileRequest;
import com.qcloud.cos.request.UploadFileRequest;
import net.sf.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
                    || originalFilename.endsWith(".png"))) {
                throw new RuntimeException("Format Error" + originalFilename);
            }
            String extensions = originalFilename.substring(originalFilename.indexOf("."));
            LogHelper.info("extensions: " + extensions);
            //商品图片
            File dir = new File(fileDir);
            //目录不存在则创建目录
            if (!dir.exists()) {
                dir.mkdir();
            }
            //保存文件
            String FilePath = fileDir + fileName + extensions;
            multipartFile.transferTo(new File(FilePath));
            LogHelper.info("success save file: " + FilePath);
            return fileName + extensions;
        }
        return "";
    }

    public static int UploadCOS(String localPath, String fileName) {
        int appId = Integer.parseInt(NoWaterProperties.getCos_appId());
        String secretId = NoWaterProperties.getCos_secretId();
        String secretKey = NoWaterProperties.getCos_secretKey();
        String bucketName = NoWaterProperties.getCos_bucketName();

        COSClient cosClient = new COSClient(appId, secretId, secretKey);

        UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, "/" + fileName, localPath);
        String uploadFileRet = cosClient.uploadFile(uploadFileRequest);

        JSONObject jsonObject = JSONObject.fromObject(uploadFileRet);
        return jsonObject.getInt("code");
    }

    public static int DeleteCOS(String fileName) {
        int appId = Integer.parseInt(NoWaterProperties.getCos_appId());
        String secretId = NoWaterProperties.getCos_secretId();
        String secretKey = NoWaterProperties.getCos_secretKey();
        String bucketName = NoWaterProperties.getCos_bucketName();

        COSClient cosClient = new COSClient(appId, secretId, secretKey);
        
        DelFileRequest delFileRequest = new DelFileRequest(bucketName, "/" + fileName);
        String delFileRet = cosClient.delFile(delFileRequest);
        JSONObject jsonObject = JSONObject.fromObject(delFileRet);
        return jsonObject.getInt("code");
    }
}
