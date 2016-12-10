package com.NoWater.shopOwner;

import com.NoWater.model.Favorite;
import com.NoWater.model.Product;
import com.NoWater.model.Photo;

import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
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
 * Created by qingpeng on 2016/12/8.
 */
@RestController
public class ShopOwnerProductsDetail {
    @RequestMapping("shop-owner/products/detail")
    public JSONObject productsDetail(
            @RequestParam(value = "product_id", defaultValue = "0") int product_id,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");

//        String token = CookieUtil.getCookieValueByName(request, "token");
        JSONObject jsonObject = new JSONObject();
        if (product_id <= 0) {
            jsonObject.put("status",300);
        } else {
            DBUtil db = new DBUtil();
            List<Object> list = new ArrayList<Object>();
            String queryProduct = "select * from products where product_id = ?";
            list.add(product_id);
            List<Product> productList = db.queryInfo(queryProduct, list, Product.class);
            jsonObject.put("data", JSONArray.fromObject(productList));

            String queryPhoto = "select * from photo where product_id = ? and photo_type = 2";
            list.add(product_id);
            List<Photo> photoList = db.queryInfo(queryPhoto,list,Photo.class);
            jsonObject.put("photo",JSONObject.fromObject(photoList));
            jsonObject.put("status",200);
        }
        return jsonObject;
    }
}
