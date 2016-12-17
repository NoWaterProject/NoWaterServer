package com.NoWater.model;

/**
 * Created by Koprvhdix on 2016/12/17.
 */
public class Payment {
    private int payment_id;
    private String aliPay_account;
    private float price;
    private int status;

    public void setPayment_id(int payment_id) {
        this.payment_id = payment_id;
    }

    public void setAliPay_account(String aliPay_account) {
        this.aliPay_account = aliPay_account;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPaymentId() {
        return payment_id;
    }

    public String getAliPayAccount() {
        return aliPay_account;
    }

    public float getPrice() {
        return price;
    }

    public int getStatus() {
        return status;
    }
}
