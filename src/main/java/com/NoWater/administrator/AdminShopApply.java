package com.NoWater.administrator;

import com.NoWater.model.Admin;
import com.NoWater.model.Shop;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.LogHelper;
import net.sf.json.JSONArray;
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
 * Created by wukai on 2016/11/29.
 */
@RestController
public class AdminShopApply {

    @RequestMapping("/admin/shop/applyList")
    public JSONObject adminShopApplyList(HttpServletRequest request, HttpServletResponse response) {
        LogHelper.info("shop apply list");
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "admin_token");

        if (token != null) {
            Jedis jedis = new Jedis("127.0.0.1", 6379);
            String name = jedis.get(token);
            String realToken = jedis.get(name);

            if (token.equals(realToken)) {
                // 获取管理员用户名
                List<Object> list = new ArrayList<Object>();
                DBUtil db = new DBUtil();
                String sql = "select * from admin";
                try {
                    List<Admin> adminList = db.queryInfo(sql, list, Admin.class);
                    // 确认用户名
                    if (adminList.get(0).getName().equals(name)) {

                        // 核心start
                        String getShopApply = "select * from `shop` where `status` = 0";
                        try {
                            List<Shop> shopApplyList = db.queryInfo(getShopApply, list, Shop.class);

                            jsonObject.put("status", 200);
                            if (shopApplyList.size() == 0) {
                                jsonObject.put("data", "{}");
                            } else {
                                jsonObject.put("data", JSONArray.fromObject(shopApplyList));
                            }
                        } catch (Exception e) {
                            LogHelper.error(e.toString());
                            jsonObject.put("status", 400);
                        }
                        // 核心end

                    } else {
                        jsonObject.put("status", 300);
                    }
                } catch (Exception e) {
                    LogHelper.error(e.toString());
                    jsonObject.put("status", 400);  // server error
                }
            } else {
                jsonObject.put("status", 300);
            }
        } else {
            jsonObject.put("status", 300);
        }

        return jsonObject;
    }

    @RequestMapping("/admin/shop/handle")  // 0为未处理，1为accepted，-1为rejected
    public JSONObject adminShopHandle(@RequestParam(value = "shopId", defaultValue = "/") int shopId,
                                      @RequestParam(value = "behavior", defaultValue = "/") int behavior,
                                      HttpServletRequest request, HttpServletResponse response) {
        LogHelper.info(String.format("[admin/shop/handle] [param] shopId:%s, behavior:%s", shopId, behavior));
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "admin_token");

        if (token != null) {
            Jedis jedis = new Jedis("127.0.0.1", 6379);
            String name = jedis.get(token);
            String realToken = jedis.get(name);

            if (token.equals(realToken)) {
                // 获取管理员用户名
                List<Object> list = new ArrayList<Object>();
                DBUtil db = new DBUtil();
                String sql = "select * from admin";
                try {
                    List<Admin> adminList = db.queryInfo(sql, list, Admin.class);
                    // 确认用户名
                    if (adminList.get(0).getName().equals(name)) {

                        String confirmSQL = "select `status` from `shop` where `shop_id` = ?";
                        list.add(shopId);
                        List<Shop> hasShopId = db.queryInfo(confirmSQL, list, Shop.class);

                        if (hasShopId.size() == 0) {
                            jsonObject.put("status", 500);  // don't have this shopId
                        } else if (hasShopId.get(0).getStatus() == 1 || hasShopId.get(0).getStatus() == -1) {
                            jsonObject.put("status", 600);  // shopId has handle
                        } else {
                            String handleSQL = "update `shop` set `status` = ? where `shop_id` = ?";
                            List<Object> listHandle = new ArrayList<>();
                            listHandle.add(behavior);
                            listHandle.add(shopId);
                            db.insertUpdateDeleteExute(handleSQL, listHandle);

                            jsonObject.put("status", 200);
                        }

                    } else {
                        jsonObject.put("status", 300);
                    }
                } catch (Exception e) {
                    LogHelper.error(e.toString());
                    jsonObject.put("status", 400);  // server error
                }
            } else {
                jsonObject.put("status", 300);
            }
        } else {
            jsonObject.put("status", 300);
        }

        return jsonObject;
    }
}
