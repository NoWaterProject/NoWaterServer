package com.NoWater.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.NoWater.model.Admin;
import com.NoWater.model.Product;
import com.NoWater.model.Status;
import com.NoWater.model.User;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.LogHelper;
import com.NoWater.util.Uuid;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wukai on 2016/11/21.
 */
@RestController
public class CustomerLogin {
    private Jedis jedis;

    @RequestMapping("/customer/login")
    public JSONObject login(@RequestParam(value = "name", defaultValue = "/") String name,
                            @RequestParam(value = "password", defaultValue = "/") String password,
                            HttpServletResponse response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        int status = 0;
        String uuid;
        JSONObject jsonObject = new JSONObject();

        List<Object> list = new ArrayList<>();
        DBUtil db = new DBUtil();
        StringBuffer sql = new StringBuffer();
        sql.append("select password, user_id from user where name = ? and `status` != -1 and `status` != -2");
        list.add(name);
        List<User> userList = db.queryInfo(sql.toString(), list, User.class);
        if (userList.size() == 0) {
            status = 300;
            jsonObject.put("status", status);
        } else {
            if (userList.get(0).getPassword().equals(password)) {
                status = 200;
                jsonObject.put("status", status);
                uuid = Uuid.getUuid();
                Cookie cookie = new Cookie("token", uuid);
                cookie.setMaxAge(1800);
                cookie.setPath("/");
                response.addCookie(cookie);
                String user_id = userList.get(0).getUserId().toString();

                jedis = new Jedis("127.0.0.1", 6379);
                jedis.set(uuid, user_id);
                jedis.set(user_id, uuid);
                jedis.expire(uuid, 1800);
                jedis.expire(user_id, 1800);

                LogHelper.info(name + "\tlogin");
            } else {
                status = 300;
                jsonObject.put("status", status);
            }
        }
        return jsonObject;
    }

    @RequestMapping("/customer/loginout")
    public JSONObject customerLoginOut(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "token");
        if (token != null) {
            jedis = new Jedis("127.0.0.1", 6379);
            String user_id = jedis.get(token);
            if (user_id == null) {
                jsonObject.put("status", 300);
            } else {
                String realToken = jedis.get(user_id);

                if (token.equals(realToken)) {
                    // 核心start
                    jedis.del(user_id);
                    jedis.del(token);
                    jsonObject.put("status", 200);
                    // 核心end
                } else {
                    jsonObject.put("status", 300);
                }
            }
        } else {
            jsonObject.put("status", 300);
        }

        return jsonObject;
    }

    @RequestMapping("/customer/info/detail")
    public JSONObject customerInfoDetail(HttpServletRequest request, HttpServletResponse response) {
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
        String sql = "select `user_id`,`name`,`telephone`,`address1`,`address2`,`address3`,`post_code`,`first_name`,`last_name`,`status` from `user` where `user_id` = ?";
        List<Object> paramUser = new ArrayList<>();
        paramUser.add(userId);
        List<User> userList;
        try {
            userList = db.queryInfo(sql, paramUser, User.class);
            if (userList.size() == 0) {
                jsonObject.put("status", 500);
                return jsonObject;
            }

        } catch (Exception e) {
            jsonObject.put("status", 1100);
            e.printStackTrace();
            return jsonObject;
        }

        jsonObject.put("status",200);
        jsonObject.put("data",JSONObject.fromObject(userList.get(0)));

        return jsonObject;
    }

    @RequestMapping("/customer/password/changing")
    public JSONObject customerInfoEdit(@RequestParam(value = "oldPassword", defaultValue = "/") String oldPassword,
                                       @RequestParam(value = "newPassword", defaultValue = "/") String newPassword,
                                       HttpServletRequest request,HttpServletResponse response) {
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

        String confirmPassword = "select * from `user` where `user_id` = ? and `password` = ?";
        List<Object> objectList = new ArrayList<>();
        objectList.add(userId);
        objectList.add(oldPassword);
        try {
            List<User> userList = db.queryInfo(confirmPassword, objectList, User.class);
            if (userList.size() == 0) {
                jsonObject.put("status", 400);  // password error
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return jsonObject;
        }

        String sql = "update `user` set `password` = ? where `user_id` = ?";
        List<Object> paramUser = new ArrayList<>();
        paramUser.add(userId);
        try {
            db.insertUpdateDeleteExute(sql,paramUser);
            jsonObject.put("status",200);
            return jsonObject;

        } catch (Exception e) {
            jsonObject.put("status", 1100);
            e.printStackTrace();
            return jsonObject;
        }
    }
}

