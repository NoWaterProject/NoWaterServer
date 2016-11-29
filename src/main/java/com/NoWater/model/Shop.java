package com.NoWater.model;

/**
 * Created by doodoo on 2016/11/28.
 */
public class Shop {

    private int shop_id;
    private String shop_name;
    private int owner_id;
    private String email;
    private int status;
    private String telephone;

    public void setShopName(String shopName) {
        this.shop_name = shopName;
    }

    public void setOwnerId(int ownerId) {
        this.owner_id = ownerId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getShopName() {
        return shop_name;
    }

    public int getOwnerId() {
        return owner_id;
    }

    public String getEmail() {
        return email;
    }

    public int getStatus() {
        return status;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getShopId() {
        return shop_id;
    }

}
