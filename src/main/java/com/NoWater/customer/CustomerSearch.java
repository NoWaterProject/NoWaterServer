package com.NoWater.customer;

import com.NoWater.model.Product;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.LogHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by SL on 2016/12/5.
 */

@RestController
public class CustomerSearch {
    @RequestMapping("customer/product/search")
    public JSONObject productSearch(@RequestParam(value = "keyWord", defaultValue = "/") String keyWord,
                                    @RequestParam(value = "count", defaultValue = "/") int count,
                                    @RequestParam(value = "shopId", required = false, defaultValue = "0") int shopId,
                                    @RequestParam(value = "startId", required = false, defaultValue = "0") int startId,
                                    HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        JSONObject jsonObject = new JSONObject();

        List<Product> productsList;
        DBUtil db = new DBUtil();
        List<Object> list = new ArrayList<>();
        String[] keyWords = keyWord.split(" ");
        StringBuffer sql = new StringBuffer("select * from products where ");

        for (int i = 0; i < keyWords.length; i++) {
            sql.append("product_name like '%?%' and ");
            list.add(keyWords[i]);
        }

        if (shopId != 0) {
            //店铺搜索
            sql.append("shop_id = ? and ");
            list.add(shopId);
        }

        if (startId == -1) {
            jsonObject.put("status", 400);
            jsonObject.put("startId", -1);
            jsonObject.put("actualCount", 0);
            return jsonObject;
        } else if (startId != 0) {
            sql.append("product_id <= ? and ");
            list.add(startId);
        }

        sql.append("is_del = 0 order by product_id desc");
        LogHelper.info(sql.toString());
        productsList = db.queryInfo(sql.toString(), list, Product.class);
        int actualCount = productsList.size();      //实际查询到的数量

        if (actualCount > count) {
            List<Product> data = productsList.subList(0, count);
            jsonObject.put("actualCount", count);
            jsonObject.put("startId", productsList.get(count).getProductId());
            jsonObject.put("data", JSONArray.fromObject(data));
        } else {
            jsonObject.put("actualCount", actualCount);
            jsonObject.put("startId", -1);
            jsonObject.put("data", JSONArray.fromObject(productsList));
        }

        jsonObject.put("status", 200);

        return jsonObject;
    }
}
