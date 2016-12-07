package com.NoWater.shopOwner;

import com.NoWater.model.Product;
import com.NoWater.model.Shop;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.MD5;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qingpeng on 2016/12/3.
 */
@RestController
public class ShopOwnerProduct {
    @RequestMapping("shop-owner/products/list")
    public JSONObject searchKey(
            @RequestParam(value = "startId", defaultValue = "0") int startId,
            @RequestParam(value = "count", defaultValue = "/") int count,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String token = CookieUtil.getCookieValueByName(request, "token");
        JSONObject jsonObject = new JSONObject();
        int actualCount = 0, endId = 0;

        if (token == null) {
            jsonObject.put("status", 300);
        } else {
            Jedis jedis = new Jedis("127.0.0.1", 6379);
            String user_id = jedis.get(token);
            if (user_id == null) {
                jsonObject.put("status", 300);
            } else {
                String realToken = jedis.get(user_id);
                if (realToken.equals(token)) {
                    if (startId == -1) {
                        jsonObject.put("status", 400);
                    } else {
                        DBUtil db = new DBUtil();
                        List<Object> getShopId = new ArrayList<Object>();
                        String getShopIdSQL = "select shop_id from shop where owner_id = ?";
                        getShopId.add(user_id);
                        List<Shop> getShopIdList = db.queryInfo(getShopIdSQL, getShopId, Shop.class);
                        int shopId = getShopIdList.get(0).getShopId();

                        if (startId == 0) {
                            String getStartIdSQL = "select count(1) num from product";
                            List<Object> EmptyList = new ArrayList<Object>();
                            List<Product> getStartIdList = db.queryInfo(getStartIdSQL, EmptyList, Product.class);
                            startId = (int) getStartIdList.get(0).getNum();
                        }
                        List<Object> list = new ArrayList<Object>();
                        String sqlNoSearchKey = "select count(1) num,  from products c where shop_id = ? and product_id <= ?";
                        list.add(shopId);
                        list.add(startId);
                        List<Product> productListCount = db.queryInfo(sqlNoSearchKey, list, Product.class);
                        int num = (int) productListCount.get(0).getNum();

                        String queryProductInfo = "select * from products where shop_id = ? and product_id <= ? order by product_id asc";
                        List<Product> productListInfo = db.queryInfo(queryProductInfo, list, Product.class);

                        if (num > count) {
                            actualCount = count;
                            JSONArray jsonArray = new JSONArray();
                            for (int i = 0; i < actualCount; i++) {
                                JSONObject jsonObject1 = JSONObject.fromObject(productListInfo.get(i));
                                jsonArray.add(jsonObject1);
                            }
                            endId = productListInfo.get(actualCount).getProduct_id();
                            jsonObject.put("actualCount", actualCount);
                            jsonObject.put("endId", endId);
                            jsonObject.put("data", jsonArray);
                        } else {
                            actualCount = num;
                            jsonObject.put("actualCount", actualCount);
                            jsonObject.put("endId", -1);
                            jsonObject.put("data", JSONArray.fromObject(productListInfo));
                        }
                    }
                } else {
                    jsonObject.put("status", 300);
                }
            }
        }
        return jsonObject;
    }
}

