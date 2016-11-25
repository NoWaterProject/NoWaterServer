package com.NoWater.administrator;

import java.util.ArrayList;
import java.util.List;

import com.NoWater.model.Status;
import com.NoWater.util.DBUtil;
import com.NoWater.util.LogHelper;
import com.NoWater.util.Uuid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wukai on 2016/11/21.
 */
@RestController
public class AdministratorLogin {
    int status;
    String uuid;

    @RequestMapping("/admin/login")
    public Status login(@RequestParam(value = "name", defaultValue = "/") String name,
                        @RequestParam(value = "password", defaultValue = "/") String password,
                        HttpServletResponse response) throws Exception {
        List<Object> list = new ArrayList<Object>();
        DBUtil db = new DBUtil();
        String sql = "select * from admin";
        List<Admin> adminList = db.queryInfo(sql, list, Admin.class);
        if (adminList.get(0).getName() == name && adminList.get(0).getPassword() == password) {
            status = 200;
            uuid = Uuid.getUuid();
            response.addCookie(new Cookie("admin_token", uuid));
            LogHelper.info(name + "login");
        } else {
            status = 300;
        }
        return new Status(status);
    }
}
