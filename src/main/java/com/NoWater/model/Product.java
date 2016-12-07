package com.NoWater.model;

/**
 * Created by qingpeng on 2016/12/4.
 */
public class Product {
    private int product_id;
    private String product_name;
    private String ad_photo_url;
    private int shop_id;
    private int class_id;
    private String product_photo_url;
    private int is_del;
    private long num;
    private double price;
    private int quantity_stock;

    public int getIs_del() {
        return is_del;
    }

    public void setIs_del(int is_del) {
        this.is_del = is_del;
    }

    public int getQuantity_stock() {
        return quantity_stock;
    }

    public void setQuantity_stock(int quantity_stock) {
        this.quantity_stock = quantity_stock;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getAd_photo_url() {
        return ad_photo_url;
    }

    public void setAd_photo_url(String ad_photo_url) {
        this.ad_photo_url = ad_photo_url;
    }

    public int getShop_id() {
        return shop_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }

    public String getProduct_photo_url() {
        return product_photo_url;
    }

    public void setProduct_photo_url(String product_photo_url) {
        this.product_photo_url = product_photo_url;
    }

}
