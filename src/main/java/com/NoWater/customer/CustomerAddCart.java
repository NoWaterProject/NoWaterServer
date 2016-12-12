package com.NoWater.customer;

import com.NoWater.model.Cart;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.LogHelper;
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
 * Created by wukai on 2016/11/29.
 */
@RestController
public class CustomerAddCart {
    private Jedis jedis;
    DBUtil db = new DBUtil();

    @RequestMapping("/customer/cartAdding")
    public JSONObject CustomerCartAdding(@RequestParam(value = "productId", defaultValue = "0") String productId,
                                         @RequestParam(value = "num", defaultValue = "1") int num,
                                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);
        if (userId == null) {
            jsonObject.put("status", 300);
            return jsonObject;
        }

        String sql = "select * from cart where user_id=? and product_id=?";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(productId);
        List<Cart> cartList = db.queryInfo(sql, params, Cart.class);
        if (cartList.size() == 0) {
            sql = "insert into cart(user_id, product_id, num) values(?,?,?)";
            params.add(num);
            db.insertUpdateDeleteExute(sql, params);
            LogHelper.info("add success.");
            jsonObject.put("status", 200);
        } else {
            sql = "update cart set num=num+? where user_id=? and product_id=?";
            params.clear();
            params.add(num);
            params.add(userId);
            params.add(productId);
            db.insertUpdateDeleteExute(sql, params);
            LogHelper.info("add success.");
            jsonObject.put("status", 200);
        }
        return jsonObject;
    }
}
