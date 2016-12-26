package com.NoWater.administrator;

import com.NoWater.model.Order;
import com.NoWater.model.Product;
import com.NoWater.model.Shop;
import com.NoWater.util.*;
import com.sun.org.apache.xpath.internal.operations.Or;
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
 * Created by wukai on 2016/12/18.
 */
@RestController
public class AdminOrder {

    @RequestMapping("admin/order/list")
    public JSONObject paymentList(@RequestParam(value = "count") int count,
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
            return jsonObject;
        }


        DBUtil db = new DBUtil();
        List<Object> list = new ArrayList<>();
        String sql = "select * from `order` where `order_type` in (0,3) ORDER BY `order_id` DESC";
        List<Order> orderList = db.queryInfo(sql, list, Order.class);

        if (orderList.size() == 0) {
            jsonObject.put("status", 200);
            jsonObject.put("data", "[]");
            jsonObject.put("endId", -1);
            return jsonObject;
        } else {
            int start_num = 0;
            for (int j = 0; j < orderList.size(); j++) {
                if (orderList.get(j).getOrderId() == startId) {
                    start_num = j;
                    break;
                }
            }
            if (orderList.size() - start_num > count) {
                endId = orderList.get(start_num + count).getOrderId();
                actualCount = count;
                jsonObject.put("endId", endId);
            } else {
                actualCount = orderList.size() - start_num;
                jsonObject.put("endId", -1);
            }

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < actualCount; i++) {
                JSONObject jsonObject1 = JSONObject.fromObject(orderList.get(i + start_num));
                jsonArray.add(jsonObject1);
            }

            jsonObject.put("status", 200);
            jsonObject.put("data", jsonArray);

        }
        return jsonObject;

    }

    @RequestMapping("admin/product/ad/list")
    public JSONObject adminProductAdList(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "admin_token");
        String admin = CookieUtil.confirmUser(token);

        if (admin == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[admin/product/ad/approve] %s", jsonObject.toString()));
            return jsonObject;
        }

        String getOrderSQL = "select * from `order` where `order_type` = 1 and `show_time` = ? order by `price` desc";
        List<Object> objectList = new ArrayList<>();
        objectList.add(timeUtil.getShowTime());
        Order.getProductAdOrder(getOrderSQL, objectList, jsonObject, 0, true);
        return jsonObject;
    }

    @RequestMapping("admin/shop/ad/list")
    public JSONObject adminShopAdList(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "admin_token");
        String admin = CookieUtil.confirmUser(token);

        if (admin == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[admin/product/ad/approve] %s", jsonObject.toString()));
            return jsonObject;
        }

        String getOrderSQL = "select * from `order` where `order_type` = 2 and `show_time` = ? order by `price` desc";
        List<Object> objectList = new ArrayList<>();
        objectList.add(timeUtil.getShowTime());
        jsonObject.put("status", 200);
        Order.getShopAdOrder(getOrderSQL, objectList, jsonObject, 0, true);
        return jsonObject;
    }

    @RequestMapping("admin/shop/ad/approve")
    public JSONObject adminShopAdApprove(@RequestParam(value = "orderId") int orderId,
                                         HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        DBUtil db = new DBUtil();

        String token = CookieUtil.getCookieValueByName(request, "admin_token");
        String admin = CookieUtil.confirmUser(token);

        if (admin == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[admin/shop/ad/approve] %s", jsonObject.toString()));
            return jsonObject;
        }

        if (!timeUtil.timeLimit()) {
            jsonObject.put("status", 600);
            LogHelper.info(String.format("[admin/shop/ad/approve] %s", jsonObject.toString()));
            return jsonObject;
        }

        List<Object> objectList = new ArrayList<>();
        objectList.add(orderId);
        String getOrder = "select * from `order` where `order_id` = ? and `order_type` = 2 and `status` = 1";
        List<Order> orderList;
        try {
            orderList = db.queryInfo(getOrder, objectList, Order.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }

        if (orderList.size() == 0) {
            jsonObject.put("status", 400);
            return jsonObject;
        }

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("shopId", orderList.get(0).getTargetId());
        jsonObject1.put("adPhotoUrl", orderList.get(0).getPhoto());

        OrderUtil.approvedOrder(orderId);

        Jedis jedis = new Jedis("127.0.0.1", 6379);
        String prepareShopAd = jedis.get("prepareShopAd");
        JSONArray jsonArray = new JSONArray();
        if (prepareShopAd == null) {
            jsonArray.add(jsonObject1);
        } else {
            jsonArray = JSONArray.fromObject(prepareShopAd);

            if (jsonArray.size() == 5) {
                jsonObject.put("status", 500);
                return jsonObject;
            } else {
                jsonArray.add(jsonObject1);
            }
        }
        jedis.set("prepareShopAd", jsonArray.toString());

        if (jsonArray.size() == 5) {
            OrderUtil.rejectAllOrder(2, 1, timeUtil.getShowTime());
        }

        jsonObject.put("status", 200);
        return jsonObject;
    }

    @RequestMapping("admin/product/ad/approve")
    public JSONObject adminProductAdApprove(@RequestParam(value = "orderId") int orderId,
                                            HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        DBUtil db = new DBUtil();

        String token = CookieUtil.getCookieValueByName(request, "admin_token");
        String admin = CookieUtil.confirmUser(token);

        if (admin == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[admin/product/ad/approve] %s", jsonObject.toString()));
            return jsonObject;
        }

        if (!timeUtil.timeLimit()) {
            jsonObject.put("status", 600);
            LogHelper.info(String.format("[admin/product/ad/approve] %s", jsonObject.toString()));
            return jsonObject;
        }

        List<Object> objectList = new ArrayList<>();
        objectList.add(orderId);
        String getOrder = "select * from `order` where `order_id` = ? and `order_type` = 1 and `status` = 1";
        List<Order> orderList;
        try {
            orderList = db.queryInfo(getOrder, objectList, Order.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }

        if (orderList.size() == 0) {
            jsonObject.put("status", 400);
            return jsonObject;
        }

        int productId = orderList.get(0).getProductId();

        String getProduct = "select * from `products` where `product_id` = ?";
        List<Object> objectList1 = new ArrayList<>();
        objectList1.add(productId);
        JSONObject jsonObject1 = new JSONObject();
        try {
            List<Product> productList = db.queryInfo(getProduct, objectList1, Product.class);
            jsonObject1.put("productId", productList.get(0).getProductId());
            jsonObject1.put("productName", productList.get(0).getProductName());
            jsonObject1.put("price", productList.get(0).getPrice());

        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
            return jsonObject;
        }

        Jedis jedis = new Jedis("127.0.0.1", 6379);
        String prepareProductAd = jedis.get("prepareProductAd");
        JSONArray jsonArray = new JSONArray();
        if (prepareProductAd == null) {
            jsonArray.add(jsonObject1);
        } else {
            jsonArray = JSONArray.fromObject(prepareProductAd);

            if (jsonArray.size() == 10) {
                jsonObject.put("status", 500);
                return jsonObject;
            } else {
                jsonArray.add(jsonObject1);
            }
        }

        jedis.set("prepareProductAd", jsonArray.toString());

        if (jsonArray.size() == 10) {
            OrderUtil.rejectAllOrder(1, 1, timeUtil.getShowTime());
        }

        jsonObject.put("status", 200);
        return jsonObject;
    }
}
