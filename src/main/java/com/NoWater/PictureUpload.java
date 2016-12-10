package com.NoWater;

import com.NoWater.util.CookieUtil;
import com.NoWater.util.LogHelper;
import com.NoWater.util.Uuid;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.filters.TruePropertyFilter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.NoWater.util.FileUpload;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by 李鹏飞 on 2016/12/5 0005.
 */

@RestController
public class PictureUpload {
    /**
     * 上传图片
     */
    @RequestMapping(value = "/picture/upload", method = RequestMethod.POST)
    public JSONObject add(HttpServletResponse response, HttpServletRequest request,
                          @RequestParam(value = "goodsPic[]") MultipartFile[] PictureFile) {
        //商品详情图,单文件、多文件都可
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String userId = "1";
//        String uuid = CookieUtil.getCookieValueByName(request, "token");
//        String userId = CookieUtil.confirmUser(uuid);
//        if (userId == null) {
//            jsonObject.put("status", 300);
//            return jsonObject;
//        }

        try {
            ArrayList<String> filenameList = FileUpload.handleFile(PictureFile, userId);    //保存图片
            jsonObject.put("status", 200);
            jsonObject.put("data", JSONArray.fromObject(filenameList));
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 400);
        }

        return jsonObject;
    }
}
