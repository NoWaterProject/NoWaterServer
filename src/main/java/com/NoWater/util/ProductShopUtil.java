package com.NoWater.util;

import com.NoWater.model.Photo;
import com.NoWater.model.Product;
import com.NoWater.model.Shop;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Koprvhdix on 2016/12/19.
 */
public final class ProductShopUtil {
    public static JSONObject GetProductDetail(int productId) {
        JSONObject jsonObject = new JSONObject();
        DBUtil db = new DBUtil();
        String getProduct = "select * from `products` where `product_id` = ?";
        List<Object> list = new ArrayList<>();
        list.add(productId);
        List<Product> productList;
        try {
            productList = db.queryInfo(getProduct, list, Product.class);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
            return jsonObject;
        }

        if (productList.size() == 0) {
            jsonObject.put("status", 400);
            return jsonObject;
        }

        jsonObject = JSONObject.fromObject(productList.get(0));
        String getPhotoSQL = "select * from photo where belong_id = ? and photo_type = ? and is_del = 0";
        jsonObject.put("photo", JSONArray.fromObject(Photo.getPhotoURL(getPhotoSQL, productId, 2)));
        return jsonObject;
    }

    public static JSONObject GetShopDetail(int shopId) {
        JSONObject jsonObject = new JSONObject();
        DBUtil db = new DBUtil();
        String getShopSQL = "select * from `shop` where `shop_id` = ?";
        List<Object> list = new ArrayList<>();
        list.add(shopId);
        List<Shop> ShopList;
        try {
            ShopList = db.queryInfo(getShopSQL, list, Shop.class);
            jsonObject = JSONObject.fromObject(ShopList);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
            return jsonObject;
        }
    }
}
