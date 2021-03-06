package com.NoWater.customer;

import com.NoWater.model.Photo;
import com.NoWater.model.Product;
import com.NoWater.model.Shop;
import com.NoWater.util.*;
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

        jsonObject.put("data", ProductShopUtil.GetShopDetail(shopId, false));

        LogHelper.info(String.format("[customer/shop/info] %s", jsonObject.toString()));
        return jsonObject;
    }

    @RequestMapping("customer/product/show")
    public JSONObject customerProductShow(@RequestParam(value = "productId", defaultValue = "0") int productId,
                                          HttpServletRequest request, HttpServletResponse response) {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        LogHelper.info(String.format("[customer/product/show] [param] [product_id: %s]", productId));

        JSONObject product = ProductShopUtil.GetProductDetail(productId, true, true, false);
        if (product.has("status")) {
            jsonObject.put("status", 400);
            LogHelper.error(String.format("[customer/product/show] %s", jsonObject));
            return jsonObject;
        }

        jsonObject.put("status", 200);
        jsonObject.put("data", product);

        LogHelper.error(String.format("[customer/product/show] %s", jsonObject));
        return jsonObject;
    }
}
