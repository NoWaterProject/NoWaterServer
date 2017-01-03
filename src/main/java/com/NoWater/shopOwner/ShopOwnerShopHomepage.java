package com.NoWater.shopOwner;

import com.NoWater.model.Product;
import com.NoWater.model.ShopHomepage;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.ProductShopUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Koprvhdix on 2016/12/29.
 */
@RestController
public class ShopOwnerShopHomepage {
    @RequestMapping("shop-owner/homepage")
    public JSONObject shopOwnerHomepage(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        JSONObject jsonObject = new JSONObject();
        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);

        if (userId == null) {
            jsonObject.put("status", 300);
            return jsonObject;
        }

        int shopId = CookieUtil.confirmShop(userId);

        if (shopId == -1) {
            jsonObject.put("status", 500);      // not shop owner
            return jsonObject;
        }

        Jedis jedis = new Jedis("127.0.0.1", 6379);
        String getProductAd = jedis.get("shop" + shopId);
        if (getProductAd == null) {
            jedis.set("shop" + shopId, "[]");
            getProductAd = jedis.get("shop" + shopId);
        }

        JSONArray jsonArray = JSONArray.fromObject(getProductAd);

        JSONArray jsonArray1 = new JSONArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            int productId = jsonArray.getInt(i);
            jsonArray1.add(ProductShopUtil.GetProductDetail(productId, true, false, false));
        }

        jsonObject.put("status", 200);
        jsonObject.put("data", jsonArray1);
        return jsonObject;
    }


    @RequestMapping("/shop-owner/homepage/product/adding")
    public JSONObject shopOwnerHomePageProductAdding(@RequestParam(value = "productId") int productId,
                                                     HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        JSONObject jsonObject = new JSONObject();
        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);

        if (userId == null) {
            jsonObject.put("status", 300);
            return jsonObject;
        }

        int shopId = CookieUtil.confirmShop(userId);

        if (shopId == -1) {
            jsonObject.put("status", 500);      // not shop owner
            return jsonObject;
        }

        if(!Product.confirmProductShop(shopId, productId)) {
            jsonObject.put("status", 400);      //  productId error
            return jsonObject;
        }

        DBUtil db = new DBUtil();
        String confirmProduct = "select * from `shop_homepage` where `shop_id` = ? and `product_id` = ?";
        List<Object> objectList = new ArrayList<>();
        objectList.add(shopId);
        objectList.add(productId);
        try {
            List<ShopHomepage> shopHomepageList = db.queryInfo(confirmProduct, objectList, ShopHomepage.class);
            if (shopHomepageList.size() == 0) {
                String insertSQL = "insert into `shop_homepage` (`shop_id`, `product_id`) values (?, ?)";
                db.insertUpdateDeleteExute(insertSQL, objectList);
            } else {
                jsonObject.put("status", 600);
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
            return jsonObject;
        }

        Jedis jedis = new Jedis("127.0.0.1", 6379);
        JSONArray jsonArray = ShopHomepage.getProductId(shopId);

        jedis.set("shop" + shopId, jsonArray.toString());
        jsonObject.put("status", 200);
        return jsonObject;
    }

    @RequestMapping("/shop-owner/homepage/product/deleting")
    public JSONObject shopOwnerHomePageProductDeleting(@RequestParam(value = "productId") int productId,
                                                     HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        JSONObject jsonObject = new JSONObject();
        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);

        if (userId == null) {
            jsonObject.put("status", 300);
            return jsonObject;
        }

        int shopId = CookieUtil.confirmShop(userId);

        if (shopId == -1) {
            jsonObject.put("status", 500);      // not shop owner
            return jsonObject;
        }

        String deleteSQL = "delete from `shop_homepage` where `shop_id` = ? and `product_id` = ?";
        List<Object> objectList = new ArrayList<>();
        objectList.add(shopId);
        objectList.add(productId);
        DBUtil db = new DBUtil();
        db.insertUpdateDeleteExute(deleteSQL, objectList);

        Jedis jedis = new Jedis("127.0.0.1", 6379);
        JSONArray jsonArray = ShopHomepage.getProductId(shopId);

        jedis.set("shop" + shopId, jsonArray.toString());
        jsonObject.put("status", 200);
        return jsonObject;
    }
}
