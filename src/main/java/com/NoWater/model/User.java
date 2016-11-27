package com.NoWater.model;

/**
 * Created by 李鹏飞 on 2016/11/25 0025.
 */
public class User {
    private int user_id;
    private String name;
    private String password;
    private String phone;
    private String address1;
    private String address2;
    private String address3;
    private int cart_pd_num;

    public int getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getAddress3() {
        return address3;
    }

    public int getCart_pd_num() {
        return cart_pd_num;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public void setCart_pd_num(int cart_pd_num) {
        this.cart_pd_num = cart_pd_num;
    }

}
