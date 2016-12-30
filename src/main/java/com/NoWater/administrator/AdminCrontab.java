package com.NoWater.administrator;

import com.NoWater.model.Order;
import com.NoWater.util.DBUtil;
import com.NoWater.util.NoWaterProperties;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping("admin/time/show")
    public JSONObject adminDBBackupTimeShow() {
        return new JSONObject();
    }

    @RequestMapping("admin/db/backup/time/changing")
    public JSONObject adminDBBackupTimeChanging() {
        return new JSONObject();
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
                        orderId += ", ";
                    }
                    orderId += ")";
                    String updateSQL;
                    if (statusCount[index] == -3) {
                        updateSQL = "delete from `order` where `order_id` in " + orderId;
                    } else if (statusCount[index] == 1) {
                        updateSQL = "update `order` set `status` = -1 where `order_id` in " + orderId;
                    } else {
                        updateSQL = "update `order` set `status` = 5 where `order_id` in " + orderId;
                    }
                    db.insertUpdateDeleteExute(updateSQL, new ArrayList<>());
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
