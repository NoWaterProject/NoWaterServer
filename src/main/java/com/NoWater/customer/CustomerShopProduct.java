package com.NoWater.customer;

import com.NoWater.model.Photo;
import com.NoWater.model.Product;
import com.NoWater.model.Shop;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.LogHelper;
import com.NoWater.util.NoWaterProperties;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qingpeng on 2016/12/8.
 */
@RestController
public class CustomerShopProduct {
    @RequestMapping("customer/shop/info")
    public JSONObject customerShopInfo(
            @RequestParam(value = "shopId", defaultValue = "0") int shopId,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        LogHelper.info("[customer/shop/info] [params] shopId: " + String.valueOf(shopId));

        DBUtil db = new DBUtil();
        List<Object> list = new ArrayList<>();
        String shopExistSQL = "select * from shop where shop_id = ?";
        list.add(shopId);
        List<Shop> getShopExist = db.queryInfo(shopExistSQL, list, Shop.class);

        if (getShopExist.size() == 0) {
            jsonObject.put("status", 400);
            LogHelper.info(String.format("[customer/shop/info] %s", jsonObject.toString()));
            return jsonObject;
        }

        jsonObject.put("status", 200);
        jsonObject = JSONObject.fromObject(getShopExist.get(0));

        String queryClass = "select distinct class_id from products where shop_id = ? order by class_id";
        List<Product> getProductClass = db.queryInfo(queryClass, list, Product.class);

        String[] classNameList = NoWaterProperties.getClassName();

        if (getProductClass.size() == 0) {
            jsonObject.put("classList", "[]");
        } else {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < getProductClass.size(); i++) {
                String className = classNameList[getProductClass.get(i).getClassId()];
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("classId", getProductClass.get(i).getClassId());
                jsonObject1.put("className", className);
                jsonArray.add(jsonObject1);
            }
            jsonObject.put("classList", jsonArray);
        }

        LogHelper.info(String.format("[customer/shop/class/list] %s", jsonObject.toString()));
        return jsonObject;
    }

    @RequestMapping("customer/product/show")
    public JSONObject customerProductShow(
            @RequestParam(value = "productId", defaultValue = "0") int productId,
            HttpServletRequest request, HttpServletResponse response) {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        LogHelper.info(String.format("[customer/product/show] [param] [product_id: %s]", productId));

        DBUtil db = new DBUtil();
        List<Object> list = new ArrayList<>();
        String productExistSQL = "select * from products where product_id = ?";
        list.add(productId);
        List<Product> getProductExist;
        try {
            getProductExist = db.queryInfo(productExistSQL, list, Product.class);
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.error(e.toString());
            jsonObject.put("status", 400);
            return jsonObject;
        }

        // can't find product
        if (getProductExist.size() == 0) {
            jsonObject.put("status", 400);
            LogHelper.error(String.format("[customer/product/show] %s", jsonObject));
            return jsonObject;
        }

        if (getProductExist.get(0).getIsDel() == 1) {
            jsonObject.put("status", 500);
        } else {
            jsonObject.put("status", 200);
        }

        JSONObject jsonObject1 = JSONObject.fromObject(getProductExist);

        int shopId = getProductExist.get(0).getShopId();

        List<Object> getShopInfo = new ArrayList<>();
        String shopInfoSQL = "select * from shop where shop_id = ?";
        getShopInfo.add(shopId);
        List<Shop> ShopInfo;
        try {
            ShopInfo = db.queryInfo(shopInfoSQL, getShopInfo, Shop.class);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 600);
            LogHelper.error(String.format("[customer/product/show] %s", jsonObject));
            return jsonObject;
        }

        JSONObject jsonObject2 = JSONObject.fromObject(ShopInfo);
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("shop", jsonObject2);
        jsonObject3.put("product", jsonObject1);
        jsonObject.put("data", jsonObject3);

        LogHelper.error(String.format("[customer/product/show] %s", jsonObject));
        return jsonObject;
    }
}
