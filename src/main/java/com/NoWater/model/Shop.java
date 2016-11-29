package com.NoWater.model;

/**
 * Created by doodoo on 2016/11/28.
 */
public class Shop {

    private int shopId;
    private String shopName;
    private int ownerId;
    private String email;
    private int status;
    private String telephone;

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getShopName() {
        return shopName;
    }

    public int getOwnerId() {
        return ownerId;
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
        return shopId;
    }
}
