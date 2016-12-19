package com.NoWater.administrator;

import com.NoWater.model.User;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.LogHelper;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李鹏飞 on 2016/12/19 0019.
 */
@RestController
public class AdminBlackList {
    private Jedis jedis;

    @RequestMapping("admin/customer/blacklist/adding")
    public JSONObject CustomerBlackListAdding(
            @RequestParam(value = "userId", defaultValue = "0") int userId,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "admin_token");
        String admin = CookieUtil.confirmUser(token);

        if (admin == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[admin/customer/blacklist/adding] %s", jsonObject.toString()));
            return jsonObject;
        }

        DBUtil db = new DBUtil();
        List<Object> list = new ArrayList<>();
        String sql = "select * from user where user_id = ?";
        list.add(userId);
        List<User> userList = db.queryInfo(sql,list,User.class);

        if (userList.size() == 0 ){
            jsonObject.put("status", 400);
            LogHelper.info(String.format("[admin/customer/blacklist/adding] %s", jsonObject.toString()));
            return jsonObject;
        }   else {
            List<Object> param = new ArrayList<>();
            sql = "UPDATE user SET status = -1 WHERE user_id = ?";
            param.add(userId);
            db.insertUpdateDeleteExute(sql, param);
            jsonObject.put("status", 200);
            LogHelper.info(String.format("[admin/customer/blacklist/adding] %s", jsonObject.toString()));
        }

        jedis = new Jedis("127.0.0.1", 6379);
        String user_id = String.valueOf(userId);
        jedis.del(user_id);
        return jsonObject;
    }

    @RequestMapping("admin/customer/blacklist/deleting")
    public JSONObject CustomerBlackListDeleting(
            @RequestParam(value = "userId", defaultValue = "0") int userId,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "admin_token");
        String admin = CookieUtil.confirmUser(token);

        if (admin == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[admin/customer/blacklist/deleting] %s", jsonObject.toString()));
            return jsonObject;
        }

        DBUtil db = new DBUtil();
        List<Object> list = new ArrayList<>();
        String sql = "select * from user where user_id = ?";
        list.add(userId);
        List<User> userList = db.queryInfo(sql,list,User.class);

        if (userList.size() == 0 ){
            jsonObject.put("status", 400);
            LogHelper.info(String.format("[admin/customer/blacklist/deleting] %s", jsonObject.toString()));
            return jsonObject;
        }   else {
            List<Object> param = new ArrayList<>();
            sql = "UPDATE user SET status = 1 WHERE user_id = ?";
            param.add(userId);
            db.insertUpdateDeleteExute(sql, param);
            jsonObject.put("status", 200);
            LogHelper.info(String.format("[admin/customer/blacklist/deleting] %s", jsonObject.toString()));
        }

        return jsonObject;
    }

    @RequestMapping("admin/shop/blacklist/adding")
    public JSONObject ShopBlackListAdding(
            @RequestParam(value = "shopId", defaultValue = "0") int shopId,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "admin_token");
        String admin = CookieUtil.confirmUser(token);

        if (admin == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[admin/shop/blacklist/adding] %s", jsonObject.toString()));
            return jsonObject;
        }

        DBUtil db = new DBUtil();
        List<Object> list = new ArrayList<>();
        String sql = "select * from shop where shop_id = ?";
        list.add(shopId);
        List<User> userList = db.queryInfo(sql,list,User.class);

        if (userList.size() == 0 ){
            jsonObject.put("status", 400);
            LogHelper.info(String.format("[admin/shop/blacklist/adding] %s", jsonObject.toString()));
            return jsonObject;
        }   else {
            List<Object> param = new ArrayList<>();
            sql = "UPDATE shop SET status = -1 WHERE user_id = ?";
            param.add(shopId);
            db.insertUpdateDeleteExute(sql, param);
            jsonObject.put("status", 200);
            LogHelper.info(String.format("[admin/shop/blacklist/adding] %s", jsonObject.toString()));
        }

        return jsonObject;
    }

    @RequestMapping("admin/shop/blacklist/deleting")
    public JSONObject ShopBlackListDeleting(
            @RequestParam(value = "shopId", defaultValue = "0") int shopId,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "admin_token");
        String admin = CookieUtil.confirmUser(token);

        if (admin == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[admin/shop/blacklist/deleting] %s", jsonObject.toString()));
            return jsonObject;
        }

        DBUtil db = new DBUtil();
        List<Object> list = new ArrayList<>();
        String sql = "select * from shop where shop_id = ?";
        list.add(shopId);
        List<User> userList = db.queryInfo(sql,list,User.class);

        if (userList.size() == 0 ){
            jsonObject.put("status", 400);
            LogHelper.info(String.format("[admin/shop/blacklist/deleting] %s", jsonObject.toString()));
            return jsonObject;
        }   else {
            List<Object> param = new ArrayList<>();
            sql = "UPDATE shop SET status = 1 WHERE shop_id = ?";
            param.add(shopId);
            db.insertUpdateDeleteExute(sql, param);
            jsonObject.put("status", 200);
            LogHelper.info(String.format("[admin/shop/blacklist/deleting] %s", jsonObject.toString()));
        }

        return jsonObject;
    }


}
