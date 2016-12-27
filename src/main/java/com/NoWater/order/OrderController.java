package com.NoWater.order;

import com.NoWater.model.*;
import com.NoWater.util.*;
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


/**
 * Created by wukai on 16-11-18.
 */
@RestController
public class OrderController {
    @RequestMapping("/order/prepare")
    public JSONObject orderPrepare(@RequestParam(value = "orderType", defaultValue = "3") int orderType,
                                   @RequestParam(value = "productId", defaultValue = "0") int productId,
                                   @RequestParam(value = "num", defaultValue = "0") int num,
                                   @RequestParam(value = "cartIdList", defaultValue = "[]") String cartIdList,
                                   HttpServletRequest request, HttpServletResponse response) {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        DBUtil db = new DBUtil();

        LogHelper.info(String.format("[order/prepare] [param] [orderType: %s, productId: %s, num: %s, cartIdList: %s]",
                orderType, productId, num, cartIdList));

        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);

        if (userId == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[order/prepare] %s", jsonObject.toString()));
            return jsonObject;
        }

        if (orderType != 3 && orderType != 0) {
            jsonObject.put("status", 1300);
            return jsonObject;
        }

        String pat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pat);
        String currentTime = sdf.format(new Date());

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
            String getCartSQL = "select * from `cart` where `cart_id` in " + getCart + " and `is_del` = 0";
            List<Object> getCartList = new ArrayList<>();
            List<Cart> cartList;
            LogHelper.info("getCartSQL: " + getCartSQL);
            try {
                cartList = db.queryInfo(getCartSQL, getCartList, Cart.class);
            } catch (Exception e) {
                e.printStackTrace();
                jsonObject.put("status", 1100);
                LogHelper.info(String.format("[order/prepare] %s", jsonObject.toString()));
                return jsonObject;
            }

            // cartList error
            if (cartList.size() != jsonArray.size()) {
                jsonObject.put("status", 400);
                LogHelper.info(String.format("[order/prepare] %s", jsonObject.toString()));
                return jsonObject;
            }

            for (int i = 0; i < cartList.size(); i++) {
                int confirmStock = Product.confirmStock(cartList.get(i).getNum(), cartList.get(i).getProductId());
                if (confirmStock != 200) {
                    jsonObject.put("status", confirmStock);
                    LogHelper.info(String.format("[order/prepare] %s", jsonObject.toString()));
                    return jsonObject;
                }
            }

            for (int i = 0; i < cartList.size(); i++) {
                int orderId = OrderUtil.insertOrder(3, cartList.get(i).getProductId(),
                        currentTime, Integer.parseInt(userId), cartList.get(i).getNum());
                orderList.add(orderId);
                Jedis jedis = new Jedis("127.0.0.1", 6379);
                jedis.set("orderId:" + String.valueOf(orderId), String.valueOf(cartList.get(i).getCartId()));
            }
        } else {
            int confirmStock = Product.confirmStock(num, productId);

            if (confirmStock != 200) {
                jsonObject.put("status", confirmStock);
                LogHelper.info(String.format("[order/prepare] %s", jsonObject.toString()));
                return jsonObject;
            }

            int orderId = OrderUtil.insertOrder(0, productId, currentTime, Integer.parseInt(userId), num);
            orderList.add(orderId);
        }
        jsonObject.put("status", 200);
        jsonObject.put("orderIdList", orderList);
        LogHelper.info(String.format("[order/prepare] %s", jsonObject.toString()));
        return jsonObject;
    }

    @RequestMapping("order/confirm")
    public JSONObject orderConfirm(@RequestParam(value = "orderIdList") String orderIdList,
                                   @RequestParam(value = "addressId", defaultValue = "0") int addressId,
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
        ArrayList<Integer> productList = new ArrayList<>();
        ArrayList<Integer> numList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            int orderId = jsonArray.getInt(i);

            int status = OrderUtil.confirmOrderUserId(orderId, userId, -3, 1);

            if (status != 200) {
                jsonObject.put("status", status);
                LogHelper.info(String.format("[order/confirm] %s", jsonObject.toString()));
                return jsonObject;
            }

            String getProductId = "select * from `order` where `order_id` = ?";
            List<Object> list = new ArrayList<>();
            list.add(orderId);
            List<Order> getOrderItem;
            try {
                getOrderItem = db.queryInfo(getProductId, list, Order.class);
            } catch (Exception e) {
                e.printStackTrace();
                jsonObject.put("status", 1100);
                return jsonObject;
            }

            productList.add(getOrderItem.get(0).getProductId());
            numList.add(getOrderItem.get(0).getNum());

            // 检查库存
            int confirmStock = Product.confirmStock(getOrderItem.get(0).getNum(), getOrderItem.get(0).getProductId());
            if (confirmStock != 200) {
                jsonObject.put("status", confirmStock);
                LogHelper.info(String.format("[order/confirm] %s", jsonObject.toString()));
                return jsonObject;
            }
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            int orderId = jsonArray.getInt(i);
            Jedis jedis = new Jedis("127.0.0.1", 6379);
            String cartId = jedis.get("orderId:" + String.valueOf(orderId));

            if (cartId != null) {
                String deleteCartSQL = "update `cart` set `is_del` = 1 where `cart_id` = ?";
                List<Object> deleteCartList = new ArrayList<>();
                deleteCartList.add(Integer.parseInt(cartId));
                db.insertUpdateDeleteExute(deleteCartSQL, deleteCartList);
            }

            Product.decreaseStock(numList.get(i), productList.get(i));

            String pat = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(pat);
            String currentTime = sdf.format(new Date());

            List<Object> getConfirmOrderId = new ArrayList<>();
            getConfirmOrderId.add(currentTime);
            getConfirmOrderId.add(orderId);

            if (addressId != 0) {
                String getAddressSQL = "select * from `address` where `address_id` = ?";
                try {
                    List<Object> objectList = new ArrayList<>();
                    objectList.add(addressId);

                    List<Address> addressList = db.queryInfo(getAddressSQL, objectList, Address.class);
                    String address = addressList.get(0).getFirstName() + " " + addressList.get(0).getLastName() + ", "
                            + addressList.get(0).getTelephone() + ", " + addressList.get(0).getPostCode() + ", "
                            + addressList.get(0).getAddress1() + " " + addressList.get(0).getAddress2() + " "
                            + addressList.get(0).getAddress3();
                    getConfirmOrderId.add(address);
                } catch (Exception e) {
                    jsonObject.put("status", 1100);
                    return jsonObject;
                }
            } else {
                getConfirmOrderId.add("");
            }

            String updateConfirm = "update `order` set `status` = 1, `time` = ?, `address` = ? where `order_id` = ?";
            db.insertUpdateDeleteExute(updateConfirm, getConfirmOrderId);
        }

        jsonObject.put("status", 200);
        LogHelper.info(String.format("[order/confirm] %s", jsonObject.toString()));

        return jsonObject;
    }

    @RequestMapping("order/cancel")
    public JSONObject orderCancel(@RequestParam(value = "orderId") int orderId,
                                  HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        DBUtil db = new DBUtil();

        LogHelper.info(String.format("[order/cancel] [param] [orderId: %s]", orderId));

        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);

        if (userId == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[order/cancel] %s", jsonObject.toString()));
            return jsonObject;
        }

        int confirmOrder = OrderUtil.confirmOrderUserId(orderId, userId, 1, 1);

        if (confirmOrder != 200) {
            jsonObject.put("status", confirmOrder);
            LogHelper.info(String.format("[order/cancel] %s", jsonObject.toString()));
            return jsonObject;
        }

        String getProductId = "select * from `order` where `order_id` = ?";
        List<Object> list = new ArrayList<>();
        list.add(orderId);
        try {
            List<Order> getOrderItem = db.queryInfo(getProductId, list, Order.class);
            int num = getOrderItem.get(0).getNum();
            int productId = getOrderItem.get(0).getProductId();
            Product.increaseStock(num, productId);

            List<Object> cancelList = new ArrayList<>();
            cancelList.add(orderId);
            String cancelOrderSQL = "update `order` set `status` = 10 where `order_id` = ?";
            db.insertUpdateDeleteExute(cancelOrderSQL, cancelList);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
            return jsonObject;
        }
        jsonObject.put("status", 200);
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

            int status = OrderUtil.confirmOrderUserId(orderId, userId, 1, 1);

            if (status != 200) {
                jsonObject.put("status", status);
                LogHelper.info(String.format("[order/price] %s", jsonObject.toString()));
                return jsonObject;
            }
        }

        String pat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pat);
        String currentTime = sdf.format(new Date());

        for (int i = 0; i < jsonArray.size(); i++) {
            int orderId = jsonArray.getInt(i);

            String updatePaymentSQL = "update `order` set `status` = 2, `time` = ? where `order_id` = ?";
            List<Object> updateList = new ArrayList<>();
            updateList.add(currentTime);
            updateList.add(orderId);
            db.insertUpdateDeleteExute(updatePaymentSQL, updateList);
        }
        jsonObject.put("status", 200);
        LogHelper.info(String.format("[order/price] %s", jsonObject.toString()));
        return jsonObject;
    }

    @RequestMapping("order/detail")
    public JSONObject orderDetail(@RequestParam(value = "orderIdList") String orderIdList,
                                  HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        LogHelper.info(String.format("[order/detail] [param] [orderIdList: %s]", orderIdList));

        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);

        if (userId == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[order/detail] %s", jsonObject.toString()));
            return jsonObject;
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("( ");

        JSONArray jsonArray = JSONArray.fromObject(orderIdList);
        for (int i = 0; i < jsonArray.size(); i++) {
            int orderId = jsonArray.getInt(i);

            stringBuffer.append(orderId);
            if (i != jsonArray.size() - 1) {
                stringBuffer.append(", ");
            }

            int statusConfirmOrder = OrderUtil.confirmOrderUserId(orderId, userId, 0, 1);

            if (statusConfirmOrder != 200) {
                jsonObject.put("status", statusConfirmOrder);
                LogHelper.info(String.format("[order/detail] %s", jsonObject.toString()));
                return jsonObject;
            }
        }
        stringBuffer.append(")");

        String getOrderDetailSQL = "select * from `order` where `order_id` in " + stringBuffer.toString();
        List<Object> getOrderDetailList = new ArrayList<>();
        JSONArray orderDetail;
        try {
            orderDetail = OrderUtil.getOrderDetail(getOrderDetailSQL, getOrderDetailList, 0, "", "", false, 0);
        } catch (Exception e) {
            jsonObject.put("status", 1100);
            return jsonObject;
        }

        jsonObject.put("data", orderDetail);
        jsonObject.put("status", 200);
        LogHelper.info(String.format("[order/detail] %s", jsonObject.toString()));
        return jsonObject;
    }

    @RequestMapping("order/list")
    public JSONObject orderList(@RequestParam(value = "status") int status,
                                @RequestParam(value = "timeFilter", defaultValue = "0") int timeFilter,
                                @RequestParam(value = "beginTime", defaultValue = "1970-01-01 00:00:00") String beginTime,
                                @RequestParam(value = "endTime", defaultValue = "-1") String endTime,
                                @RequestParam(value = "searchKey", defaultValue = "") String searchKey,
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

        StringBuffer getOrderList = new StringBuffer("select * from `order` where");
        List<Object> list = new ArrayList<>();
        if (!searchKey.isEmpty()) {
            String searchResult = OrderUtil.searchProduct(searchKey);
            getOrderList.append(searchResult);
            list.add(searchKey);
        }

        if (status == 0) {
            getOrderList.append(" `order_type` in (0, 3) and `initiator_id` = ? and `status` != -3 order by `status`");
            list.add(userId);
        } else {
            getOrderList.append(" `order_type` in (0, 3) and `initiator_id` = ? and `status` = ?");
            list.add(userId);
            list.add(status);
        }

        if (endTime == "-1") {
            endTime = timeUtil.getShowTime() + " 23:59:59";
        }

        try {
            JSONArray jsonArray = OrderUtil.getOrderDetail(getOrderList.toString(), list, timeFilter, beginTime, endTime, false, 0);
            jsonObject.put("status", 200);
            jsonObject.put("data", jsonArray);
            LogHelper.info(String.format("[order/list] %s", jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
        }
        return jsonObject;
    }

    @RequestMapping("order/confirm/receipt")
    public JSONObject orderConfirmReceipt(@RequestParam(value = "orderId") int orderId,
                                          HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        DBUtil db = new DBUtil();

        LogHelper.info(String.format("[order/confirm/receipt] [param] [orderId: %s]", orderId));

        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);

        if (userId == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[order/confirm/receipt] %s", jsonObject.toString()));
            return jsonObject;
        }

        int statusConfirmOrder = OrderUtil.confirmOrderUserId(orderId, userId, 3, 1);

        if (statusConfirmOrder != 200) {
            jsonObject.put("status", statusConfirmOrder);
            LogHelper.info(String.format("[order/confirm/receipt] %s", jsonObject.toString()));
            return jsonObject;
        }

        String updateSQL = "update `order` set `status` = 4 where `order_id` = ?";
        List<Object> list = new ArrayList<>();
        list.add(orderId);
        db.insertUpdateDeleteExute(updateSQL, list);

        jsonObject.put("status", 200);
        LogHelper.info(String.format("[order/confirm/receipt] %s", jsonObject.toString()));
        return jsonObject;
    }

    @RequestMapping("order/comment")
    public JSONObject orderComment(@RequestParam(value = "orderId") int orderId,
                                   @RequestParam(value = "comment") String comment,
                                   @RequestParam(value = "star") int star,
                                   HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        DBUtil db = new DBUtil();

        LogHelper.info(String.format("[order/comment] [param] [orderId: %s]", orderId));

        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);

        if (userId == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[order/comment] %s", jsonObject.toString()));
            return jsonObject;
        }

        int statusConfirmOrder = OrderUtil.confirmOrderUserId(orderId, userId, 4, 1);

        if (statusConfirmOrder != 200) {
            jsonObject.put("status", statusConfirmOrder);
            LogHelper.info(String.format("[order/comment] %s", jsonObject.toString()));
            return jsonObject;
        }

        String sql = "select * from `user` where user user_id = ?";
        List<Object> list = new ArrayList<>();
        list.add(Integer.parseInt(userId));
        List<User> user;
        try {
            user = db.queryInfo(sql, list, User.class);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
            return jsonObject;
        }
        String userName = user.get(0).getName().substring(0, 1) + "*****";

        sql="select * from `order` where `order_id`=?";
        list.clear();
        list.add(orderId);
        List<Order> order;
        try {
            order = db.queryInfo(sql, list, Order.class);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("OrderStatus", 1100);
            return jsonObject;
        }
        int productId = order.get(0).getProductId();

        String pat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pat);
        String currentTime = sdf.format(new Date());

        sql = "insert into `comment_product` (`comment_content`, `user_id`, `user_name`, `order_id`, `product_id`, `time`, `star`) values(?, ?, ?, ?, ?, ?, ?)";
        list.clear();
        list.add(comment);
        list.add(userId);
        list.add(userName);
        list.add(orderId);
        list.add(productId);
        list.add(currentTime);
        list.add(star);
        db.insertUpdateDeleteExute(sql, list);
        jsonObject.put("status", 200);
        LogHelper.info(String.format("[order/comment] %s", jsonObject.toString()));

        return jsonObject;
    }
}
