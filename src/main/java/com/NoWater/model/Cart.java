package com.NoWater.model;

import com.NoWater.util.DBUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李鹏飞 on 2016/11/28 0028.
 */
public class Cart {

    private int cart_id;
    private int user_id;
    private int product_id;
    private int num;
    private long cartNum;

    public int getProductId() {
        return product_id;
    }

    public void setProductId(int product_id) {
        this.product_id = product_id;
    }

    public long getCartNum() {
        return cartNum;
    }

    public void setCartNum(long cartNum) {
        this.cartNum = cartNum;
    }

    public void setCartId(int cart_id) {
        this.cart_id = cart_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getCartId() {
        return cart_id;
    }

    public int getUserId() {
        return user_id;
    }

    public int getNum() {
        return num;
    }

    public static JSONObject getUserCartNum(int userId) {
        List<Object> list1 = new ArrayList<>();
        DBUtil db1 = new DBUtil();
        String getUserCartSQL = "select * from `cart` where user_id = ?";
        list1.add(userId);
        JSONObject jsonObject = new JSONObject();
        try {
            List<Cart> cartList1 = db1.queryInfo(getUserCartSQL, list1, Cart.class);
            String name = User.getUserName(userId);

            JSONObject userInformation = new JSONObject();
            userInformation.put("name", name);
            userInformation.put("cartNum", cartList1.size());
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status", 1100);
        }
        return jsonObject;
    }
}
