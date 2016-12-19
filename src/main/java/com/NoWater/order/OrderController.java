package com.NoWater.order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.NoWater.model.*;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.OrderUtil;
import net.sf.json.JSONArray;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.NoWater.util.LogHelper;

import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by wukai on 16-11-18.
 */
@RestController
public class OrderController {
    @RequestMapping("/order/prepare")
    public JSONObject orderPrepare(@RequestParam(value = "orderType", defaultValue = "3") int orderType,
                                   @RequestParam(value = "productId", defaultValue = "0") int productId,
                                   @RequestParam(value = "price", defaultValue = "0.0") float price,
                                   @RequestParam(value = "num", defaultValue = "0") int num,
                                   @RequestParam(value = "cartIdList", defaultValue = "[]") String cartIdList,
                                   HttpServletRequest request, HttpServletResponse response) {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        DBUtil db = new DBUtil();

        LogHelper.info(String.format("[order/prepare] [param] [orderType: %s, productId: %s, price: %s, num: %s, cartIdList: %s]",
                orderType, productId, price, num, cartIdList));

        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);

        if (userId == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[order/prepare] %s", jsonObject.toString()));
            return jsonObject;
        }

        // get address
        int user_id = Integer.parseInt(userId);
        String getAddressSQL = "select * from user where user_id = ?";
        List<Object> getAddressList = new ArrayList<>();
        getAddressList.add(user_id);
        List<User> userInfo;
        try {
            userInfo = db.queryInfo(getAddressSQL, getAddressList, User.class);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
            LogHelper.info(String.format("[order/prepare] %s", jsonObject.toString()));
            return jsonObject;
        }

        String pat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pat);
        String currentTime = sdf.format(new Date());

        String address = userInfo.get(0).getFirstName() + " "
                + userInfo.get(0).getLastName() + ", "
                + userInfo.get(0).getPhone() + ", "
                + userInfo.get(0).getPostCode() + ", "
                + userInfo.get(0).getAddress1() + ", "
                + userInfo.get(0).getAddress2() + ", "
                + userInfo.get(0).getAddress3();

        JSONArray orderList = new JSONArray();

        if (orderType == 3) {
            JSONArray jsonArray = JSONArray.fromObject(cartIdList);
            String getCart = "(";

            if (jsonArray.size() == 0) {
                jsonObject.put("status", 500);
                return jsonObject;
            }

            for (int i = 0; i < jsonArray.size(); i++) {
                int cartId = jsonArray.getInt(i);
                getCart += String.valueOf(cartId);
                if (i != jsonArray.size() - 1)
                    getCart += ", ";
            }
            getCart += ")";
            String getCartSQL = "select * from `cart` where `cart_id` in ? and `is_del` = 0";
            List<Object> getCartList = new ArrayList<>();
            getCartList.add(getCart);
            List<Cart> cartList;
            try {
                cartList = db.queryInfo(getCartSQL, getCartList, Cart.class);
            } catch (Exception e) {
                e.printStackTrace();
                jsonObject.put("status", 1100);
                LogHelper.info(String.format("[order/prepare] %s", jsonObject.toString()));
                return jsonObject;
            }

            if (cartList.size() != jsonArray.size()) {
                jsonObject.put("status", 400);
                LogHelper.info(String.format("[order/prepare] %s", jsonObject.toString()));
                return jsonObject;
            }

            for (int i = 0; i < cartList.size(); i++) {
                int orderId = OrderUtil.insertOrder(3, cartList.get(i).getProductId(),
                        currentTime, user_id, address, cartList.get(i).getNum());
                orderList.add(orderId);
                Jedis jedis = new Jedis("127.0.0.1", 6379);
                jedis.set("orderId:" + String.valueOf(orderId), String.valueOf(cartList.get(i).getCartId()));
            }
        } else {
            int orderId = OrderUtil.insertOrder(orderType, productId, currentTime, user_id, address, num);
            orderList.add(orderId);
        }
        jsonObject.put("status", 200);
        jsonObject.put("orderIdList", orderList);
        LogHelper.info(String.format("[order/prepare] %s", jsonObject.toString()));
        return jsonObject;
    }

    @RequestMapping("order/confirm")
    public JSONObject orderConfirm(@RequestParam(value = "orderIdList") String orderIdList,
                                   HttpServletRequest request, HttpServletResponse response) {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        DBUtil db = new DBUtil();

        LogHelper.info(String.format("[order/confirm] [param] [orderIdList: %s]", orderIdList));

        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);

        if (userId == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[order/confirm] %s", jsonObject.toString()));
            return jsonObject;
        }

        JSONArray jsonArray = JSONArray.fromObject(orderIdList);
        for (int i = 0; i < jsonArray.size(); i++) {
            int orderId = jsonArray.getInt(i);

            int status = OrderUtil.confirmOrderUserId(orderId, userId, -3);

            if (status != 200) {
                jsonObject.put("status", 200);
                LogHelper.info(String.format("[order/confirm] %s", jsonObject.toString()));
                return jsonObject;
            }

            Jedis jedis = new Jedis("127.0.0.1", 6379);
            String cartId = jedis.get("orderId:" + String.valueOf(orderId));

            if (cartId != null) {
                String deleteCartSQL = "update `cart` set `is_del` = 1 where `cart_id` = ?";
                List<Object> deleteCartList = new ArrayList<>();
                deleteCartList.add(Integer.parseInt(cartId));
                db.insertUpdateDeleteExute(deleteCartSQL, deleteCartList);
            }

            List<Object> getConfirmOrderId = new ArrayList<>();
            getConfirmOrderId.add(orderId);

            String updateConfirm = "update `order` set `status` = 1 where `order_id` = ?";
            db.insertUpdateDeleteExute(updateConfirm, getConfirmOrderId);

            jsonObject.put("status", 200);
            LogHelper.info(String.format("[order/confirm] %s", jsonObject.toString()));
        }
        return jsonObject;
    }

    @RequestMapping("order/price")
    public JSONObject orderPrice(@RequestParam(value = "orderIdList") String orderIdList,
                                 HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        DBUtil db = new DBUtil();

        LogHelper.info(String.format("[order/confirm] [param] [orderIdList: %s]", orderIdList));

        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);

        if (userId == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[order/price] %s", jsonObject.toString()));
            return jsonObject;
        }

        JSONArray jsonArray = JSONArray.fromObject(orderIdList);
        for (int i = 0; i < jsonArray.size(); i++) {
            int orderId = jsonArray.getInt(i);

            int status = OrderUtil.confirmOrderUserId(orderId, userId, 1);

            if (status != 200) {
                jsonObject.put("status", status);
                LogHelper.info(String.format("[order/price] %s", jsonObject.toString()));
                return jsonObject;
            }
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            int orderId = jsonArray.getInt(i);

            String updatePaymentSQL = "update `order` set `status` = 2 where `order_id` = ?";
            List<Object> updateList = new ArrayList<>();
            updateList.add(orderId);
            db.insertUpdateDeleteExute(updatePaymentSQL, updateList);
        }
        jsonObject.put("stauts", 200);
        LogHelper.info(String.format("[order/price] %s", jsonObject.toString()));
        return jsonObject;
    }

    @RequestMapping("order/detail")
    public JSONObject orderDetail(@RequestParam(value = "orderIdList") String orderIdList,
                                  @RequestParam(value = "status") int status,
                                  HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        DBUtil db = new DBUtil();

        LogHelper.info(String.format("[order/detail] [param] [orderIdList: %s]", orderIdList));

        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);

        if (userId == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[order/detail] %s", jsonObject.toString()));
            return jsonObject;
        }

        JSONArray jsonArray = JSONArray.fromObject(orderIdList);
        for (int i = 0; i < jsonArray.size(); i++) {
            int orderId = jsonArray.getInt(i);

            int statusConfirmOrder = OrderUtil.confirmOrderUserId(orderId, userId, status);

            if (statusConfirmOrder != 200) {
                jsonObject.put("status", statusConfirmOrder);
                LogHelper.info(String.format("[order/detail] %s", jsonObject.toString()));
                return jsonObject;
            }
        }

        String getOrderDetailSQL = "select * from `order` where `order_id` in ? and ``";
        List<Object> getOrderDetailList = new ArrayList<>();
        getOrderDetailList.add(orderIdList);

        JSONArray orderDetail = OrderUtil.getOrderDetail(getOrderDetailSQL, getOrderDetailList);

        jsonObject.put("data", orderDetail);
        jsonObject.put("status", 200);
        return jsonObject;
    }

    @RequestMapping("order/list")
    public JSONObject orderList(@RequestParam(value = "status") int status,
                                HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        DBUtil db = new DBUtil();

        LogHelper.info(String.format("[order/list] [param] [status: %s]", status));

        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);

        if (userId == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[order/list] %s", jsonObject.toString()));
            return jsonObject;
        }

        String getOrderList = "select * from `order` where `initiator_id` = ? and `status` = ?";
        List<Object> list = new ArrayList<>();
        list.add(userId);
        list.add(status);

        JSONArray jsonArray = OrderUtil.getOrderDetail(getOrderList, list);
        jsonObject.put("status", 200);
        jsonObject.put("data", jsonArray);
        return jsonObject;
    }

}
