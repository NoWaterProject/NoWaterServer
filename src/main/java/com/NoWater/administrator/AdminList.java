package com.NoWater.administrator;

import com.NoWater.model.Shop;
import com.NoWater.model.User;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.LogHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李鹏飞 on 2016/12/19 0019.
 */
@RestController
public class AdminList {

    @RequestMapping("/admin/shop/list")
    public JSONObject adminShopList(@RequestParam(value = "shopType", defaultValue = "/") int shopType,
                                    @RequestParam(value = "count", defaultValue = "/") int count,
                                    @RequestParam(value = "startId", defaultValue = "0") int startId,
                                    HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        int actualCount;
        int endId;

        String token = CookieUtil.getCookieValueByName(request, "admin_token");
        String admin = CookieUtil.confirmUser(token);

        if (admin == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[/admin/shop/list] %s", jsonObject.toString()));
            return jsonObject;
        }

        DBUtil db = new DBUtil();
        List<Object> list = new ArrayList<>();
        String sql = "select * from shop where status = ?";
        list.add(shopType);
        List<Shop> shopList = db.queryInfo(sql, list, Shop.class);

        if (shopList.size() == 0) {
            jsonObject.put("status", 200);
            jsonObject.put("data", "[]");
            jsonObject.put("endId", -1);
            LogHelper.info(String.format("[/admin/shop/list] %s", jsonObject.toString()));
            return jsonObject;
        } else {
            if (shopList.size() - startId > count) {
                endId = startId + count;
                actualCount = count;
                jsonObject.put("endId", endId);
            } else {
                actualCount = shopList.size() - startId;
                jsonObject.put("endId", -1);
            }

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < actualCount; i++) {
                JSONObject jsonObject1 = JSONObject.fromObject(shopList.get(i + startId));
                jsonArray.add(jsonObject1);
            }

            jsonObject.put("status", 200);
            jsonObject.put("data", jsonArray);
            LogHelper.info(String.format("[/admin/shop/list] %s", jsonObject.toString()));
        }

        return jsonObject;
    }

    @RequestMapping("/admin/customer/list")
    public JSONObject adminCustomerList(@RequestParam(value = "customerType", defaultValue = "/") int customerType,
                                        @RequestParam(value = "count", defaultValue = "/") int count,
                                        @RequestParam(value = "startId", defaultValue = "0") int startId,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        int actualCount;
        int endId;

        String token = CookieUtil.getCookieValueByName(request, "admin_token");
        String admin = CookieUtil.confirmUser(token);

        if (admin == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[/admin/customer/list] %s", jsonObject.toString()));
            return jsonObject;
        }

        DBUtil db = new DBUtil();
        List<Object> list = new ArrayList<>();
        String sql = "select * from user where status = ?";
        list.add(customerType);
        List<User> userList = db.queryInfo(sql, list, User.class);

        if (userList.size() == 0) {
            jsonObject.put("status", 200);
            jsonObject.put("data", "[]");
            jsonObject.put("endId", -1);
            LogHelper.info(String.format("[/admin/customer/list] %s", jsonObject.toString()));
            return jsonObject;
        } else {
            if (userList.size() - startId > count) {
                endId = startId + count;
                jsonObject.put("endId", endId);
                actualCount = count;
            } else {
                actualCount = userList.size() - startId;
                jsonObject.put("endId", -1);
            }

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < actualCount; i++) {
                JSONObject jsonObject1 = JSONObject.fromObject(userList.get(i + startId));
                jsonArray.add(jsonObject1);
            }

            jsonObject.put("status", 200);
            jsonObject.put("data", jsonArray);
            LogHelper.info(String.format("[/admin/customer/list] %s", jsonObject.toString()));
        }

        return jsonObject;
    }

}
