package com.NoWater.shopOwner;

import com.NoWater.model.Photo;
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
        int actualCount;
        int endId;

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

                        String queryProductNotDel = "select * from products where shop_id = ? and product_id <= ? and `is_del` = 0 order by product_id asc";
                        String queryProductDel = "select * from products where shop_id = ? and product_id <= ? and `is_del` = 1 order by product_id asc";
                        List<Product> productListInfo = db.queryInfo(queryProductNotDel, list, Product.class);
                        List<Product> productListDel = db.queryInfo(queryProductDel, list, Product.class);
                        productListInfo.addAll(productListDel);

                        if (num > count) {
                            actualCount = count;
                            JSONArray jsonArray = new JSONArray();
                            for (int i = 0; i < actualCount; i++) {
                                JSONObject jsonObject1 = JSONObject.fromObject(productListInfo.get(i));
                                jsonArray.add(jsonObject1);
                            }
                            endId = productListInfo.get(actualCount).getProductId();
                            jsonObject.put("endId", endId);
                            jsonObject.put("data", jsonArray);
                        } else {
                            actualCount = num;
                            jsonObject.put("endId", -1);
                            jsonObject.put("data", JSONArray.fromObject(productListInfo));
                        }
                        jsonObject.put("status", 200);
                        jsonObject.put("actualCount", actualCount);
                    }
                } else {
                    jsonObject.put("status", 300);
                }
            }
        }
        return jsonObject;
    }

    @RequestMapping("shop-owner/products/detail")
    public JSONObject productsDetail(
            @RequestParam(value = "product_id", defaultValue = "0") int product_id,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");

//        String token = CookieUtil.getCookieValueByName(request, "token");
        JSONObject jsonObject = new JSONObject();
        if (product_id <= 0) {
            jsonObject.put("status", 300);
        } else {
            DBUtil db = new DBUtil();
            List<Object> list = new ArrayList<Object>();
            String queryProduct = "select * from products where product_id = ?";
            list.add(product_id);
            List<Product> productList = db.queryInfo(queryProduct, list, Product.class);
            jsonObject.put("data", JSONArray.fromObject(productList));

            String queryPhoto = "select * from photo where product_id = ? and photo_type = 2";
            list.add(product_id);
            List<Photo> photoList = db.queryInfo(queryPhoto, list, Photo.class);
            jsonObject.put("photo", JSONObject.fromObject(photoList));
            jsonObject.put("status", 200);
        }
        return jsonObject;
    }
}

