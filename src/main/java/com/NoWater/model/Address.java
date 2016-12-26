package com.NoWater.model;

/**
 * Created by Koprvhdix on 2016/12/24.
 */
public class Address {
    private int address_id;
    private String telephone;
    private String address1;
    private String address2;
    private String address3;
    private String post_code;
    private String first_name;
    private String last_name;
    private int user_id;
    private int is_default;

    public int getAddressId() {
        return address_id;
    }

    public void setAddressId(int address_id) {
        this.address_id = address_id;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getPostCode() {
        return post_code;
    }

    public void setPostCode(String post_code) {
        this.post_code = post_code;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public int getIsDefault() {
        return is_default;
    }

    public void setIsDefault(int is_default) {
        this.is_default = is_default;
    }
}
