package com.NoWater.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.request.UploadFileRequest;
import com.qcloud.cos.sign.Credentials;
import net.sf.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by 李鹏飞 on 2016/12/5 0005.
 */

public class FIleUpload {

    public static int handleFile(MultipartFile[] pictureFile, String fileDir, String fileName) {
        try {
            int state = saveImgs(pictureFile, fileDir, fileName);  //保存图片
            if (state == -1) {
                return -1;
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int saveImgs(MultipartFile[] pictureFile, String fileDir, String fileName) throws IllegalStateException, IOException {
        if (pictureFile == null || pictureFile.length <= 0) {//数组无图片
            return -1;
        }
        for (MultipartFile multipartFile : pictureFile) {
            saveImg(multipartFile, fileDir, fileName);
        }

    }

    /**
     * 保存单张图片
     */
    private static void saveImg(MultipartFile multipartFile, String fileDir, String fileName) throws IllegalStateException, IOException {
        if (multipartFile != null && multipartFile.getSize() > 0) {
            //有图
            String originalFilename = multipartFile.getOriginalFilename();
            if (!(originalFilename.endsWith(".jpg") || originalFilename.endsWith(".jpeg")
                    || originalFilename.endsWith(".png"))) {
                throw new RuntimeException("Format Error" + originalFilename);
            }
            //商品图片
            File dir = new File(fileDir);
            //目录不存在则创建目录
            if (!dir.exists()) {
                dir.mkdir();
            }
            //保存文件
            multipartFile.transferTo(new File(fileDir + fileName));
        }
    }

    public static int UploadCOS(String localPath, String fileName, String field) {
        int appId = Integer.parseInt(NoWaterProperties.getCos_appId());
        String secretId = NoWaterProperties.getCos_secretId();
        String secretKey = NoWaterProperties.getCos_secretKey();
        String bucketName = NoWaterProperties.getCos_bucketName();

        COSClient cosClient = new COSClient(appId, secretId, secretKey);

        UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, field + fileName, localPath);
        String uploadFileRet = cosClient.uploadFile(uploadFileRequest);

        JSONObject jsonObject = JSONObject.fromObject(uploadFileRet);
        return jsonObject.getInt("code");
    }
}
