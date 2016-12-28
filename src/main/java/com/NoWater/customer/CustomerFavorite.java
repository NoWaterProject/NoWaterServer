package com.NoWater.customer;

import com.NoWater.model.Favorite;
import com.NoWater.model.User;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
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
    public JSONObject customerFavoriteAdding(
            @RequestParam(value = "type", defaultValue = "/") int type,//type=1 店铺  type=2 商品
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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());

        DBUtil db = new DBUtil();
        List<Object> list = new ArrayList<>();
        String sql = "insert into favorite (user_id,favorite_type,id,favorite_time) value (?,?,?,?)";
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
    public JSONObject customerFavoriteDeleting(
            @RequestParam(value = "type", defaultValue = "/") int type,//type=1 店铺  type=2 商品
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
        List<Object> list = new ArrayList<>();
        String sql = "update favorite set is_del = 1 where user_id = ? and id = ? and favorite_type = ?";
        list.add(userId);
        list.add(id);
        list.add(type);
        try {
            db.insertUpdateDeleteExute(sql, list);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1000);
            return jsonObject;
        }
        jsonObject.put("status", 200);
        return jsonObject;
    }
}
