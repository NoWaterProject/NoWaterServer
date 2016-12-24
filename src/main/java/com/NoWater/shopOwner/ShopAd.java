package com.NoWater.shopOwner;

import com.NoWater.model.Order;
import com.NoWater.model.Photo;
import com.NoWater.model.Product;
import com.NoWater.model.Shop;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.FileUpload;
import com.NoWater.util.timeUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Koprvhdix on 2016/12/18.
 */
@RestController
public class ShopAd {
    @RequestMapping("/shop-owner/current/apply")
    public JSONObject shopOwnerCurrentApply(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);
        DBUtil db = new DBUtil();

        if (userId == null) {
            jsonObject.put("status", 300);
            return jsonObject;
        }

        int shopId = CookieUtil.confirmShop(userId);

        if (shopId == -1) {
            jsonObject.put("status", 500);      // not shop owner
            return jsonObject;
        }

        if (timeUtil.timeLimit()) {
            jsonObject.put("allow", 0);
        } else {
            String showTime = timeUtil.getShowTime();
            Jedis jedis = new Jedis("127.0.0.1", 6379);
            String hasApply = jedis.get(showTime + String.valueOf(shopId));
            if (hasApply == null)
                jsonObject.put("allow", 1);
            else
                jsonObject.put("allow", 0);
        }

        jsonObject.put("status", 200);
        String getOrderSQL = "select * from `order` where `order_type` = 2 and `target_id` = ? order by `order_id` desc";
        List<Object> objectList = new ArrayList<>();
        objectList.add(shopId);
        JSONArray jsonArray = Order.getShopAdOrder(getOrderSQL, objectList);
        jsonObject.put("data", jsonArray);
        return jsonObject;
    }

    @RequestMapping("/shop-owner/shop/ad/apply")
    public JSONObject shopOwnerShopAdApply(@RequestParam(value = "filename") String filename,
                                           @RequestParam(value = "price") double price,
                                           HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);
        DBUtil db = new DBUtil();

        if (userId == null) {
            jsonObject.put("status", 300);
            return jsonObject;
        }

        int shopId = CookieUtil.confirmShop(userId);

        if (shopId == -1) {
            jsonObject.put("status", 500);      // not shop owner
            return jsonObject;
        }

        if (timeUtil.timeLimit()) {
            jsonObject.put("status", 600);      // 超时
            return jsonObject;
        }

        ArrayList<String> addFileNameList = new ArrayList<>();
        addFileNameList.add(filename);
        int status = FileUpload.UploadToCOS(addFileNameList, userId, String.valueOf(shopId), 0);
        if (status == -1) {
            jsonObject.put("status", 1010);
            return jsonObject;
        }

        String pat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pat);
        String currentTime = sdf.format(new Date());

        String showTime = timeUtil.getShowTime();

        String insertOrder = "insert into `order` (`order_type`, `time`, `initiator_id`, `target_id`, `num`, `price`, `sum_price`, `photo_id`, `show_time`, `status`) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object> objectList = new ArrayList<>();
        objectList.add(2);
        objectList.add(currentTime);
        objectList.add(Integer.parseInt(userId));
        objectList.add(shopId);
        objectList.add(1);
        objectList.add(price);
        objectList.add(price);
        objectList.add("http://koprvhdix117-10038234.file.myqcloud.com/" + filename);
        objectList.add(showTime);
        objectList.add(1);
        db.insertUpdateDeleteExute(insertOrder, objectList);

        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.set(showTime + String.valueOf(shopId), "true");
        jedis.expire(showTime + String.valueOf(shopId), 86400);

        jsonObject.put("status", 200);
        return jsonObject;
    }

    @RequestMapping("/shop-owner/shop/ad/list")
    public JSONObject shopOwnerShopAdList(@RequestParam(value = "startId", defaultValue = "0") int startId,
                                          @RequestParam(value = "count") int count,
                                          HttpServletRequest request, HttpServletResponse response) {
        return new JSONObject();
    }

    @RequestMapping("/shop-owner/product/ad/apply")
    public JSONObject shopOwnerProductAdApply(@RequestParam(value = "productId") int productId,
                                              HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);

        JSONObject jsonObject = new JSONObject();
        if (userId == null) {
            jsonObject.put("status", 300);
            return jsonObject;
        }

        int shopId = CookieUtil.confirmShop(userId);
        if (shopId == -1) {
            jsonObject.put("status", 500);      // not shop owner
            return jsonObject;
        }

        boolean confirmProduct = Product.confirmProductShop(shopId, productId);
        if (!confirmProduct) {
            jsonObject.put("status", 2400);
            return jsonObject;
        }

        return new JSONObject();
    }

    @RequestMapping("/shop-owner/product/ad/list")
    public JSONObject shopOwnerProductAdList(@RequestParam(value = "startId", defaultValue = "0") int startId,
                                             @RequestParam(value = "count") int count,
                                             HttpServletRequest request, HttpServletResponse response) {
        return new JSONObject();
    }
}
