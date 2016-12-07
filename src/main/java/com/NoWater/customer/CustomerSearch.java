package com.NoWater.customer;

import com.NoWater.model.Product;
import com.NoWater.util.DBUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by SL on 2016/12/5.
 */

@RestController
public class CustomerSearch {
    @RequestMapping("customer/product/search")
    public JSONArray productSearch(@RequestParam(value = "keyWord", defaultValue = "/") String keyWord,
                                   @RequestParam(value = "count",defaultValue = "/") int count,
                                   @RequestParam(value = "shopId", required = false, defaultValue = "0") int shopId,
                                   @RequestParam(value = "startId", required = false, defaultValue = "0") int startId,
                                   HttpServletResponse response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        JSONArray jsonArray = new JSONArray();
        List<Product> productsList = new ArrayList<Product>();
        int actualCount = 0;
        int endId = startId;
        DBUtil db = new DBUtil();
        List<Object> list = new ArrayList<Object>();
        String keyWords = keyWord.replaceAll(" ", "");              //去掉关键字的空格，用于模糊查询
        String sql = null;

        if (shopId != 0) {                                             //店铺搜索
            sql = "select * from products where product_name like ? and shop_id = ? order by product_id desc";
            list.add(keyWords);
            list.add(shopId);
            productsList = db.queryInfo(sql, list, Product.class);
        } else {                                                     //全站搜索
            sql = "select * from products where product_name like ? order by product_id desc";
            list.add(keyWords);
            productsList = db.queryInfo(sql, list, Product.class);
        }

        actualCount = productsList.size();                          //实际查询到的数量

        if (endId != -1) {
            if (actualCount != 0) {                                 //查到数据
                if ((actualCount - startId > 0) && (actualCount - startId <= count)) {        //数据到最后一页
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("status", 400);
                    jsonObject.put("endId", -1);
                    jsonObject.put("actualCount", actualCount);
                    jsonArray.add(jsonObject);
                    jsonArray.add(JSONArray.fromObject(productsList.subList(startId, actualCount)));
                    return jsonArray;
                }else {                                            //数据没到最后一页
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("status", 200);
                    jsonObject.put("endId", startId + count);
                    jsonObject.put("actualCount", actualCount);
                    jsonArray.add(jsonObject);
                    jsonArray.add(JSONArray.fromObject(productsList.subList(startId, startId + count)));
                    return jsonArray;
                }
            }else {                                                  //没查到数据，endId返回-1
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("status", 200);
                jsonObject.put("endId", -1);
                jsonObject.put("actualCount", actualCount);
                jsonArray.add(jsonObject);
                return jsonArray;
            }
        }else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", 400);
            jsonObject.put("endId", -1);
            jsonObject.put("actualCount", actualCount);
            jsonArray.add(jsonObject);
            return jsonArray;
        }
    }
}
