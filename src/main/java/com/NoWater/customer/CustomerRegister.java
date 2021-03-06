package com.NoWater.customer;

import com.NoWater.model.Cart;
import com.NoWater.util.NoWaterProperties;
import com.NoWater.model.User;
import com.NoWater.util.DBUtil;
import com.NoWater.util.LogHelper;
import net.sf.json.JSONObject;
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
    public JSONObject register(@RequestParam(value = "name") String name,
                               @RequestParam(value = "password") String password,
                               @RequestParam(value = "telephone") String telephone,
                               @RequestParam(value = "address1") String address1,
                               @RequestParam(value = "address2") String address2,
                               @RequestParam(value = "address3") String address3,
                               @RequestParam(value = "postCode") String postCode,
                               @RequestParam(value = "firstName") String firstName,
                               @RequestParam(value = "lastName") String lastName,
                               HttpServletResponse response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        LogHelper.info(String.format("[customer/register] [param] name:%s, password:%s, telephone:%s, address1:%s, address2:%s, address3:%s",
                name, password, telephone, address1, address2, address3));
        int status = 0;
        JSONObject jsonObject = new JSONObject();
        List<Object> list = new ArrayList<>();
        DBUtil db = new DBUtil();
        StringBuffer sql = new StringBuffer();
        sql.append("select user_id from user where name = ?");
        list.add(name);
        List<User> userList = db.queryInfo(sql.toString(), list, User.class);
        if (userList.size() != 0) {
            status = 300; //用户名已存在。
            jsonObject.put("status", status);
        } else {
            try {
                int first = Integer.parseInt(telephone.substring(0, 1));
                if (telephone.length() != 8) {
                    status = 400; //电话号码不合格
                    jsonObject.put("status", status);
                } else {
                    String[] addRess1 = NoWaterProperties.getAddress();
                    String[][] addRess2 = NoWaterProperties.getAddress2();
                    int addressStatus = 0;
                    for (int i = 0; i < addRess1.length; i++) {
                        if (address1.equals(addRess1[i])) {
                            for (int j = 0; j < addRess2[i].length; j++) {
                                if (address2.equals(addRess2[i][j])) {
                                    addressStatus = 1;
                                    break;
                                }
                            }
                            break;
                        }
                    }

                    if (addressStatus == 1) {
                        List<Object> list1 = new ArrayList<>();
                        DBUtil db1 = new DBUtil();
                        StringBuffer sql1 = new StringBuffer();
                        sql1.append("insert into user (name,password,telephone,address1,address2,address3,post_code, first_name, last_name) values (?,?,?,?,?,?,?,?,?)");
                        list1.add(name);
                        list1.add(password);
                        list1.add(telephone);
                        list1.add(address1);
                        list1.add(address2);
                        list1.add(address3);
                        list1.add(postCode);
                        list1.add(firstName);
                        list1.add(lastName);
                        try {
                            db1.insertUpdateDeleteExute(sql1.toString(), list1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            jsonObject.put("status", 1000);
                            return jsonObject;
                        }

                        String getUserId = "select * from `user` where `name` = ?";
                        List<Object> getUserIdList = new ArrayList<>();
                        getUserIdList.add(name);
                        try {
                            List<User> userList1 = db.queryInfo(getUserId, getUserIdList, User.class);
                            int userId = userList1.get(0).getUserId();
                            String insertAddress = "insert into `address` (`telephone`, `address1`, `address2`,`address3`,`post_code`,`first_name`,`last_name`,`user_id`,`is_default`) values (?,?,?,?,?,?,?,?,?)";
                            List<Object> objectList = new ArrayList<>();
                            objectList.add(telephone);
                            objectList.add(address1);
                            objectList.add(address2);
                            objectList.add(address3);
                            objectList.add(postCode);
                            objectList.add(firstName);
                            objectList.add(lastName);
                            objectList.add(userId);
                            objectList.add(1);

                            db.insertUpdateDeleteExute(insertAddress, objectList);
                        } catch (Exception e) {
                            e.printStackTrace();
                            jsonObject.put("status", 1100);
                            return jsonObject;
                        }

                        status = 200;
                        jsonObject.put("status", status);
                        LogHelper.info("register: name=" + name + "password=" + password + "telephone=" + telephone + "address1=" + address1 + "address2=" + address2 + "addres3=" + address3);
                    } else {
                        status = 500;
                        jsonObject.put("status", status);
                    }
                }
            } catch (Exception e) {
                LogHelper.error(e.toString());
                jsonObject.put("status", 400);
            }
        }
        return jsonObject;
    }

}