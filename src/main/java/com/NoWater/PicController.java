package com.NoWater;

import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import com.NoWater.util.FIleUpload;

/**
 * Created by 李鹏飞 on 2016/12/5 0005.
 */

@RestController
public class PicController {
    /**
     * 添加新的Goods
     *
     * @return resMap: 成功msg==>"ok" / 失败 error==>错误信息
     */
    @RequestMapping(value = "/picUpload", method = RequestMethod.POST)
    public JSONObject add(HttpServletResponse response, HttpServletRequest request,
                          @RequestParam(value = "goodsPic[]", required = false) MultipartFile[] goodsPics) {//商品详情图,单文件、多文件都可
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
//        try {
////            String goodsIdStr = request.getParameter("godosId");
//            String goodsIdStr = "111222";
//            //XXX表单其他数据的处理
//            FIleUpload.saveImgs(goodsPics, Long.valueOf(goodsIdStr));//保存图片
//            jsonObject.put("msg", "ok");
//        } catch (Exception e) {
//            e.printStackTrace();
//            jsonObject.put("error", "提交商品出错" + e.getMessage());
//        }
//        return jsonObject;
    }
}
