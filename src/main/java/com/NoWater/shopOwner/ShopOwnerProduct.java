package com.NoWater.shopOwner;

import com.NoWater.model.Product;
import com.NoWater.model.Shop;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.MD5;
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
 * Created by qingpeng on 2016/12/3.
 */
@RestController
public class ShopOwnerProduct {
        @RequestMapping("shop-owner/products/list")
    public JSONObject searchKey(
            @RequestParam(value = "startId", defaultValue = "/") int startId,
            @RequestParam(value = "count", defaultValue = "/") int count,
            @RequestParam(value = "searchKey", defaultValue = "/") String searchKey,
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
                    List<Object> list = new ArrayList<Object>();
                    DBUtil db = new DBUtil();
                    if (searchKey == null) {
                        String sqlNoSearchKey = "select count(1) num from user a,shop b,products c where a.user_id = b.owner_id and b.shop_id = c.shop_id and user_id = ? and product_id >= ? order by product_id asc ";
                        list.add(user_id);
                        list.add(startId);
                        List<Product> productListCount = db.queryInfo(sqlNoSearchKey, list, Product.class);
                        int num = (int) productListCount.get(0).getNum();
                        if (num > count) {
                            actualCount = count;
                            endId = startId + count;
                        } else {
                            actualCount = num;
                            endId = -1;
                        }
                        String queryProductInfo = "select product_id,ad_product_url,product_name from user a,shop b,products c where a.user_id =b.owner_id and b.shop_id = c.shop_id and user_id = ? order by product_id asc limit ?,?";
                        list.add(startId);
                        list.add(actualCount);
                        List<Product> productListInfo = db.queryInfo(queryProductInfo, list, Product.class);
                        jsonObject.put("endId", endId);
                        jsonObject.put("searchKey", searchKey);
                        jsonObject.put("actualCount", actualCount);
                        JSONArray jsonArray = new JSONArray();
                        for (int i = startId; i < endId; i++) {
                            JSONObject ob = new JSONObject();
                            ob.put("product_id", productListInfo.get(i).getProduct_id());
                            ob.put("product_name", productListInfo.get(i).getProduct_name());
                            ob.put("ad_product_url", productListInfo.get(1).getAd_photo_url());
                            jsonArray.add(i, ob);
                        }
                        jsonObject.put("shopProduct", jsonArray);
                        searchKey = MD5.getInstance().getMD5(jsonObject.toString());
                        jedis.set("searchKey",searchKey);
                    } else {
                        String js = jedis.get(searchKey);
                        jsonObject.fromObject(js);
                    }
                } else {
                    jsonObject.put("status",300);
                }
            }
        }
        return jsonObject;
    }
}

