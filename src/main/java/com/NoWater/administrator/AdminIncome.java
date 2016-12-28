package com.NoWater.administrator;

import com.NoWater.util.CookieUtil;
import com.NoWater.util.LogHelper;
import com.NoWater.util.OrderUtil;
import com.NoWater.util.timeUtil;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 李鹏飞 on 2016/12/28 0028.
 */
@RestController
public class AdminIncome {
    @RequestMapping("admin/income")
    public JSONObject AdminIncome(@RequestParam(value = "timeFilter", defaultValue = "0") int timeFilter,
                                  @RequestParam(value = "beginTime", defaultValue = "1970-01-01") String beginTime,
                                  @RequestParam(value = "endTime", defaultValue = "-1") String endTime,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        double income;
        String token = CookieUtil.getCookieValueByName(request, "admin_token");
        String admin = CookieUtil.confirmUser(token);

        if (admin == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[admin/income] %s", jsonObject.toString()));
            return jsonObject;
        }
        if (endTime.equals("-1")) {
            endTime = timeUtil.getShowTime();
        }
        income = OrderUtil.getIncome(0, timeFilter, beginTime, endTime);

        jsonObject.put("status", 200);
        jsonObject.put("income", income);
        LogHelper.info(String.format("[admin/income] %s", jsonObject.toString()));
        return jsonObject;
    }
}
