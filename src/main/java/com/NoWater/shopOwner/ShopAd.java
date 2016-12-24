package com.NoWater.shopOwner;

import com.NoWater.model.Shop;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.FileUpload;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Koprvhdix on 2016/12/18.
 */
@RestController
public class ShopAd {
    @RequestMapping("/shop-owner/shop/ad/apply")
    public JSONObject shopOwnerShopAdApply(@RequestParam(value = "filename") String filename,
                                  @RequestParam(value = "price") double price,
                                  HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);
        DBUtil db = new DBUtil();

        if (userId == null) {
            jsonObject.put("status", 300);
            return jsonObject;
        }

        int shopId = CookieUtil.confirmShop(userId);

        if (shopId == -1) {
            jsonObject.put("status", 500);      // not shop owner
            return jsonObject;
        }

        ArrayList<String> addFileNameList = new ArrayList<>();
        addFileNameList.add(filename);
        int status = FileUpload.UploadToCOS(addFileNameList, userId, String.valueOf(shopId), 0);
        if (status == -1) {
            jsonObject.put("status", 1010);
            return jsonObject;
        }

        jsonObject.put("status", 200);
        return jsonObject;
    }

    @RequestMapping("/shop-owner/shop/ad/list")
    public JSONObject shopOwnerShopAdList(HttpServletRequest request, HttpServletResponse response) {

    }

    @RequestMapping("/shop-owner/product/ad/apply")
    public JSONObject shopOwnerProductAdApply(HttpServletRequest request, HttpServletResponse response) {

    }

    @RequestMapping("/shop-owner/product/ad/list")
    public JSONObject shopOwnerProductAdList(HttpServletRequest request, HttpServletResponse response) {

    }
}
