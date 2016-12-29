package com.NoWater.model;

import com.NoWater.util.DBUtil;
import net.sf.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wukai on 2016/12/29.
 */
public class ShopHomepage {
    private int shop_homepage_id;
    private int shop_id;
    private int product_id;

    public int getShopHomepageId() {
        return shop_homepage_id;
    }

    public int getShopId() {
        return shop_id;
    }

    public int getProductId() {
        return product_id;
    }

    public void setShop_homepage_id(int shop_homepage_id) {
        this.shop_homepage_id = shop_homepage_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public static JSONArray getProductId(int shopId) {
        String getProductSQL = "select * from `shop_homepage` where `shop_id` = ?";
        List<Object> objectList = new ArrayList<>();
        objectList.add(shopId);
        DBUtil db = new DBUtil();
        JSONArray jsonArray = new JSONArray();
        try {
            List<ShopHomepage> shopHomepageList = db.queryInfo(getProductSQL, objectList, ShopHomepage.class);
            for (int i = 0; i < shopHomepageList.size(); i++) {
                jsonArray.add(shopHomepageList.get(i).getProductId());
            }
            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }
}
