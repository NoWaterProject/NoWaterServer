package com.NoWater.shopOwner;

import com.NoWater.model.Photo;
import com.NoWater.model.Product;
import com.NoWater.model.Shop;
import com.NoWater.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.ArrayStack;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 李鹏飞 on 2016/12/10 0010.
 */
@RestController
public class ShopOwnerProductHandle {
    @RequestMapping("/shop-owner/products/edit")
    public JSONObject ShopOwnerEdit(
            @RequestParam(value = "productId", defaultValue = "0") int product_id,
            @RequestParam(value = "classId", defaultValue = "0") int class_id,
            @RequestParam(value = "productName", defaultValue = "") String product_name,
            @RequestParam(value = "price", defaultValue = "0") double price,
            @RequestParam(value = "quantityStock", defaultValue = "0") int quantity_stock,
            @RequestParam(value = "detailPhotoList") String detail_photo_list,
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
        DBUtil db = new DBUtil();
        List<Shop> list = db.queryInfo(Sql.toString(), Param, Shop.class);

        if (list.size() == 0) {
            jsonObject.put("status", 400);                  //用户不是商家
            return jsonObject;
        }
        int shop_id = list.get(0).getShopId();

        String pat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pat);
        String currentTime = sdf.format(new Date());

        List<Object> param = new ArrayList<>();
        param.add(shop_id);
        param.add(class_id);
        param.add(product_name);
        param.add(price);
        param.add(quantity_stock);

        ArrayList<String> addFileNameList = FileUpload.jsonToArrayList(detail_photo_list);
        LogHelper.info("add:" + addFileNameList.toString());

        if (product_id == 0) {
            String confirmProductNameSQL = "select product_id from products where `product_name` = ? and `is_del` = 0";
            List<Object> getProductName = new ArrayList<>();
            getProductName.add(product_name);
            List<Product> confirmProductName = db.queryInfo(confirmProductNameSQL, getProductName, Product.class);
            if (confirmProductName.size() != 0) {
                jsonObject.put("status", 500);          //  重复产品名
                return jsonObject;
            }

            StringBuffer sql = new StringBuffer();
            sql.append("insert into products(shop_id, class_id, product_name, price, quantity_stock, update_time) values(?,?,?,?,?)");
            param.add(currentTime);
            db.insertUpdateDeleteExute(sql.toString(), param);

            int status = FileUpload.fileExists(userId, addFileNameList);
            if (status == -4) {
                jsonObject.put("status", 1040);
                return jsonObject;
            }

            String getProduct = "select * from products where product_name = ? and shop_id = ?";
            getProductName.add(shop_id);
            List<Product> productDetail = db.queryInfo(getProduct, getProductName, Product.class);
            int productId = productDetail.get(0).getProductId();

            status = FileUpload.UploadToCOS(addFileNameList, userId, String.valueOf(productId), 2);
            if (status == -1) {
                jsonObject.put("status", 1010);
                return jsonObject;
            }
            jsonObject.put("status", 200);                  //添加商品成功
            jsonObject.put("data", JSONObject.fromObject(productDetail.get(0)));
        } else {
            ArrayList<String> unionFileList = FileUpload.jsonToArrayList(detail_photo_list);
            ArrayList<String> deleteFileList = FileUpload.dbFileNameList(String.valueOf(product_id), 2);
            addFileNameList.removeAll(deleteFileList);
            deleteFileList.removeAll(unionFileList);

            int status = FileUpload.fileExists(userId, addFileNameList);
            if (status == -4) {
                jsonObject.put("status", 1040);
                return jsonObject;
            }

            status = FileUpload.UploadToCOS(addFileNameList, userId, String.valueOf(product_id), 2);
            if (status == -1) {
                jsonObject.put("status", 1010);
                return jsonObject;
            }

            status = FileUpload.DeleteFileCOS(deleteFileList);
            if (status == -2) {
                jsonObject.put("status", 1020);
                return jsonObject;
            }

            boolean hasProduct = Product.confirmProductShop(shop_id, product_id);
            if (!hasProduct) {
                jsonObject.put("status", 2400);
                return jsonObject;
            }

            Jedis jedis = new Jedis("127.0.0.1", 6379);
            String productPrepareAd = jedis.get("prepareProductAd");
            if (productPrepareAd != null) {
                JSONArray jsonArray = JSONArray.fromObject(productPrepareAd);
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (Integer.parseInt(jsonArray.getJSONObject(i).get("productId").toString()) == product_id) {
                        String getPhotoSQL = "select * from photo where belong_id = ? and photo_type = ? and is_del = 0";
                        ArrayList<String> stringArrayList = Photo.getPhotoURL(getPhotoSQL, product_id, 2);
                        jsonArray.getJSONObject(i).put("photoIdUrl", stringArrayList.get(0));
                        break;
                    }
                }
                jedis.set("prepareProductAd", jsonArray.toString());
            }
            String productAd = jedis.get("ProductAd");
            JSONArray jsonArray = JSONArray.fromObject(productAd);
            for (int i = 0; i < jsonArray.size(); i++) {
                if (Integer.parseInt(jsonArray.getJSONObject(i).get("productId").toString()) == product_id) {
                    String getPhotoSQL = "select * from photo where belong_id = ? and photo_type = ? and is_del = 0";
                    ArrayList<String> stringArrayList = Photo.getPhotoURL(getPhotoSQL, product_id, 2);
                    jsonArray.getJSONObject(i).put("photoIdUrl",stringArrayList.get(0));
                }
            }
            jedis.set("ProductAd", jsonArray.toString());

            StringBuffer sql = new StringBuffer();
            sql.append("UPDATE products SET shop_id = ?, class_id = ?, product_name = ?, price = ?, quantity_stock = ? WHERE product_id = ?");
            param.add(product_id);
            db.insertUpdateDeleteExute(sql.toString(), param);
            jsonObject.put("status", 200);                  //修改商品成功
        }
        LogHelper.info(String.format("[/shop-owner/products/edit] %s", jsonObject.toString()));
        return jsonObject;
    }

    @RequestMapping("/shop-owner/products/delete")
    public JSONObject shopOwnerProductsDelete(@RequestParam(value = "productId", defaultValue = "0") int productId,
                                              HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String token = CookieUtil.getCookieValueByName(request, "token");
        JSONObject jsonObject = new JSONObject();
        String userId = CookieUtil.confirmUser(token);
        if (userId == null) {
            jsonObject.put("status", 300);                  //用户未登录
            return jsonObject;
        }

        int shopId = CookieUtil.confirmShop(userId);
        if (shopId == -1) {
            jsonObject.put("status", 500);          // user not shop owner
            return jsonObject;
        }

        if (!Product.confirmProductShop(shopId, productId)) {
            jsonObject.put("status", 600);          // not the user product
            return jsonObject;
        }

        String deleteSQL = "update `products` set `is_del` = 1 where `product_id` = ?";
        List<Object> objectList = new ArrayList<>();
        objectList.add(productId);
        DBUtil db = new DBUtil();
        db.insertUpdateDeleteExute(deleteSQL, objectList);
        jsonObject.put("status", 200);
        return jsonObject;
    }
}
