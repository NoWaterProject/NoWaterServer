package com.NoWater.order;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.NoWater.util.LogHelper;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by wukai on 16-11-18.
 */
@RestController
public class Order {
    @RequestMapping("/order/prepare")
    public JSONObject greeting(@RequestParam(value="orderType", defaultValue="3" ) int orderType,
                               @RequestParam(value="productId", defaultValue="0") int productId,
                               @RequestParam(value="price", defaultValue="0.0") float price,
                               @RequestParam(value="num", defaultValue="0") int num,
                               HttpServletRequest request, HttpServletResponse response) {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");

    }

}