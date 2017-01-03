package com.NoWater.administrator;

import com.NoWater.model.Order;
import com.NoWater.util.*;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Koprvhdix on 2016/12/30.
 */
@RestController
public class AdminCrontab {
    @RequestMapping("admin/ad/time/show")
    public JSONObject adminDBBackupTimeShow(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "admin_token");
        String admin = CookieUtil.confirmUser(token);

        if (admin == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[admin/income] %s", jsonObject.toString()));
            return jsonObject;
        }

        Jedis jedis = new Jedis("127.0.0.1", 6379);
        String changeAd = jedis.get("changeAd");
        jsonObject.put("changeAd", changeAd);
        jsonObject.put("status", 200);
        return jsonObject;
    }

    @RequestMapping("admin/ad/time/changing")
    public JSONObject adminDBBackupTimeChanging(@RequestParam(value = "changeAd") String changeAd,
                                                HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "admin_token");
        String admin = CookieUtil.confirmUser(token);

        if (admin == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[admin/income] %s", jsonObject.toString()));
            return jsonObject;
        }

        Jedis jedis = new Jedis("127.0.0.1", 6379);

        jedis.set("changeAd", changeAd);
        String cmd = "/usr/bin/python bin/change_crontab.py " + "1 " + timeUtil.changeToCron(changeAd);
        LogHelper.info("change crontab: " + cmd);
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            proc.waitFor();
        } catch (Exception e) {
            jsonObject.put("status", 1100);
            e.printStackTrace();
        }

        jsonObject.put("status", 200);
        return jsonObject;
    }

    @RequestMapping("admin/checkout/time")
    public JSONObject adminCheckoutTime(@RequestParam(value = "key") String key) {
        JSONObject jsonObject = new JSONObject();
        ArrayList<Integer> orderIdList = new ArrayList<>();
        if (key.equals(NoWaterProperties.getKey())) {
            DBUtil db = new DBUtil();

            int[] statusCount = {-3, 1, 2, 3, 4};

            for (int index = 0; index < statusCount.length; index++) {
                orderIdList.clear();
                String getStatus3 = "select * from `order` where `order_type` in (0, 3) and `status` = " + statusCount[index];
                try {
                    List<Order> orderList = db.queryInfo(getStatus3, new ArrayList<>(), Order.class);
                    for (int i = 0; i < orderList.size(); i++) {
                        String orderTime = orderList.get(i).getTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = sdf.parse(orderTime);
                        Calendar dateDeadline = Calendar.getInstance();
                        dateDeadline.setTime(date);

                        if (statusCount[index] == -3) {
                            dateDeadline.add(Calendar.HOUR, 1);
                        } else if (statusCount[index] == 1) {
                            dateDeadline.add(Calendar.DATE, 1);
                        } else if (statusCount[index] == 2 || statusCount[index] == 3) {
                            dateDeadline.add(Calendar.DATE, 7);
                        } else if (statusCount[index] == 4) {
                            dateDeadline.add(Calendar.DATE, 1);
                        }

                        Calendar currentTime = Calendar.getInstance();
                        if (currentTime.after(dateDeadline)) {
                            orderIdList.add(orderList.get(i).getOrderId());
                        }
                    }

                    String orderId = "(";
                    for (int i = 0; i < orderIdList.size(); i++) {
                        orderId += orderIdList.get(i);
                        if (i != orderIdList.size() - 1)
                            orderId += ", ";
                    }
                    orderId += ")";
                    String updateSQL = "";
                    if (statusCount[index] == -3 && orderId.length() != 2) {
                        updateSQL = "delete from `order` where `order_id` in " + orderId;
                    } else if (statusCount[index] == 1 && orderId.length() != 2) {
                        updateSQL = "update `order` set `status` = 10 where `order_id` in " + orderId;
                    } else if (orderId.length() != 2) {
                        updateSQL = "update `order` set `status` = 5 where `order_id` in " + orderId;
                    }
                    LogHelper.info("updateSQL:" + updateSQL);
                    if (!updateSQL.isEmpty())
                        db.insertUpdateDeleteExute(updateSQL, new ArrayList<>());
                    jsonObject.put("status", 200);
                } catch (Exception e) {
                    e.printStackTrace();
                    return jsonObject;
                }
            }
        } else {
            jsonObject.put("status", 300);
        }

        return jsonObject;
    }
}
