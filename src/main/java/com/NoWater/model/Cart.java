package com.NoWater.model;

/**
 * Created by 李鹏飞 on 2016/11/28 0028.
 */
public class Cart {

    private int cart_id;
    private int user_id;
    private int product_id;
    private int num;
    private long cartNum;

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public long getCartNum() {
        return cartNum;
    }

    public void setCartNum(long cartNum) {
        this.cartNum = cartNum;
    }

    public void setCart_id(int cart_id) {
        this.cart_id = cart_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
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


    public int getNum() {
        return num;
    }


}
