package com.NoWater.shopOwner;

import com.NoWater.model.Photo;
import com.NoWater.model.Product;
import com.NoWater.model.Shop;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.LogHelper;
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
    public JSONObject ShopOwnerProductsList(
            @RequestParam(value = "startId", defaultValue = "0") int startId,
            @RequestParam(value = "count", defaultValue = "/") int count,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String token = CookieUtil.getCookieValueByName(request, "token");
        JSONObject jsonObject = new JSONObject();
        int actualCount;
        int endId;

        String user_id = CookieUtil.confirmUser(token);
        if (user_id == null) {
            jsonObject.put("status", 300);                  //用户未登录
            return jsonObject;
        }

        if (startId < 0) {
            jsonObject.put("status", 400);
            return jsonObject;
        }

        DBUtil db = new DBUtil();
        List<Object> getShopId = new ArrayList<>();
        String getShopIdSQL = "select shop_id from shop where owner_id = ?";
        getShopId.add(user_id);
        List<Shop> getShopIdList = db.queryInfo(getShopIdSQL, getShopId, Shop.class);
        if (getShopIdList.size() == 0) {
            jsonObject.put("status", 500);              // user isn't shop owner
            return jsonObject;
        }
        int shopId = getShopIdList.get(0).getShopId();

        StringBuffer queryProductNotDel = new StringBuffer();
        StringBuffer queryProductDel = new StringBuffer();
        queryProductNotDel.append("select * from products where shop_id = ? and `is_del` = 0 order by product_id desc");
        queryProductDel.append("select * from products where shop_id = ? and `is_del` = 1 order by product_id desc");
        List<Object> list = new ArrayList<>();
        list.add(shopId);

        List<Product> productListInfo = db.queryInfo(queryProductNotDel.toString(), list, Product.class);
        List<Product> productListDel = db.queryInfo(queryProductDel.toString(), list, Product.class);
        productListInfo.addAll(productListDel);

        if (productListInfo.size() - startId > count) {
            actualCount = count;
            endId = startId + count;
            jsonObject.put("endId", endId);
        } else {
            actualCount = productListInfo.size() - startId;
            jsonObject.put("endId", -1);
        }

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < actualCount; i++) {
            JSONObject jsonObject1 = JSONObject.fromObject(productListInfo.get(i + startId));

            int product_id = productListInfo.get(i + startId).getProductId();

            String getPhotoSQL = "select * from photo where belong_id = ? and photo_type = ? and is_del = 0";
            jsonObject1.put("photo", JSONArray.fromObject(Photo.getPhotoURL(getPhotoSQL, product_id, 2)));
            jsonArray.add(jsonObject1);
        }

        jsonObject.put("status", 200);
        jsonObject.put("actualCount", actualCount);
        jsonObject.put("data", jsonArray);

        LogHelper.info("[shop-owner/products/list] " + jsonObject.toString());
        return jsonObject;
    }
}

