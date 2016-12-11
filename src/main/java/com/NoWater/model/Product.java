package com.NoWater.model;

/**
 * Created by qingpeng on 2016/12/4.
 */
public class Product {
    private int product_id;
    private String product_name;
    private int shop_id;
    private int class_id;
    private int is_del;
    private long num;
    private double price;
    private int quantity_stock;

    public int getIsDel() {
        return is_del;
    }

    public void setIsDel(int is_del) {
        this.is_del = is_del;
    }

    public int getQuantityStock() {
        return quantity_stock;
    }

    public void setQuantityStock(int quantity_stock) {
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

    public int getProductId() {
        return product_id;
    }

    public void setProductId(int product_id) {
        this.product_id = product_id;
    }

    public String getProductName() {
        return product_name;
    }

    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    public int getShopId() {
        return shop_id;
    }

    public void setShopId(int shop_id) {
        this.shop_id = shop_id;
    }

    public int getClassId() {
        return class_id;
    }

    public void setClassId(int class_id) {
        this.class_id = class_id;
    }

}
