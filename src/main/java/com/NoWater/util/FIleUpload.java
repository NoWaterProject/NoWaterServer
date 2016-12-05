package com.NoWater.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by 李鹏飞 on 2016/12/5 0005.
 */

//@Transactional(rollbackFor={IllegalStateException.class,IOException.class,RuntimeException.class})
public class FIleUpload {

    public static void saveImgs(MultipartFile[] goodsPics, long goodsId) throws IllegalStateException, IOException {
        if(goodsPics==null || goodsPics.length<=0){//数组无图片
            return;
        }
        for (MultipartFile multipartFile : goodsPics) {
            saveImg(multipartFile, goodsId);
        }
    }

    /**
     * 保存单张图片
     * @param multipartFile
     * @param goodsId 商品ID
     * @author Nifury
     */
    private static void saveImg(MultipartFile multipartFile, long goodsId) throws IllegalStateException, IOException {
        if(multipartFile!=null && multipartFile.getSize()>0){//有图
            String originalFilename = multipartFile.getOriginalFilename();
            if( !(originalFilename.endsWith(".jpg") || originalFilename.endsWith(".jpeg")
                    || originalFilename.endsWith(".png") ) ){
                throw new RuntimeException("图片格式不正确==>"+originalFilename);
            }
            String GOODS_PIC_DIR = "/tmp/pictmp/";//商品图片
            File dir = new File(GOODS_PIC_DIR  +goodsId+"/");
            if (!dir.exists()) {//目录不存在则创建目录
                dir.mkdir();
            }
            String newFilename = (long)(Math.random()*1000000)
                    +originalFilename.substring(originalFilename.lastIndexOf("."));
            multipartFile.transferTo(new File(GOODS_PIC_DIR  +goodsId+"/"+newFilename));//保存文件

        }
    }
}
