package com.NoWater.administrator;

import com.NoWater.model.Payment;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.LogHelper;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Koprvhdix on 2016/12/18.
 */
@RestController
public class AdminOrder {
    @RequestMapping("admin/payment/confirm")
    public JSONObject paymentConfirm(@RequestParam(value = "paymentId") int paymentId,
                                          HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        LogHelper.info(String.format("[admin/payment/confirm] [param] [paymentId:%s]", paymentId));

        String token = CookieUtil.getCookieValueByName(request, "admin_token");
        String admin = CookieUtil.confirmUser(token);

        if (admin == null) {
            jsonObject.put("status", 300);
            return jsonObject;
        }

        String confirmPaymentIdSQL = "select * from `payment` where `payment_id` = ? and `status` = 0";
        List<Object> confirmPaymentList = new ArrayList<>();
        confirmPaymentList.add(paymentId);
        List<Payment> getPaymentId;
        DBUtil db = new DBUtil();
        try{
            getPaymentId = db.queryInfo(confirmPaymentIdSQL, confirmPaymentList, Payment.class);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
            return jsonObject;
        }

        if (getPaymentId.size() == 0) {
            jsonObject.put("status", 400);  //  paymentId not exist, or payment has confirmed
            return jsonObject;
        }

        String updatePaymentSQL = "update `payment` set `status` = 1 where `payment_id` = ?";
        db.insertUpdateDeleteExute(updatePaymentSQL, confirmPaymentList);
        String updateOrderSQL = "update `order` set `status` = 2 where `payment_id` = ?";
        db.insertUpdateDeleteExute(updateOrderSQL, confirmPaymentList);

        jsonObject.put("status", 200);
        return jsonObject;
    }

    @RequestMapping("admin/payment/list")
    public JSONObject paymentList(@RequestParam(value = "count") int count,
                                  @RequestParam(value = "startId", defaultValue = "0") int startId,
                                  HttpServletRequest request, HttpServletResponse response) {
        
    }
}
