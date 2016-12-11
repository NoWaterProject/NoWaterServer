package com.NoWater.customer;

import com.NoWater.model.Photo;
import com.NoWater.model.Product;
import com.NoWater.model.Shop;
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
import java.util.Objects;

/**
 * Created by qingpeng on 2016/12/8.
 */
@RestController
public class CustomerShopClassProduct {
    @RequestMapping("customer/shop/class/product")
    public JSONObject customerShopClassProduct(
            @RequestParam(value = "shopId", defaultValue = "/") int shopId,
            @RequestParam(value = "classId", defaultValue = "0") int classId,
            @RequestParam(value = "count", defaultValue = "/") int count,
            @RequestParam(value = "startId", defaultValue = "0") int startId,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String token = CookieUtil.getCookieValueByName(request, "token");
        JSONObject jsonObject = new JSONObject();
        int actualCount;
        int nextStartId;
        if (token == null) {
            jsonObject.put("status", 300);//用户未登录，一样成功
        } else {
            Jedis jedis = new Jedis("127.0.0.1", 6379);
            String user_id = jedis.get(token);
            if (user_id == null) {
                jsonObject.put("status", 300);//用户未登录，一样成功
            } else {
                String realToken = jedis.get(user_id);
                if (!realToken.equals(token)) {
                    jsonObject.put("status", 300);//用户未登录，一样成功
                }
            }
        }
        DBUtil db = new DBUtil();
        List<Object> getShopExist = new ArrayList<Object>();
        String getShopExistSQL = "select * from products where shop_id = ?";
        getShopExist.add(shopId);
        List<Product> getShopExistList = db.queryInfo(getShopExistSQL,getShopExist,Product.class);


        if (getShopExistList.size() == 0) {
            jsonObject.put("status",500);//商铺不存在
        } else {

            List<Object> getShopClassExist = new ArrayList<>();
            String getShopClassExistSQL = "select * from products where shop_id = ? and class_id = ?";
            getShopClassExist.add(shopId);
            getShopClassExist.add(classId);
            List<Product> getShopClassExistList = db.queryInfo(getShopClassExistSQL,getShopClassExist,Product.class);
            if (classId <1 || classId>9){
                jsonObject.put("status",600);//ClassId错误，不在1-9之间
            } else if (getShopClassExistList.size() == 0){
                jsonObject.put("status",400);//店铺不存在这个类别的商品
            } else {
                if (startId == 0) {  //0代表查找从这个商店某个商品类别从头开始查找
                    String getStartIdSQL = "select count(1) num from products where shop_id = ? and class_id = ?";
                    List<Object> EmptyList = new ArrayList<Object>();
                    List<Product> getStartIdList = db.queryInfo(getStartIdSQL, EmptyList, Product.class);
                    startId = (int) getStartIdList.get(0).getNum();
                }
                List<Object> list = new ArrayList<Object>();
                String getTheRestCount = "select count(1) num from products where shop_id = ? and product_id <= ? and class_id = ?";
                list.add(shopId);
                list.add(startId);
                list.add(classId);
                List<Product> productListCount = db.queryInfo(getTheRestCount, list, Product.class);
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
                    nextStartId = productListInfo.get(actualCount).getProductId();
                    jsonObject.put("nextStartId", nextStartId);
                    jsonObject.put("data", jsonArray);
                } else {
                    actualCount = num;
                    jsonObject.put("nextStartId", -1);
                    jsonObject.put("data", JSONArray.fromObject(productListInfo));
                }
                jsonObject.put("status", 200);
                jsonObject.put("actualCount", actualCount);
            }
        }
        return jsonObject;
    }
}
