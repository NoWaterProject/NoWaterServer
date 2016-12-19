package com.NoWater.model;

/**
 * Created by 李鹏飞 on 2016/11/25 0025.
 */
public class User {
    private Integer user_id;
    private String name;
    private String password;
    private String phone;
    private String address1;
    private String address2;
    private String address3;
    private int post_code;
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setPostCode(int post_code) {
        this.post_code = post_code;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public int getPostCode() {
        return post_code;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    private String first_name;
    private String last_name;
    private int cart_pd_num;

    public Integer getUserId() {
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

    public int getCartPdNum() {
        return cart_pd_num;
    }

    public void setUserId(int user_id) {
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

    public void setCartPdNum(int cart_pd_num) {
        this.cart_pd_num = cart_pd_num;
    }

}
