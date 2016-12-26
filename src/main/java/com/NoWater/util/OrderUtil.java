package com.NoWater.util;

import com.NoWater.model.Order;
import com.NoWater.model.Product;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.EnumMorpher;
import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Koprvhdix on 2016/12/18.
 */
public final class OrderUtil {
    public static int insertOrder(int order_type, int productId, String time, int user_id, int num) {
        DBUtil db = new DBUtil();

        String getProductSQL = "select * from products where product_id = ?";
        List<Object> getProductList = new ArrayList<>();
        getProductList.add(productId);
        List<Product> ProductItem;
        try {
            ProductItem = db.queryInfo(getProductSQL, getProductList, Product.class);
        } catch (Exception e) {
            e.printStackTrace();
            return -10;
        }
        Jedis jedis = new Jedis("127.0.0.1", 6379);

        double price = ProductItem.get(0).getPrice();

        String insertOrderSQL = "insert into `order` (`order_type`, `product_id`, `time`, `initiator_id`, `target_id`, `num`, `price`, `sum_price`, `commission`) values (?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object> insertList = new ArrayList<>();
        insertList.add(order_type);
        insertList.add(productId);
        insertList.add(time);
        insertList.add(user_id);
        insertList.add(ProductItem.get(0).getShopId());
        insertList.add(num);
        insertList.add(price);
        insertList.add(num * price);
        insertList.add(Double.valueOf(jedis.get("commission")));

        db.insertUpdateDeleteExute(insertOrderSQL, insertList);

        String getOrderIdSQL = "select `order_id` from `order` where `order_type` = ? and `initiator_id` = ? and `time` = ? and `product_id` = ?";
        List<Object> orderIdList = new ArrayList<>();
        orderIdList.add(order_type);
        orderIdList.add(user_id);
        orderIdList.add(time);
        orderIdList.add(productId);
        List<Order> orderItem;
        try {
            orderItem = db.queryInfo(getOrderIdSQL, orderIdList, Order.class);
        } catch (Exception e) {
            e.printStackTrace();
            return -10;
        }

        return orderItem.get(0).getOrderId();
    }

    public static int confirmOrderUserId(int orderId, String userId, int status, int userType) {
        List<Object> getConfirmOrderId = new ArrayList<>();
        String confirmOrderId;
        if (status != 0) {
            confirmOrderId = "select * from `order` where `order_id` = ? and `status` = ?";
            getConfirmOrderId.add(orderId);
            getConfirmOrderId.add(status);
        } else {
            confirmOrderId = "select * from `order` where `order_id` = ?";
            getConfirmOrderId.add(orderId);
        }

        DBUtil db = new DBUtil();

        List<Order> confirmOrder;
        try {
            confirmOrder = db.queryInfo(confirmOrderId, getConfirmOrderId, Order.class);
        } catch (Exception e) {
            e.printStackTrace();
            return 1100;
        }

        if (confirmOrder.size() == 0) {
            return 400;
        }

        if (userType == 1 && confirmOrder.get(0).getInitiatorId() != Integer.parseInt(userId)) {
            return 600;
        } else if (userType == 2 && confirmOrder.get(0).getTargetId() != Integer.parseInt(userId)) {
            return 600;
        }

        return 200;
    }

    public static JSONArray getOrderDetail(String getOrderDetailSQL, List<Object> getOrderDetailList, int timeFilter,
                                           String beginTime, String endTime) throws Exception {
        DBUtil db = new DBUtil();
        List<Order> orderDetail;
        JSONArray jsonArray = new JSONArray();
        try {
            orderDetail = db.queryInfo(getOrderDetailSQL, getOrderDetailList, Order.class);
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", 1100);
            jsonArray.add(jsonObject);
            return jsonArray;
        }


        for (int i = 0; i < orderDetail.size(); i++) {
            JSONObject orderItem = new JSONObject();

            //获取订单时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String getTime = orderDetail.get(i).getTime();

            Date orderTime = sdf.parse(getTime);
            Calendar orderDate = Calendar.getInstance();
            orderDate.setTime(orderTime);

            Calendar nowDate = Calendar.getInstance();

            if (timeFilter == 0) {//0 不处理  1 每天 ； 2 每周 ； 3 每月 ；4 每年 ; 5处理时间段
                orderItem = JSONObject.fromObject(orderDetail.get(i));
            } else if (timeFilter == 1) {
                nowDate.add(Calendar.DATE, -1);
                if (orderDate.before(nowDate)) {
                    continue;
                } else {
                    orderItem = JSONObject.fromObject(orderDetail.get(i));
                }

            } else if (timeFilter == 2) {
                nowDate.add(Calendar.DATE, -7);
                if (orderDate.before(nowDate)) {
                    continue;
                } else {
                    orderItem = JSONObject.fromObject(orderDetail.get(i));
                }

            } else if (timeFilter == 3) {
                nowDate.add(Calendar.MONTH, -1);
                if (orderDate.before(nowDate)) {
                    continue;
                } else {
                    orderItem = JSONObject.fromObject(orderDetail.get(i));
                }

            } else if (timeFilter == 4) {
                nowDate.add(Calendar.YEAR, -1);
                if (orderDate.before(nowDate)) {
                    continue;
                } else {
                    orderItem = JSONObject.fromObject(orderDetail.get(i));
                }

            } else if (timeFilter == 5) {
                Date begin = sdf.parse(beginTime);
                Calendar beginDate = Calendar.getInstance();
                beginDate.setTime(begin);
                Date end = sdf.parse(endTime);
                Calendar endDate = Calendar.getInstance();
                endDate.setTime(end);

                if (orderDate.before(beginDate) || orderDate.after(endDate)) {
                    continue;
                } else {
                    orderItem = JSONObject.fromObject(orderDetail.get(i));
                }
            }

            if (orderDetail.get(i).getStatus() == 1)
                orderItem.put("countdown", timeUtil.timeCountdown(orderDetail.get(i).getTime(), 1));
            else
                orderItem.put("countdown", timeUtil.timeCountdown(orderDetail.get(i).getTime(), 7));
            int productId = orderDetail.get(i).getProductId();
            int shopId = orderDetail.get(i).getTargetId();
            JSONObject product = ProductShopUtil.GetProductDetail(productId, true);
            orderItem.put("product", product);
            jsonArray.add(orderItem);
        }

        return jsonArray;
    }

    public static void rejectAllOrder(int orderType, int status, String showTime) {
        DBUtil db = new DBUtil();
        String rejectAllOrderSQL = "update `order` set `status` = -2 where `status` = ? and `order_type` = ? and `show_time` = ?";
        List<Object> objectList = new ArrayList<>();
        objectList.add(status);
        objectList.add(orderType);
        objectList.add(showTime);
        db.insertUpdateDeleteExute(rejectAllOrderSQL, objectList);
    }

    public static void approvedOrder(int orderId) {
        DBUtil db = new DBUtil();
        String approveOrder = "update `order` set `status` = 5 where `order_id` = ?";
        List<Object> objectList = new ArrayList<>();
        objectList.add(orderId);
        db.insertUpdateDeleteExute(approveOrder, objectList);
    }

    public static int insertOrderAd(int orderType, String time, String userId, int shopId, double price, String filename, int productId) {
        DBUtil db = new DBUtil();

        String showTime = timeUtil.getShowTime();
        String redisKey;
        String getOrderIdSQL;
        List<Object> objectList1 = new ArrayList<>();

        // for orderId
        objectList1.add(orderType);
        objectList1.add(Integer.parseInt(userId));
        objectList1.add(time);

        List<Object> objectList = new ArrayList<>();
        String insertOrder;
        objectList.clear();
        objectList.add(orderType);
        objectList.add(time);
        objectList.add(Integer.parseInt(userId));
        objectList.add(shopId);
        objectList.add(1);
        objectList.add(price);
        objectList.add(price);
        if (filename.length() != 0) {
            insertOrder = "insert into `order` (`order_type`, `time`, `initiator_id`, `target_id`, `num`, `price`, `sum_price`, `photo`, `show_time`, `status`) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            objectList.add("http://koprvhdix117-10038234.file.myqcloud.com/" + filename);
            redisKey = showTime + String.valueOf(shopId);

            getOrderIdSQL = "select `order_id` from `order` where `order_type` = ? and `initiator_id` = ? and `time` = ?";
        } else {
            insertOrder = "insert into `order` (`order_type`, `time`, `initiator_id`, `target_id`, `num`, `price`, `sum_price`, `product_id`, `show_time`, `status`) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            objectList.add(productId);
            redisKey = "product" + showTime + String.valueOf(productId);

            getOrderIdSQL = "select `order_id` from `order` where `order_type` = ? and `initiator_id` = ? and `time` = ? and `product_id` = ?";
            objectList1.add(productId);
        }
        objectList.add(showTime);
        objectList.add(1);
        db.insertUpdateDeleteExute(insertOrder, objectList);

        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.set(redisKey, "true");
        jedis.expire(redisKey, 86400);

        try {
            List<Order> orderList = db.queryInfo(getOrderIdSQL, objectList1, Order.class);
            LogHelper.info(String.valueOf(orderList.get(0).getOrderId()));
            return orderList.get(0).getOrderId();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
