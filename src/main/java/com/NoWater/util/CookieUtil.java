package com.NoWater.util;

import com.NoWater.model.Shop;
import redis.clients.jedis.Jedis;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 李鹏飞 on 2016/11/27 0027.
 */
public class CookieUtil {
    /**
     * 根据名字获取cookie
     *
     * @param request
     * @param name    cookie名字
     * @return
     */
    public static String getCookieValueByName(HttpServletRequest request, String name) {
        Map<String, Cookie> cookieMap = ReadCookieMap(request);
        if (cookieMap.containsKey(name)) {
            String value = cookieMap.get(name).getValue();
            return value;
        } else {
            return null;
        }
    }

    /**
     * 将cookie封装到Map里面
     *
     * @param request
     * @return
     */
    private static Map<String, Cookie> ReadCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }

    public static String confirmUser(String uuid) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        if (uuid != null) {
            String userId = jedis.get(uuid);
            if (userId == null) {
                return null;
            } else {
                String realToken = jedis.get(userId);
                if (uuid.equals(realToken)) {
                    return userId;
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    public static int confirmShop(String userId) {
        DBUtil db = new DBUtil();
        String getShopIdSQL = "select * from `shop` where `owner_id` = ? and `status` = 1";
        List<Object> list = new ArrayList<>();
        list.add(Integer.parseInt(userId));
        List<Shop> getShop;
        try {
            getShop = db.queryInfo(getShopIdSQL, list, Shop.class);
            if (getShop.size() == 0) {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -2;
        }

        return getShop.get(0).getShopId();
    }
}
