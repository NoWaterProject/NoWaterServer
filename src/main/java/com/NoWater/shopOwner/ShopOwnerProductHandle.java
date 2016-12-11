package com.NoWater.shopOwner;

import com.NoWater.model.Shop;
import com.NoWater.util.CookieUtil;
import com.NoWater.util.DBUtil;
import com.NoWater.util.FileUpload;
import com.NoWater.util.LogHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李鹏飞 on 2016/12/10 0010.
 */
@RestController
public class ShopOwnerProductHandle {
    @RequestMapping("shop-owner/products/edit")
    public JSONObject searchKey(
            @RequestParam(value = "product_id", defaultValue = "0") int product_id,
            @RequestParam(value = "class_id", defaultValue = "0") int class_id,
            @RequestParam(value = "product_name", defaultValue = "") String product_name,
            @RequestParam(value = "price", defaultValue = "0") double price,
            @RequestParam(value = "quantity_stock", defaultValue = "0") int quantity_stock,
            @RequestParam(value = "is_del", defaultValue = "0") int is_del,
            @RequestParam(value = "detail_photo_list") String detail_photo_list,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String token = CookieUtil.getCookieValueByName(request, "token");
        JSONObject jsonObject = new JSONObject();
        String userId = CookieUtil.confirmUser(token);
        if (userId == null) {
            jsonObject.put("status", 300);                  //用户未登录
            return jsonObject;
        }

        List<Object> Param = new ArrayList<>();
        StringBuffer Sql = new StringBuffer();
        Sql.append("select shop_id from shop where owner_id = ?");
        Param.add(userId);
        DBUtil Db = new DBUtil();
        List<Shop> list = Db.queryInfo(Sql.toString(), Param, Shop.class);

        if (list.size() == 0) {
            jsonObject.put("status", 400);                  //用户不是商家
            return jsonObject;
        }
        int shop_id = list.get(0).getShopId();

        List<Object> param = new ArrayList<>();
        param.add(shop_id);
        param.add(class_id);
        param.add(product_name);
        param.add(price);
        param.add(quantity_stock);

        ArrayList<String> addFileNameList = FileUpload.jsonToArrayList(detail_photo_list);
        LogHelper.info("add:" + addFileNameList.toString());

        if (product_id == 0) {

            int status = FileUpload.UploadToCOS(addFileNameList, String.valueOf(shop_id), 2);
            if (status == -1) {
                jsonObject.put("status", 1010);
                return jsonObject;
            }

            StringBuffer sql = new StringBuffer();
            DBUtil db = new DBUtil();
            sql.append("insert into products(shop_id,class_id,product_name,price,quantity_stock) values(?,?,?,?,?,?)");
            db.insertUpdateDeleteExute(sql.toString(), param);
            jsonObject.put("status", 200);                  //添加商品成功
        } else {
            ArrayList<String> unionFileList = FileUpload.jsonToArrayList(detail_photo_list);

            StringBuffer sql = new StringBuffer();
            DBUtil db = new DBUtil();
            sql.append("UPDATE products SET shop_id = ?,class_id = ?,product_name = ?,price = ?,quantity_stock = ?,is_del = ? WHERE product_id = ?");
            param.add(is_del);
            param.add(product_id);
            db.insertUpdateDeleteExute(sql.toString(), param);
            jsonObject.put("status", 200);                  //修改商品成功
        }

        return jsonObject;
    }
}
