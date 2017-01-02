package com.NoWater.util;

import com.NoWater.model.Comment;
import com.NoWater.model.Photo;
import com.NoWater.model.Product;
import com.NoWater.model.Shop;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by Koprvhdix on 2016/12/19.
 */
public final class ProductShopUtil {
    public static JSONObject GetProductDetail(int productId, boolean hasShop, boolean hasComment, boolean hasPhoto) {
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
        if (hasShop) {
            jsonObject.put("shop", GetShopDetail(productList.get(0).getShopId(), hasPhoto));
        }
        String getPhotoSQL = "select * from photo where belong_id = ? and photo_type = ? and is_del = 0";
        jsonObject.put("photo", JSONArray.fromObject(Photo.getPhotoURL(getPhotoSQL, productId, 2)));

        if (hasComment) {
            String getComment = "select * from `comment_product` where `product_id` = ?";
            List<Object> objectList = new ArrayList<>();
            objectList.add(productId);
            try {
                List<Comment> commentList = db.queryInfo(getComment, objectList, Comment.class);
                JSONObject commentObject = new JSONObject();
                if (commentList.size() == 0) {
                    commentObject.put("avgPoint", 5);
                    commentObject.put("commentList", "[]");
                } else {
                    double avgPoint;
                    int sum = 0;
                    for (int i = 0; i < commentList.size(); i++) {
                        sum += commentList.get(i).getStar();
                    }
                    avgPoint = (double) sum / commentList.size();
                    commentObject.put("avgPoint", avgPoint);
                    commentObject.put("commentList", JSONArray.fromObject(commentList));
                }
                jsonObject.put("comment", commentObject);
            } catch (Exception e) {
                e.printStackTrace();
                jsonObject.put("status", 1100);
            }
        }

        return jsonObject;
    }

    public static boolean ProductExist(int productId) {
        DBUtil db = new DBUtil();
        String getProduct = "select * from `products` where `product_id` = ?";
        List<Object> list = new ArrayList<>();
        list.add(productId);
        List<Product> productList;
        try {
            productList = db.queryInfo(getProduct, list, Product.class);
            if (productList.size() == 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean ShopExist(int shopId) {
        DBUtil db = new DBUtil();
        String getProduct = "select * from `shop` where `shop_id` = ?";
        List<Object> list = new ArrayList<>();
        list.add(shopId);
        List<Shop> shopList;
        try {
            shopList = db.queryInfo(getProduct, list, Shop.class);
            if (shopList.size() == 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static JSONObject GetShopDetail(int shopId, boolean hasPhoto) {
        JSONObject jsonObject = new JSONObject();
        DBUtil db = new DBUtil();
        String getShopSQL = "select * from `shop` where `shop_id` = ?";
        List<Object> list = new ArrayList<>();
        list.add(shopId);
        List<Shop> ShopList;
        try {
            ShopList = db.queryInfo(getShopSQL, list, Shop.class);
            jsonObject = JSONObject.fromObject(ShopList.get(0));

            if (hasPhoto) {
                String getPhotoSQL = "select * from photo where belong_id = ? and photo_type = ? and is_del = 0";
                jsonObject.put("photo", JSONArray.fromObject(Photo.getPhotoURL(getPhotoSQL, ShopList.get(0).getOwnerId(), 3)));
            }

            ArrayList<Integer> classList = GetClassDetail(shopId);

            jsonObject.put("classList", JSONArray.fromObject(classList));

            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
            return jsonObject;
        }
    }

    public static ArrayList<Integer> GetClassDetail(int shopId) {
        ArrayList<Integer> classList = new ArrayList<>();
        DBUtil db = new DBUtil();
        String getProductSQL = "select * from `products` where `shop_id` = ? and `is_del` = 0";
        List<Object> list = new ArrayList<>();
        list.add(shopId);
        List<Product> productList;
        try {
            productList = db.queryInfo(getProductSQL, list, Product.class);

            int[] classSet = {0, 0, 0, 0, 0, 0, 0, 0, 0};

            for (int i = 0; i < productList.size(); i++) {
                classSet[productList.get(i).getClassId()] = 1;
            }

            for (int i = 0; i < 9; i++) {
                if (classSet[i] == 1) {
                    classList.add(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            classList.add(-1);
        }

        return classList;
    }
}
