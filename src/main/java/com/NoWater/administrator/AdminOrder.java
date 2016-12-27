package com.NoWater.administrator;

import com.NoWater.model.Order;
import com.NoWater.model.Photo;
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
                                  @RequestParam(value = "timeFilter", defaultValue = "0") int timeFilter,
                                  @RequestParam(value = "beginTime", defaultValue = "1970-01-01 00:00:00") String beginTime,
                                  @RequestParam(value = "endTime", defaultValue = "-1") String endTime,
                                  @RequestParam(value = "searchKey", defaultValue = "") String searchKey,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "admin_token");
        String admin = CookieUtil.confirmUser(token);

        if (admin == null) {
            jsonObject.put("status", 300);
            return jsonObject;
        }

        if (endTime == "-1") {
            endTime = timeUtil.getShowTime() + " 23:59:59";
        }

        StringBuffer getOrderList = new StringBuffer("select * from `order` where");
        List<Object> getOrderDetailList = new ArrayList<>();
        if (!searchKey.isEmpty()) {
            String searchResult = OrderUtil.searchProduct(searchKey);
            getOrderList.append(searchResult);
            getOrderDetailList.add(searchKey);
        }

        if (startId != 0) {
            getOrderList.append(" `order_id` <= ? and ");
            getOrderDetailList.add(startId);
        }

        getOrderList.append(" `order_type` in (0, 3) order by `order_id` desc");

        try {
            JSONArray jsonArray = OrderUtil.getOrderDetail(getOrderList.toString(), getOrderDetailList, timeFilter, beginTime, endTime, true, count);
            if (jsonArray.size() == count + 1) {
                jsonObject.put("startId", jsonArray.getJSONObject(count).get("order_id"));
                jsonArray.remove(count);
            } else {
                jsonObject.put("startId", -1);
            }

            jsonObject.put("status", 200);
            jsonObject.put("data", jsonArray);
            LogHelper.info(String.format("[admin/order/list] %s", jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
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
        String getOrder = "select * from `order` where `order_id` = ? and `order_type` = 2 and `status` = 2";
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
        String getOrder = "select * from `order` where `order_id` = ? and `order_type` = 1 and `status` = 2";
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

            String getPhotoSQL = "select * from photo where belong_id = ? and photo_type = ? and is_del = 0";

            ArrayList<String> stringArrayList = Photo.getPhotoURL(getPhotoSQL, productId, 2);

            jsonObject1.put("photoIdUrl", stringArrayList.get(0));
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
