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
    @RequestMapping("/shop-owner/ad")
    public JSONObject shopOwnerAd(@RequestParam(value = "filename") String filename,
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

        String getShopIdSQL = "select * from `shop` where `owner_id` = ?";
        List<Object> getShopId = new ArrayList<>();
        getShopId.add(Integer.parseInt(userId));
        List<Shop> ShopList;
        try {
            ShopList = db.queryInfo(getShopIdSQL, getShopId, Shop.class);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
            return jsonObject;
        }

        if (ShopList.size() == 0) {
            jsonObject.put("status", 400);
            return jsonObject;
        }

        String deleteSQL = "update `photo` set `is_del` = 1 where `belong_id` = ? and `photo_type` = 0 and `is_del` = 0";
        List<Object> deleteList = new ArrayList<>();
        deleteList.add(ShopList.get(0).getShopId());
        db.insertUpdateDeleteExute(deleteSQL, deleteList);

        ArrayList<String> addFileNameList = new ArrayList<>();
        addFileNameList.add(filename);
        int status = FileUpload.UploadToCOS(addFileNameList, userId, String.valueOf(ShopList.get(0).getShopId()), 0);
        if (status == -1) {
            jsonObject.put("status", 1010);
            return jsonObject;
        }

        jsonObject.put("status", 200);
        return jsonObject;
    }
}
