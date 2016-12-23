package com.NoWater.customer;

import com.NoWater.model.Cart;
import com.NoWater.model.User;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import net.sf.json.JSONArray;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李鹏飞 on 2016/11/27 0027.
 */
@RestController
public class CustomerIsLogin {
    private Jedis jedis;

    @RequestMapping("/customer/isLogin")
    public JSONObject isLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        String uuid = CookieUtil.getCookieValueByName(request, "token");
        String user_id = CookieUtil.confirmUser(uuid);

        if (user_id == null) {
            jsonObject.put("status", 300);
            return jsonObject;
        }

        jsonObject.put("status", 200);

        JSONObject userInformation = Cart.getUserCartNum(Integer.parseInt(user_id));
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(0, userInformation);
        jsonObject.put("userInformation", jsonArray);

        return jsonObject;
    }
}
