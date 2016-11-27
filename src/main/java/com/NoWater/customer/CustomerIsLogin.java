package com.NoWater.customer;

import com.NoWater.model.Status;
import com.NoWater.util.CookieUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 李鹏飞 on 2016/11/27 0027.
 */
@RestController
public class CustomerIsLogin {
    private Jedis jedis;
    @RequestMapping("/customer/isLogin")
    public Status idlogin(HttpServletRequest request, HttpServletResponse response) throws Exception{
        //response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        int status ;
        String uuid  = CookieUtil.getCookieValueByName(request,"token");
        if (uuid !=null) {
            jedis = new Jedis("127.0.0.1", 6379);
            String user_id = jedis.get(uuid);
            if(uuid.equals(jedis.get(user_id))) {
                status = 200;
            } else
                status = 300;
        } else
            status = 300;

        return new Status(status);
    }
}
