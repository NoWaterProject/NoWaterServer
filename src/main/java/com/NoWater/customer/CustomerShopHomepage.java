package com.NoWater.customer;

import com.NoWater.util.ProductShopUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Koprvhdix on 2016/12/29.
 */
@RestController
public class CustomerShopHomepage {
    @RequestMapping("customer/shop/homepage")
    public JSONObject customerShopHomepage(@RequestParam(value = "shopId") int shopId,
                                        HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        Jedis jedis = new Jedis("127.0.0.1", 6379);
        String getProductAd = jedis.get("shop" + shopId);
        JSONArray jsonArray = JSONArray.fromObject(getProductAd);

        JSONArray jsonArray1 = new JSONArray();
        for (int i = 0; i < jsonArray.size(); i++) {
           int productId = jsonArray.getInt(i);
           jsonArray1.add(ProductShopUtil.GetProductDetail(productId, true, false, false));
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", 200);
        jsonObject.put("data", jsonArray1);
        return jsonObject;
    }
}
