package com.NoWater.customer;

import com.NoWater.util.LogHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by wukai on 2016/11/28.
 */
@RestController
public class CustomerAd {

    @RequestMapping("/customer/shop/ad")
    public JSONObject shopAd(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        Jedis jedis = new Jedis("127.0.0.1", 6379);
        String shop = jedis.get("ShopAd");

        if (shop != null) {
            LogHelper.info("shopAd: " + shop);
            JSONArray data = JSONArray.fromObject(shop);
            jsonObject.put("status", 200);
            jsonObject.put("data", data);
        } else {
            jsonObject.put("status", 400);
        }
        return jsonObject;
    }

    @RequestMapping("/customer/product/ad")
    public JSONObject productAd(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        Jedis jedis = new Jedis("127.0.0.1", 6379);
        String productAd = jedis.get("ProductAd");

        if (productAd != null) {
            LogHelper.info("productAd: " + productAd);
            JSONArray data = JSONArray.fromObject(productAd);
            jsonObject.put("status", 200);
            jsonObject.put("data", data);
        } else {
            jsonObject.put("status", 400);
        }

        return jsonObject;
    }
}
