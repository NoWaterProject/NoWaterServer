package com.NoWater.shopOwner;

import com.NoWater.model.Shop;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.LogHelper;
import com.NoWater.util.OrderUtil;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Koprvhdix on 2016/12/20.
 */
@RestController
public class ShopOwnerOrder {
    @RequestMapping("/shop-owner/order/list")
    public JSONObject shopOwnerOrderList(@RequestParam(value = "status", defaultValue = "0") int status,
                                         HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        LogHelper.info(String.format("[/shop-owner/order/list] [param] [status: %s]", status));

        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);

        if (userId == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[/shop-owner/order/list] %s", jsonObject.toString()));
            return jsonObject;
        }

        int shopId = CookieUtil.confirmShop(userId);

        if (shopId == -1) {
            jsonObject.put("status", 500);      // not shop owner
            return jsonObject;
        }

        String getOrderDetailSQL = "select * from `order` where `target_id` = ? and `status` = ?";
        List<Object> getOrderDetailList = new ArrayList<>();
        getOrderDetailList.add(shopId);
        getOrderDetailList.add(status);
        jsonObject.put("status", 200);
        jsonObject.put("data", OrderUtil.getOrderDetail(getOrderDetailSQL, getOrderDetailList, status));
        LogHelper.info(String.format("[/shop-owner/order/list] %s", jsonObject.toString()));
        return jsonObject;
    }

    @RequestMapping("/shop-owner/order/delivery")
    public JSONObject shopOwnerOrderDelivery(@RequestParam(value = "orderId", defaultValue = "0") int orderId,
                                             @RequestParam(value = "express") String express,
                                             @RequestParam(value = "expressCode") String expressCode,
                                             HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        DBUtil db = new DBUtil();

        LogHelper.info(String.format("[/shop-owner/order/delivery] [param] [orderId: %s]", orderId));

        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);

        if (userId == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[/shop-owner/order/delivery] %s", jsonObject.toString()));
            return jsonObject;
        }

        int shopId = CookieUtil.confirmShop(userId);
        if (shopId == -1) {
            jsonObject.put("status", 500);          // user not shop owner
            return jsonObject;
        }

        int statusConfirmOrder = OrderUtil.confirmOrderUserId(orderId, userId, 2, 2);
        if (statusConfirmOrder != 200) {
            jsonObject.put("status", statusConfirmOrder);
            return jsonObject;
        }

        String updateOrder = "update `order` set `status` = 3, `express` = ?, `express_code` = ? where `order_id` = ?";
        List<Object> updateOrderList = new ArrayList<>();
        updateOrderList.add(express);
        updateOrderList.add(expressCode);
        updateOrderList.add(orderId);
        db.insertUpdateDeleteExute(updateOrder, updateOrderList);
        jsonObject.put("status", 200);
        return jsonObject;
    }
}
