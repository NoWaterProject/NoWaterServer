package com.NoWater.administrator;

import java.util.ArrayList;
import java.util.List;

import com.NoWater.model.Admin;
import com.NoWater.model.Status;
import com.NoWater.util.DBUtil;
import com.NoWater.util.LogHelper;
import com.NoWater.util.Uuid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wukai on 2016/11/21.
 */
@RestController
public class AdministratorLogin {
    private Jedis jedis;

    @RequestMapping("/admin/login")
    public Status login(@RequestParam(value = "name", defaultValue = "/") String name,
                        @RequestParam(value = "password", defaultValue = "/") String password,
                        HttpServletResponse response) throws Exception {
        int status;
        String uuid;

        List<Object> list = new ArrayList<Object>();
        DBUtil db = new DBUtil();
        String sql = "select * from admin";
        List<Admin> adminList = db.queryInfo(sql, list, Admin.class);

        if (adminList.get(0).getName().equals(name) && adminList.get(0).getPassword().equals(password)) {
            status = 200;

            uuid = Uuid.getUuid();
            Cookie cookie = new Cookie("admin_token", uuid);
            cookie.setMaxAge(1800);
            cookie.setPath("/");
            response.addCookie(cookie);
            jedis = new Jedis("127.0.0.1", 6379);
            jedis.set(uuid, name);
            jedis.set(name, uuid);
            jedis.expire(uuid, 1800);
            jedis.expire(name, 1800);

            LogHelper.info(name + "\tlogin");
        } else {
            status = 300;
        }
        return new Status(status);
    }
}
