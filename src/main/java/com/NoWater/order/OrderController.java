package com.NoWater.order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.NoWater.model.Cart;
import com.NoWater.model.Order;
import com.NoWater.model.Product;
import com.NoWater.model.User;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.sun.org.apache.xpath.internal.operations.Or;
import net.sf.json.JSONArray;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.NoWater.util.LogHelper;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by wukai on 16-11-18.
 */
@RestController
public class OrderController {
    @RequestMapping("/order/prepare")
    public JSONObject greeting(@RequestParam(value = "orderType", defaultValue = "3") int orderType,
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
            for (int i = 0; i < jsonArray.size(); i++) {
                int cartId = jsonArray.getInt(i);

                String getCartSQL = "select * from cart where cart_id = ?";
                List<Object> getCartList = new ArrayList<>();
                getCartList.add(cartId);
                List<Cart> cartList;
                try {
                    cartList = db.queryInfo(getCartSQL, getCartList, Cart.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    jsonObject.put("status", 1100);
                    return jsonObject;
                }

                for (int j = 0; j < cartList.size(); j++) {
                    int orderId = insertOrder(3, cartList.get(j).getProductId(),
                            currentTime, user_id, address, cartList.get(j).getNum());
                    orderList.add(orderId);
                }
            }
        } else {
            int orderId = insertOrder(orderType, productId, currentTime, user_id, address, num);
            orderList.add(orderId);
        }
        jsonObject.put("status", 200);
        jsonObject.put("orderIdList", orderList);
        return jsonObject;
    }

    private static int insertOrder(int order_type, int productId,
                                    String time, int user_id,
                                    String address,
                                    int num) {
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

        String insertOrderSQL = "insert into order (order_type, product_id, time, initiator_id, target_id, address, num, price, sum_price) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
}