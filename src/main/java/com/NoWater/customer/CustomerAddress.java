package com.NoWater.customer;

import com.NoWater.model.Address;
import com.NoWater.model.Product;
import com.NoWater.model.User;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李鹏飞 on 2016/12/26 0026.
 */
@RestController
public class CustomerAddress {

    @RequestMapping("customer/address/edit")
    public JSONObject CustomerAddressEdit(
            @RequestParam(value = "addressId", defaultValue = "-1") int addressId,
            @RequestParam(value = "telephone", defaultValue = "/") String telephone,
            @RequestParam(value = "address1", defaultValue = "/") String address1,
            @RequestParam(value = "address2", defaultValue = "/") String address2,
            @RequestParam(value = "address3", defaultValue = "/") String address3,
            @RequestParam(value = "postCode", defaultValue = "710126") String postCode,
            @RequestParam(value = "firstName", defaultValue = "") String firstName,
            @RequestParam(value = "lastName", defaultValue = "") String lastName,
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

        if (addressId != -1) { // 添加address

            List<Object> list = new ArrayList<>();
            DBUtil db = new DBUtil();
            StringBuffer sql = new StringBuffer();
            sql.append("insert into address (telephone,address1,address2,address3,post_code,first_name,last_name,user_id) values (?,?,?,?,?,?,?,?)");
            list.add(telephone);
            list.add(address1);
            list.add(address2);
            list.add(address3);
            list.add(postCode);
            list.add(firstName);
            list.add(lastName);
            list.add(userId);
            try {
                db.insertUpdateDeleteExute(sql.toString(), list);
                jsonObject.put("status", 200);//添加成功
                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
                jsonObject.put("status", 1000);
                return jsonObject;
            }

        } else {  // 传入addressId  修改address

            List<Object> list = new ArrayList<>();
            DBUtil db = new DBUtil();
            String sql = "select user_id from address where address_id = ?";
            list.add(addressId);
            List<Address> userIdList = db.queryInfo(sql, list, Address.class);
            if (userIdList.size() == 0) {
                jsonObject.put("status", 400);//没有此addressId
                return jsonObject;
            } else if (userIdList.get(0).getUserId() != Integer.parseInt(userId)) {
                jsonObject.put("status", 500);//用户名和addressId不匹配
                return jsonObject;
            } else {
                List<Object> list1 = new ArrayList<>();
                DBUtil db1 = new DBUtil();
                StringBuffer sql1 = new StringBuffer();
                sql1.append("update address SET telephone = ?,address1 = ?,address2 = ?,address3 = ?,post_code = ?,first_name = ?,last_name = ? where address_id = ?");
                list1.add(telephone);
                list1.add(address1);
                list1.add(address2);
                list1.add(address3);
                list1.add(postCode);
                list1.add(firstName);
                list1.add(lastName);
                list1.add(addressId);
                try {
                    db1.insertUpdateDeleteExute(sql1.toString(), list1);
                    jsonObject.put("status", 200);//更新成功
                    return jsonObject;
                } catch (Exception e) {
                    jsonObject.put("status", 1000);
                    e.printStackTrace();
                    return jsonObject;
                }
            }
        }
    }

    @RequestMapping("customer/address/detail")
    public JSONObject CustomerAddressDetail(
            @RequestParam(value = "addressId") int addressId,
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

        DBUtil db = new DBUtil();
        List<Object> list = new ArrayList<>();
        String sql = "select * from address where address_id = ?";
        list.add(addressId);
        List<Address> addressList = db.queryInfo(sql, list, Address.class);
        JSONArray jsonArray = JSONArray.fromObject(addressList);
        jsonObject.put("status", 200);
        jsonObject.put("data", jsonArray);

        return jsonObject;
    }

    @RequestMapping("customer/address/list")
    public JSONObject CustomerAddressList(
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

        DBUtil db = new DBUtil();
        List<Object> list = new ArrayList<>();
        String sql = "select * from address where user_id = ?";
        list.add(userId);
        List<Address> addressList = db.queryInfo(sql, list, Address.class);
        jsonObject.put("status", 200);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < addressList.size(); i++) {
            String address = addressList.get(i).getFirstName() + " " + addressList.get(i).getLastName() + ", "
                    + addressList.get(i).getTelephone() + ", " + addressList.get(i).getPostCode() + ", "
                    + addressList.get(i).getAddress1() + ", " + addressList.get(i).getAddress2() + ", "
                    + addressList.get(i).getAddress3();
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("address",address);
            jsonArray.add(jsonObject1);
        }
        jsonObject.put("data",jsonArray);

        return jsonObject;
    }


}