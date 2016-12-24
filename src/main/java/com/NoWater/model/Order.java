package com.NoWater.model;

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
    private int photo_id;

    public int getPhotoId() {
        return photo_id;
    }

    public void setPhoto_id(int photo_id) {
        this.photo_id = photo_id;
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
}
