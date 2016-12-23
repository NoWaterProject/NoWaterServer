package com.NoWater.customer;

import com.NoWater.model.Cart;
import com.NoWater.model.Product;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.LogHelper;
import com.NoWater.util.ProductShopUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wukai on 2016/11/29.
 */
@RestController
public class CustomerAddCart {
    private Jedis jedis;
    DBUtil db = new DBUtil();

    @RequestMapping("/customer/cart/adding")
    public JSONObject CustomerCartAdding(@RequestParam(value = "productId") int productId,
                                         @RequestParam(value = "addType") int addType,
                                         @RequestParam(value = "num", defaultValue = "1") int num,
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

        // addType错误
        if (addType != 0 && addType != 1) {
            jsonObject.put("status", 400);
            return jsonObject;
        }

        LogHelper.info(String.format("[customer/cart/adding] [param] [userId:%s, productId:%s, num:%s]", userId, productId, num));

        String productSQL = "select * from `products` where `product_id` = ?";
        List<Object> paramProduct = new ArrayList<>();
        paramProduct.add(productId);
        List<Product> productList;
        try {
            productList = db.queryInfo(productSQL, paramProduct, Product.class);
            if (productList.size() == 0) {
                jsonObject.put("status", 500);
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
            return jsonObject;
        }

        if (num > productList.get(0).getQuantityStock() || num < 0) {
            jsonObject.put("status", 600);
            return jsonObject;
        }

        String sql = "select * from `cart` where `user_id` = ? and `product_id` = ?";
        List<Object> params = new ArrayList<>();
        params.add(Integer.parseInt(userId));
        params.add(productId);
        List<Cart> cartList = db.queryInfo(sql, params, Cart.class);
        if (cartList.size() == 0) {
            sql = "insert into `cart` (`user_id`, `product_id`, `num`) values (?, ?, ?)";
            params.add(num);
            db.insertUpdateDeleteExute(sql, params);
            jsonObject.put("status", 200);
        } else {
            if (addType == 0) {
                // 超过库存，不允许添加
                if (num + cartList.get(0).getNum() > productList.get(0).getQuantityStock()) {
                    jsonObject.put("status", 600);
                    return jsonObject;
                }
                sql = "update `cart` set `num` = `num` + ? where `user_id` = ? and `product_id` = ?";
            } else {
                sql = "update `cart` set `num` = ? where `user_id` = ? and `product_id` = ?";
            }
            params.clear();
            params.add(num);
            params.add(Integer.parseInt(userId));
            params.add(productId);
            db.insertUpdateDeleteExute(sql, params);
            jsonObject.put("status", 200);
        }

        JSONObject userInformation = Cart.getUserCartNum(Integer.parseInt(userId));
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(0, userInformation);
        jsonObject.put("userInformation", jsonArray);

        LogHelper.info(String.format("[/customer/cart/adding] %s", jsonObject.toString()));
        return jsonObject;
    }

    @RequestMapping("/customer/cart/deleting")
    public JSONObject customerCartDeleting(@RequestParam(value = "cartId") int cartId,
                                           HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);
        if (userId == null) {
            jsonObject.put("status", 300);
            return jsonObject;
        }

        String sql = "select * from `cart` where `user_id` = ? and `cart_id` = ?";
        List<Object> params = new ArrayList<>();
        params.add(Integer.parseInt(userId));
        params.add(cartId);
        try {
            List<Cart> cartList = db.queryInfo(sql, params, Cart.class);
            if (cartList.size() == 0) {
                jsonObject.put("status", 500);
                LogHelper.info(String.format("[/customer/cart/deleting] %s", jsonObject.toString()));
                return jsonObject;
            }

            String updateSQL = "update `cart` set `is_del` = 1 where `cart_id` = ?";
            List<Object> list = new ArrayList<>();
            list.add(cartId);
            db.insertUpdateDeleteExute(updateSQL, list);
            jsonObject.put("status", 200);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
        }

        LogHelper.info(String.format("[/customer/cart/deleting] %s", jsonObject.toString()));
        return jsonObject;
    }

    @RequestMapping("/customer/cart/list")
    public JSONObject customerCartList(@RequestParam(value = "startId", defaultValue = "0") int startId,
                                       @RequestParam(value = "count") int count,
                                       HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();

        String token = CookieUtil.getCookieValueByName(request, "token");
        String userId = CookieUtil.confirmUser(token);
        if (userId == null) {
            jsonObject.put("status", 300);
            LogHelper.info(String.format("[/customer/cart/list] %s", jsonObject.toString()));
            return jsonObject;
        }

        if (startId == -1) {
            jsonObject.put("status", 400);
            LogHelper.info(String.format("[/customer/cart/list] %s", jsonObject.toString()));
            return jsonObject;
        }

        String getCartSQL;
        List<Object> params = new ArrayList<>();
        params.add(Integer.parseInt(userId));
        if (startId == 0) {
            getCartSQL = "select * from `cart` where `user_id` = ? order by `cart_id` desc";
        } else {
            getCartSQL = "select * from `cart` where `user_id` = ? and `cart_id` <= ? order by `cart_id` desc";
            params.add(startId);
        }

        try {
            List<Cart> cartList = db.queryInfo(getCartSQL, params, Cart.class);
            int actualCount;
            if (cartList.size() <= count) {
                jsonObject.put("startId", -1);
                actualCount = cartList.size();
            } else {
                jsonObject.put("startId", cartList.get(count).getCartId());
                actualCount = count;
            }
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < actualCount; i++) {
                JSONObject jsonObject1 = JSONObject.fromObject(cartList.get(i));

                int productId = cartList.get(i).getProductId();

                JSONObject productDetail = ProductShopUtil.GetProductDetail(productId);
                jsonObject1.put("product", productDetail);
                jsonArray.add(jsonObject1);
            }
            jsonObject.put("data", jsonArray);
            jsonObject.put("status", 200);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
        }
        return jsonObject;
    }
}
