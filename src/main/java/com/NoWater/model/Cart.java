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

    public int getProductId() {
        return product_id;
    }

    public void setProductId(int product_id) {
        this.product_id = product_id;
    }

    public long getCartNum() {
        return cartNum;
    }

    public void setCartNum(long cartNum) {
        this.cartNum = cartNum;
    }

    public void setCartId(int cart_id) {
        this.cart_id = cart_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getCartId() {
        return cart_id;
    }

    public int getUserId() {
        return user_id;
    }

    public int getNum() {
        return num;
    }


}
