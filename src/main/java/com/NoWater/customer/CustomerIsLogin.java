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
    public JSONObject isLogin(HttpServletRequest request, HttpServletResponse response) throws Exception{
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        JSONObject jsonObject = new JSONObject();
        String uuid  = CookieUtil.getCookieValueByName(request, "token");
        if (uuid != null) {
            jedis = new Jedis("127.0.0.1", 6379);
            String user_id = jedis.get(uuid);
            if(uuid.equals(jedis.get(user_id))) {
                jsonObject.put("status", 200);

                List<Object> list = new ArrayList<Object>();
                DBUtil db = new DBUtil();
                StringBuffer sql = new StringBuffer();
                sql.append("SELECT `user`.`name`,count(cart.user_id) cartNum FROM `user`,cart WHERE `user`.user_id =cart.user_id and cart.user_id = ? GROUP BY cart.user_id");
                list.add(user_id);
                List<Cart> cartList = db.queryInfo(sql.toString(), list, Cart.class);

                JSONObject userInformation = new JSONObject();
                userInformation.put("name", cartList.get(0).getName());
                userInformation.put("cartNum", cartList.get(0).getCartNum());
                JSONArray jsonArray = new JSONArray();
                jsonArray.add(0, userInformation);
                jsonObject.put("userInformation",jsonArray);
            } else
                jsonObject.put("status", 300);
        }   else
            jsonObject.put("status", 300);

        return jsonObject;
    }
}