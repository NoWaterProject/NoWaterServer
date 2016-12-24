package com.NoWater.model;

import com.NoWater.util.DBUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by qingpeng on 2016/12/4.
 */
public class Product {
    private int product_id;
    private String product_name;
    private int shop_id;
    private int class_id;
    private int is_del;
    private long num;
    private double price;
    private int quantity_stock;

    public int getIsDel() {
        return is_del;
    }

    public void setIsDel(int is_del) {
        this.is_del = is_del;
    }

    public int getQuantityStock() {
        return quantity_stock;
    }

    public void setQuantityStock(int quantity_stock) {
        this.quantity_stock = quantity_stock;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public int getProductId() {
        return product_id;
    }

    public void setProductId(int product_id) {
        this.product_id = product_id;
    }

    public String getProductName() {
        return product_name;
    }

    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    public int getShopId() {
        return shop_id;
    }

    public void setShopId(int shop_id) {
        this.shop_id = shop_id;
    }

    public int getClassId() {
        return class_id;
    }

    public void setClassId(int class_id) {
        this.class_id = class_id;
    }

    public static int confirmStock(int num, int productId) {
        DBUtil db = new DBUtil();
        String getProductId = "select * from `products` where `product_id` = ?";
        List<Object> objectList = new ArrayList<>();
        objectList.add(productId);
        try {
            List<Product> productList = db.queryInfo(getProductId, objectList, Product.class);
            if (productList.size() == 0) {
                return 2400;
            }

            if (productList.get(0).getQuantityStock() < num) {
                return 2500;
            } else {
                return 200;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 1100;
        }
    }

    public static void decreaseStock(int num, int productId) {
        DBUtil db = new DBUtil();
        String updateProduct = "update `products` set `quantity_stock` = `quantity_stock` - ? where `product_id` = ?";
        List<Object> updateProductList = new ArrayList<>();
        updateProductList.add(num);
        updateProductList.add(productId);
        db.insertUpdateDeleteExute(updateProduct, updateProductList);
    }

    public static void increaseStock(int num, int productId) {
        DBUtil db = new DBUtil();
        String updateProduct = "update `products` set `quantity_stock` = `quantity_stock` + ? where `product_id` = ?";
        List<Object> updateProductList = new ArrayList<>();
        updateProductList.add(num);
        updateProductList.add(productId);
        db.insertUpdateDeleteExute(updateProduct, updateProductList);
    }

    public static boolean confirmProductShop(int shopId, int productId) {
        DBUtil db = new DBUtil();
        String confirmSQL = "select * from `products` where `product_id` = ? and `shop_id` = ?";
        List<Object> objectList = new ArrayList<>();
        objectList.add(productId);
        objectList.add(shopId);
        try {
            List<Product> productList = db.queryInfo(confirmSQL, objectList, Product.class);
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
}
