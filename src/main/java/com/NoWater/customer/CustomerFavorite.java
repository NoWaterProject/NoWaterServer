package com.NoWater.customer;

import com.NoWater.model.Favorite;
import com.NoWater.model.Product;
import com.NoWater.model.User;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.ProductShopUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.lang.Object;

/**
 * Created by qingpeng on 2016/12/8.
 */
@RestController
public class CustomerFavorite {

    @RequestMapping("customer/favorite/adding")
    public JSONObject customerFavoriteAdding(@RequestParam(value = "type", defaultValue = "/") int type, //type=1 店铺  type=2 商品
                                             @RequestParam(value = "id", defaultValue = "/") int id,
                                             HttpServletRequest request, HttpServletResponse response) {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);
        if (userId == null) {
            jsonObject.put("status", 300);
            return jsonObject;
        }
        DBUtil db = new DBUtil();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());

        String confirmFavorite = "select * from `favorite` where `favorite_type` = ? and `id` = ? and `user_id` = ? and `is_del` = 0";
        List<Object> objectList = new ArrayList<>();
        objectList.add(type);
        objectList.add(id);
        objectList.add(userId);
        try {
            List<Favorite> favoriteList = db.queryInfo(confirmFavorite, objectList, Favorite.class);
            if (favoriteList.size() > 0) {
                jsonObject.put("status", 400);      //  已添加到收藏
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
            return jsonObject;
        }

        if (type == 1) {
            if (!ProductShopUtil.ShopExist(id)) {
                jsonObject.put("status", 500);
                return jsonObject;
            }
        } else {
            if (!ProductShopUtil.ProductExist(id)) {
                jsonObject.put("status", 500);
                return jsonObject;
            }
        }

        List<Object> list = new ArrayList<>();
        String sql = "insert into favorite (user_id, favorite_type, id, time) value (?, ?, ?, ?)";
        list.add(userId);
        list.add(type);
        list.add(id);
        list.add(time);
        try {
            db.insertUpdateDeleteExute(sql, list);
            jsonObject.put("status", 200);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1000);
            return jsonObject;
        }

        return jsonObject;
    }

    @RequestMapping("customer/favorite/deleting")
    public JSONObject customerFavoriteDeleting(@RequestParam(value = "favoriteId") int favoriteId,
                                               HttpServletRequest request, HttpServletResponse response) {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);
        if (userId == null) {
            jsonObject.put("status", 300);
            return jsonObject;
        }
        DBUtil db = new DBUtil();

        String confirmFavoriteSQL = "select * from `favorite` where `favorite_id` = ? and `user_id` = ?";
        List<Object> objectList = new ArrayList<>();
        objectList.add(favoriteId);
        objectList.add(userId);
        List<Favorite> favoriteList;
        try {
            favoriteList = db.queryInfo(confirmFavoriteSQL, objectList, Favorite.class);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
            return jsonObject;
        }

        if (favoriteList.size() == 0) {
            jsonObject.put("status", 400);
            return jsonObject;
        }

        List<Object> list = new ArrayList<>();
        String sql = "update `favorite` set `is_del` = 1 where `favorite_id` = ?";
        list.add(favoriteId);
        try {
            db.insertUpdateDeleteExute(sql, list);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
            return jsonObject;
        }
        jsonObject.put("status", 200);
        return jsonObject;
    }

    @RequestMapping("/customer/favorite/list")
    public JSONObject customerFavoriteList(@RequestParam(value = "type", defaultValue = "/") int type, //type=1 店铺  type=2 商品
                                           HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);
        if (userId == null) {
            jsonObject.put("status", 300);
            return jsonObject;
        }
        DBUtil db = new DBUtil();

        String getFavorite = "select * from `favorite` where `user_id` = ? and `favorite_type` = ? and `is_del` = 0";
        List<Object> objectList = new ArrayList<>();
        objectList.add(userId);
        objectList.add(type);
        try {
            List<Favorite> favoriteList = db.queryInfo(getFavorite, objectList, Favorite.class);
            if (favoriteList.size() == 0) {
                jsonObject.put("data", "[]");
                return jsonObject;
            } else {
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < favoriteList.size(); i++) {
                    int Id = favoriteList.get(i).getId();
                    JSONObject jsonObject1;
                    if (type == 1) {
                        jsonObject1 = ProductShopUtil.GetShopDetail(Id, false);
                    } else {
                        jsonObject1 = ProductShopUtil.GetProductDetail(Id, true, false, false);
                    }
                    jsonObject1.put("favoriteId", favoriteList.get(i).getFavoriteId());
                    jsonObject1.put("favoriteType",favoriteList.get(i).getFavoriteType());
                    jsonObject1.put("id", favoriteList.get(i).getId());
                    jsonObject1.put("time", favoriteList.get(i).getTime());
                    jsonArray.add(jsonObject1);
                }
                jsonObject.put("data", jsonArray);
            }
            jsonObject.put("status", 200);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
        }
        return jsonObject;
    }
}
