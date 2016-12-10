package com.NoWater.customer;

import com.NoWater.model.Photo;
import com.NoWater.model.Product;
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
public class CustomerShopClassList {
    @RequestMapping("customer/shop/class/list")
    public JSONObject customerShopClassList(
            @RequestParam(value = "shop_id", defaultValue = "0") int shop_id,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String token = CookieUtil.getCookieValueByName(request, "token");
        JSONObject jsonObject = new JSONObject();

        if (token == null) {
            jsonObject.put("status", 300);
        } else {
            Jedis jedis = new Jedis("127.0.0.1", 6379);
            String user_id = jedis.get(token);
            if (user_id == null) {
                jsonObject.put("status", 300);
            } else {
                String realToken = jedis.get(user_id);
                if (!realToken.equals(token)) {
                    jsonObject.put("status", 300);
                }
            }
        }
        if (shop_id <= 0) {
            jsonObject.put("status", 400);
        } else {
            DBUtil db = new DBUtil();
            List<Object> list = new ArrayList<Object>();
            String queryCount = "select count(distinct class_id) num from products where shop_id = ?";
            list.add(shop_id);
            List<Product> getProductClassCount = db.queryInfo(queryCount, list, Product.class);
            int classCount = (int) getProductClassCount.get(0).getNum();
            String queryClass = "select distinct class_id,class_name from products where shop_id = ?";
            list.add(shop_id);
            List<Product> getProductClass = db.queryInfo(queryClass, list, Product.class);

            if (getProductClass.size() == 0) {
                jsonObject.put("status", 400);
            } else {
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < classCount; i++) {
                    JSONObject jsonObject1 = JSONObject.fromObject(getProductClass.get(i));
                    jsonArray.add(jsonObject1);
                }
                jsonObject.put("data", jsonArray);
            }
        }
        return jsonObject;
    }
}
