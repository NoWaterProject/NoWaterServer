package com.NoWater.shopOwner;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Koprvhdix on 2016/12/20.
 */
@RestController
public class ShopOwnerOrder {
    @RequestMapping("/shop-owner/order/list")
    public JSONObject shopOwnerOrderList(@RequestParam(value = "status", defaultValue = "0") int status,
                                         HttpServletRequest request, HttpServletResponse response) {

    }

    @RequestMapping("/shop-owner/order/delivery")
    public JSONObject shopOwnerOrderDelivery(@RequestParam(value = "orderId", defaultValue = "0") int orderId,
                                             HttpServletRequest request, HttpServletResponse response) {

    }
}
