package com.NoWater.model;

import com.NoWater.util.DBUtil;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李鹏飞 on 2016/11/25 0025.
 */
public class User {
    private Integer user_id;
    private String name;
    private String password;
    private String telephone;
    private String address1;
    private String address2;
    private String address3;
    private String post_code;
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setPostCode(String post_code) {
        this.post_code = post_code;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getPostCode() {
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
        return telephone;
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
        this.telephone = phone;
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

    public static String getUserName(int userId) {
        List<Object> list = new ArrayList<>();
        DBUtil db = new DBUtil();
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT name FROM `user` WHERE user_id = ?");
        list.add(userId);
        try {
            List<User> nameList = db.queryInfo(sql.toString(), list, User.class);
            String name = nameList.get(0).getName();
            return name;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
