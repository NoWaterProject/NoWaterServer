package com.NoWater.shopOwner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.NoWater.model.Admin;
import com.NoWater.model.Shop;
import com.NoWater.model.Status;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wukai on 2016/11/21.
 */
@RestController
public class ShopOwnerApply {
    @RequestMapping("/shop-owner/status")
    public JSONObject status(HttpServletRequest request, HttpServletResponse response) throws Exception{

        String token = CookieUtil.getCookieValueByName(request, "token");
        JSONObject jsonObject = new JSONObject();

        if (token == null) {
            jsonObject.put("status", 300);
        } else {
            Jedis jedis = new Jedis("127.0.0.1", 6379);
            String user_id = jedis.get("token");
            if (user_id == null) {
                jsonObject.put("status", 300);
            } else {
                String realToken = jedis.get(user_id);
                if (realToken.equals(token)) {
                    List<Object> list = new ArrayList<Object>();
                    DBUtil db = new DBUtil();
                    String sql = "select Status from shop where owner_id = " + user_id;
                    List<Shop> shopList = db.queryInfo(sql, list, Shop.class);

                    if (!shopList.isEmpty()){
                        int status = shopList.get(0).getStatus();
                        if (status == 0) {
                            jsonObject.put("status", 500);  //正在审查
                        } else if (status == 1) {
                            jsonObject.put("status", 200);  //已成为卖家
                        }
                    }else {
                        jsonObject.put("status",400);//数据库中找不到卖家，用户未申请
                    }

                } else {
                    jsonObject.put("status", 300);
                }
            }
        }

        return jsonObject;
    }

    @RequestMapping("/shop-owner/apply")
    public JSONObject apply(@RequestParam(value = "email", defaultValue = "/") String email,
                            @RequestParam(value = "shopName", defaultValue = "/") String shopName,
                            @RequestParam(value = "telephone", defaultValue = "/") String telephone,
                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        String token = CookieUtil.getCookieValueByName(request, "token");
        JSONObject jsonObject = new JSONObject();

        if (token == null) {
            jsonObject.put("status", 300);
        } else {
            Jedis jedis = new Jedis("127.0.0.1", 6379);
            String user_id = jedis.get("token");
            if (user_id == null) {
                jsonObject.put("status", 300);//未登陆
            } else {
                String realToken = jedis.get(user_id);
                if (realToken.equals(token)) {
                    List<Object> list = new ArrayList<Object>();
                    DBUtil db = new DBUtil();
                    String sql = "select Status from shop where owner_id = " + user_id;
                    List<Shop> shopList = db.queryInfo(sql, list, Shop.class);

                    if (!shopList.isEmpty()) {
                        int status = shopList.get(0).getStatus();
                        if (status == 0) {
                            jsonObject.put("status", 500);  //正在审查
                        } else if (status == 1) {
                            jsonObject.put("status", 200);  //已成为卖家
                        }
                    } else {
                        sql = "select * from shop where shop_name = " + shopName;
                        shopList = db.queryInfo(sql, list, Shop.class);

                        if (!shopList.isEmpty()) {
                            jsonObject.put("status", 600);   //店名已有人使用
                        } else {
                            int shop_status = 0;        //插入申请，正在成为卖家

                            List<Object> list1 = new ArrayList<Object>();
                            DBUtil db1 = new DBUtil();
                            StringBuffer sqlBuffer = new StringBuffer();
                            sqlBuffer.append("insert into shop (shop_name,owner_id,email,status,telephone) values (?,?,?,?,?)");
                            list1.add(shopName);
                            list1.add(user_id);
                            list1.add(email);
                            list1.add(shop_status);
                            list1.add(telephone);
                            db1.insertUpdateDeleteExute(sqlBuffer.toString(), list1);

                            jsonObject.put("status", 200);
                        }

                    }
                } else {
                    jsonObject.put("status", 300);
                }

            }
        }

        return jsonObject;
    }
}
