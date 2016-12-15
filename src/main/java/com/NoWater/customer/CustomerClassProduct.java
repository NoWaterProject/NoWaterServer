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
public class CustomerClassProduct {
    @RequestMapping("customer/class/product")
    public JSONObject customerShopClassProduct(
            @RequestParam(value = "shopId", defaultValue = "0") int shopId,
            @RequestParam(value = "classId", defaultValue = "0") int classId,
            @RequestParam(value = "count", defaultValue = "/") int count,
            @RequestParam(value = "startId", defaultValue = "0") int startId,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        if (classId > 9 && classId < 0) {
            jsonObject.put("status", 600);  // 类别错误
            return jsonObject;
        }

        if (shopId != 0) {
            DBUtil db = new DBUtil();
            List<Object> getShopExist = new ArrayList<>();
            String getShopExistSQL = "select * from shop where shop_id = ?";
            getShopExist.add(shopId);
            List<Product> getShopList = db.queryInfo(getShopExistSQL, getShopExist, Product.class);
            if (getShopList.size() == 0) {
                jsonObject.put("status", 500);
                return jsonObject;
            }
        }

        StringBuffer getProductSQL = new StringBuffer();
        List<Object> getProductSQLlist = new ArrayList<>();
        if (shopId == 0) {                  //  全局搜索
            if (classId == 0) {             //  全部类别
                getProductSQL.append("select * from products");
            } else {                        //  个别类别
                getProductSQL.append("select * from products where class_id = ?");
                getProductSQLlist.add(classId);
            }
        } else {                            //  某个店铺搜素
            if (classId == 0) {
                getProductSQL.append("select * from products where shop_id = ?");
                getProductSQLlist.add(shopId);
            } else {
                getProductSQL.append("select * from products where shop_id = ? and class_id = ?");
                getProductSQLlist.add(shopId);
                getProductSQLlist.add(classId);
            }
        }

        if (startId != 0) {
            getProductSQL.append(" and product_id <= ?");
            getProductSQLlist.add(startId);
        }

        getProductSQL.append(" and is_del = 0");

        DBUtil db = new DBUtil();
        List<Product> productList = db.queryInfo(getProductSQL.toString(), getProductSQLlist, Product.class);

        int actualCount;
        int nextStartId;
        if (productList.size() < count) {
            actualCount = productList.size();
            nextStartId = -1;
        } else {
            actualCount = count;
            nextStartId = productList.get(actualCount).getProductId();
        }

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < actualCount; i++) {
            JSONObject jsonObject1 = JSONObject.fromObject(productList.get(i));

            int product_id = productList.get(i).getProductId();

            String getPhotoSQL = "select * from photo where belong_id = ? and photo_type = ? and is_del = 0";

            jsonObject1.put("photoUrl", JSONArray.fromObject(Photo.getPhotoURL(getPhotoSQL, product_id, 2)));
            jsonArray.add(jsonObject1);
        }

        jsonObject.put("status", 200);
        jsonObject.put("actualCount", actualCount);
        jsonObject.put("startId", nextStartId);
        jsonObject.put("data", jsonArray);

        return jsonObject;
    }
}
