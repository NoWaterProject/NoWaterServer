package com.NoWater.model;

import com.NoWater.util.DBUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Koprvhdix on 2016/12/17.
 */
public class Order {
    private int order_id;
    private int order_type;
    private int product_id;
    private String time;
    private int initiator_id;
    private int target_id;
    private String address;
    private int num;
    private float price;
    private float sum_price;
    private int payment_id;
    private int status;
    private String express;
    private String express_code;
    private String photo;
    private String show_time;

    public String getShowTime() {
        return show_time;
    }

    public void setShow_time(String show_time) {
        this.show_time = show_time;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getExpress() {
        return express;
    }

    public String getExpressCode() {
        return express_code;
    }

    public void setExpress(String express) {
        this.express = express;
    }

    public void setExpress_code(String express_code) {
        this.express_code = express_code;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setInitiator_id(int initiator_id) {
        this.initiator_id = initiator_id;
    }

    public void setTarget_id(int target_id) {
        this.target_id = target_id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setSum_price(float sum_price) {
        this.sum_price = sum_price;
    }

    public void setPayment_id(int payment_id) {
        this.payment_id = payment_id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getOrderId() {
        return order_id;
    }

    public int getOrderType() {
        return order_type;
    }

    public int getProductId() {
        return product_id;
    }

    public String getTime() {
        return time;
    }

    public int getInitiatorId() {
        return initiator_id;
    }

    public int getTargetId() {
        return target_id;
    }

    public String getAddress() {
        return address;
    }

    public int getNum() {
        return num;
    }

    public float getPrice() {
        return price;
    }

    public float getSumPrice() {
        return sum_price;
    }

    public int getPaymentId() {
        return payment_id;
    }

    public int getStatus() {
        return status;
    }

    public static void getShopAdOrder(String getOrderSQL, List<Object> objectList, JSONObject jsonObject, int count) {
        DBUtil db = new DBUtil();
        List<Order> orderList;
        JSONArray jsonArray = new JSONArray();
        try {
            orderList = db.queryInfo(getOrderSQL, objectList, Order.class);
            if (count == 0 || count >= orderList.size()) {
                for (int i = 0; i < orderList.size(); i++) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("orderId", orderList.get(i).getOrderId());
                    jsonObject1.put("time", orderList.get(i).getTime());
                    jsonObject1.put("showTime", orderList.get(i).getShowTime());
                    jsonObject1.put("shopId", orderList.get(i).getTargetId());
                    jsonObject1.put("price", orderList.get(i).getPrice());
                    jsonObject1.put("photo", orderList.get(i).getPhoto());
                    jsonObject1.put("status", orderList.get(i).getStatus());
                    jsonArray.add(jsonObject1);
                }
                jsonObject.put("startId", -1);
            } else {
                for (int i = 0; i < count; i++) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("orderId", orderList.get(i).getOrderId());
                    jsonObject1.put("time", orderList.get(i).getTime());
                    jsonObject1.put("shopId", orderList.get(i).getTargetId());
                    jsonObject1.put("price", orderList.get(i).getPrice());
                    jsonObject1.put("photo", orderList.get(i).getPhoto());
                    jsonObject1.put("status", orderList.get(i).getStatus());
                    jsonObject1.put("showTime", orderList.get(i).getShowTime());
                    jsonArray.add(jsonObject1);
                }
                jsonObject.put("startId", orderList.get(count).getOrderId());
            }
            jsonObject.put("data", jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("statusOrder", 1100);
        }
    }

//    public static JSONObject getProductAdOrder() {
//
//    }
}
