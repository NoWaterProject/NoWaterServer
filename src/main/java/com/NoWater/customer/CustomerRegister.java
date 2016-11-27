package com.NoWater.customer;

import com.NoWater.model.Status;
import com.NoWater.model.User;
import com.NoWater.util.DBUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李鹏飞 on 2016/11/27 0027.
 */
@RestController
public class CustomerRegister {
    @RequestMapping("/customer/register")
    public Status login(@RequestParam(value = "name", defaultValue = "/") String name,
                        @RequestParam(value = "password", defaultValue = "/") String password,
                        @RequestParam(value = "telephone", defaultValue = "/") String telephone,
                        @RequestParam(value = "address1", defaultValue = "/") String address1,
                        @RequestParam(value = "address2", defaultValue = "/") String address2,
                        @RequestParam(value = "address3", defaultValue = "/") String address3,
                        @RequestParam(value = "address4", defaultValue = "/") String address4,
                        HttpServletResponse response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        int status = 0;

        List<Object> list = new ArrayList<Object>();
        DBUtil db = new DBUtil();
        StringBuffer sql = new StringBuffer();
        sql.append("insert into user (name,password,telephone,address1,address2,address3,address4) values (?,?,?,?,?,?,?)");
        list.add(name);
        list.add(password);
        list.add(telephone);
        list.add(address1);
        list.add(address2);
        list.add(address3);
        list.add(address4);
        db.insertUpdateDeleteExute(sql.toString(),list);

        return new Status(status);
    }

}