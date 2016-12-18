package com.NoWater.administrator;

import com.NoWater.util.CookieUtil;
import com.NoWater.util.LogHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Koprvhdix on 2016/12/18.
 */
@RestController
public class AdminCommission {
    @RequestMapping("/admin/commission/show")
    public JSONObject adminCommissionShow(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "admin_token");
        String admin = CookieUtil.confirmUser(token);

        if (admin == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[admin/commission/show] %s", jsonObject.toString()));
            return jsonObject;
        }

        Jedis jedis = new Jedis("127.0.0.1", 6379);
        String commission = jedis.get("commission");

        jsonObject.put("commission", commission);
        LogHelper.info(String.format("[admin/commission/show] %s", jsonObject.toString()));
        return jsonObject;
    }

    @RequestMapping("/admin/commission/changing")
    public JSONObject adminCommissionEdit(@RequestParam(value = "commission") String commission,
                                          HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "admin_token");
        String admin = CookieUtil.confirmUser(token);

        if (admin == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[admin/commission/changing] %s", jsonObject.toString()));
            return jsonObject;
        }

        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.set("commission", commission);

        jsonObject.put("status", 200);
        LogHelper.info(String.format("[admin/commission/changing] %s", jsonObject.toString()));
        return jsonObject;
    }
}
