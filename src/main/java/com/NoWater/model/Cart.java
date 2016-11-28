package com.NoWater.model;

/**
 * Created by 李鹏飞 on 2016/11/28 0028.
 */
public class Cart {

    private int cart_id;
    private int user_id;
    private int products_id;
    private int num;
    private int size_id;
    private String name;
    private long cartNum;

    public int getSize_id() {
        return size_id;
    }

    public void setSize_id(int size_id) {
        this.size_id = size_id;
    }

    public long getCartNum() {
        return cartNum;
    }

    public void setCartNum(long cartNum) {
        this.cartNum = cartNum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCart_id(int cart_id) {
        this.cart_id = cart_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setProducts_id(int products_id) {
        this.products_id = products_id;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getCart_id() {
        return cart_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getProducts_id() {
        return products_id;
    }

    public int getNum() {
        return num;
    }


}
