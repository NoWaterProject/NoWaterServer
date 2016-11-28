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
    public JSONObject register(@RequestParam(value = "name", defaultValue = "/") String name,
                            @RequestParam(value = "password", defaultValue = "/") String password,
                            @RequestParam(value = "telephone", defaultValue = "/") String telephone,
                            @RequestParam(value = "address1", defaultValue = "/") String address1,
                            @RequestParam(value = "address2", defaultValue = "/") String address2,
                            @RequestParam(value = "address3", defaultValue = "/") String address3,
                            HttpServletResponse response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        int status = 0;
        JSONObject jsonObject = new JSONObject();
        List<Object> list = new ArrayList<Object>();
        DBUtil db = new DBUtil();
        StringBuffer sql = new StringBuffer();
        sql.append("select user_id from user where name = ?");
        list.add(name);
        List<User> userList = db.queryInfo(sql.toString(), list, User.class);
        if(userList.size()!=0){
            status = 300; //用户名已存在。
            jsonObject.put("status",status);
        }   else {
            int first = Integer.parseInt(telephone.substring(0,1));
            if (telephone.length()!=8||(first!=9&&first!=6)){
                status = 400; //电话号码不合格
                jsonObject.put("status",status);
            }   else {
                //// TODO: 2016/11/28 0028 address判断
                String[] addRess1 = NoWaterProperties.getAddress();
                String [][] addRess2 = NoWaterProperties.getAddress2();
                int addressStatus = 0;
                for (int i = 0; i < addRess1.length ; i++) {
                    if (address1.equals(addRess1[i])) {
                        for (int j = 0; j < addRess2.length ; j++) {
                            if (address2.equals(addRess2[i][j])) {
                                addressStatus = 1;
                                break;
                            }
                        }
                        break;
                    }
                }

                if (addressStatus == 1) {
                    List<Object> list1 = new ArrayList<Object>();
                    DBUtil db1 = new DBUtil();
                    StringBuffer sql1 = new StringBuffer();
                    sql1.append("insert into user (name,password,telephone,address1,address2,address3) values (?,?,?,?,?,?)");
                    list1.add(name);
                    list1.add(password);
                    list1.add(telephone);
                    list1.add(address1);
                    list1.add(address2);
                    list1.add(address3);
                    db1.insertUpdateDeleteExute(sql1.toString(),list1);
                    status = 200;
                    jsonObject.put("status",status);
                    LogHelper.info("register: name="+name +"password="+password+"telephone="+telephone+"address1="+address1+"address2="+address2+"addres31="+address3);

                }   else {
                    status = 500 ;
                    jsonObject.put("status",status);
                }
            }
        }
        return jsonObject;
    }

}