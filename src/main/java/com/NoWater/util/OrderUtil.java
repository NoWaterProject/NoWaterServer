package com.NoWater.util;

import com.NoWater.model.Order;
import com.NoWater.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Koprvhdix on 2016/12/18.
 */
public final class OrderUtil {
    public static int insertOrder(int order_type, int productId,
                                  String time, int user_id,
                                  String address, int num) {
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

        double price = ProductItem.get(0).getPrice();

        String insertOrderSQL = "insert into order (order_type, product_id, time, initiator_id, target_id, address, num, price, sum_price) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object> insertList = new ArrayList<>();
        insertList.add(order_type);
        insertList.add(productId);
        insertList.add(time);
        insertList.add(user_id);
        insertList.add(ProductItem.get(0).getShopId());
        insertList.add(address);
        insertList.add(num);
        insertList.add(price);
        insertList.add(num * price);

        db.insertUpdateDeleteExute(insertOrderSQL, insertList);

        String getOrderIdSQL = "select order_id from order where order_type = ? and initiator_id = ? and time = ?";
        List<Object> orderIdList = new ArrayList<>();
        orderIdList.add(order_type);
        orderIdList.add(user_id);
        orderIdList.add(time);
        List<Order> orderItem;
        try {
            orderItem = db.queryInfo(getOrderIdSQL, orderIdList, Order.class);
        } catch (Exception e) {
            e.printStackTrace();
            return -10;
        }

        return orderItem.get(0).getOrderId();
    }

    public static int confirmOrderUserId(int orderId, String userId, int status) {
        DBUtil db = new DBUtil();
        String confirmOrderId = "select * from `order` where `orderId` = ? and `status` = ?";
        List<Object> getConfirmOrderId = new ArrayList<>();
        getConfirmOrderId.add(orderId);
        getConfirmOrderId.add(status);
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

        if (confirmOrder.get(0).getInitiatorId() != Integer.parseInt(userId)) {
            return 500;
        }

        return 200;
    }
}
