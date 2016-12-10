package com.NoWater.shopOwner;

import java.util.ArrayList;
import java.util.List;

import com.NoWater.model.Photo;
import com.NoWater.model.Shop;
import com.NoWater.util.*;
import net.sf.json.JSONArray;
import org.apache.commons.collections.ArrayStack;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.io.File;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wukai on 2016/11/21.
 */
@RestController
public class ShopOwnerApply {
    @RequestMapping("/shop-owner/status")
    public JSONObject status(HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String token = CookieUtil.getCookieValueByName(request, "token");
        JSONObject jsonObject = new JSONObject();

        String user_id = CookieUtil.confirmUser(token);
        if (user_id == null) {
            jsonObject.put("status", 300);
            return jsonObject;
        }

        List<Object> list = new ArrayList<>();
        DBUtil db = new DBUtil();
        String sql = "select Status from shop where owner_id = ?";
        list.add(user_id);
        List<Shop> shopList = db.queryInfo(sql, list, Shop.class);

        if (shopList.size() != 0) {
            int status = shopList.get(0).getStatus();
            if (status == 0) {
                jsonObject.put("status", 500);  //正在审查
            } else if (status == 1) {
                jsonObject.put("status", 200);  //已成为卖家
            } else if (status == -1) {
                jsonObject.put("status", 600);  //已拒绝
            } else if (status == 2) {
                Jedis jedis = new Jedis("127.0.0.1", 6379);

                long ttl = jedis.ttl(user_id + "email");
                if (ttl == -2) {
                    jsonObject.put("status", 600);              //超时

                    List<Object> listUpdate = new ArrayList<>();
                    listUpdate.add(user_id);
                    String sqlUpdate = "update `shop` set `status`=-1 where `owner_id` = ?";
                    db.insertUpdateDeleteExute(sqlUpdate, listUpdate);

                } else if (ttl == -1) {
                    jedis.expire(user_id + "email", 86400);     //没有设置上超时
                    jsonObject.put("status", 700);              //处于邮箱验证阶段，请用户验证邮箱
                } else {
                    jsonObject.put("status", 700);              //处于邮箱验证阶段，请用户验证邮箱
                }
            }
            String sqlGetInfo = "select * from shop where owner_id = ?";
            List<Object> ownerList = new ArrayList<>();
            ownerList.add(user_id);
            List<Shop> shopInfo = db.queryInfo(sqlGetInfo, ownerList, Shop.class);
            jsonObject.put("data", JSONArray.fromObject(shopInfo));

            String getPhotoSQL = "select * from photo where belong_id = ? and photo_type = 4";
            List<Object> getPhotoList = new ArrayList<>();
            getPhotoList.add(user_id);
            List<Photo> photoList = db.queryInfo(getPhotoSQL, getPhotoList, Photo.class);
            jsonObject.put("photo", JSONArray.fromObject(photoList));
        } else {
            jsonObject.put("status", 400);      //数据库中找不到卖家，用户未申请
        }
        return jsonObject;
    }

    @RequestMapping("/shop-owner/apply")
    public JSONObject apply(@RequestParam(value = "email", defaultValue = "/") String email,
                            @RequestParam(value = "shopName", defaultValue = "/") String shopName,
                            @RequestParam(value = "telephone", defaultValue = "/") String telephone,
                            @RequestParam(value = "fileNameList", required = false) String fileNameList,
                            HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Access-Control-Allow-Origin", "http://123.206.100.98");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String token = CookieUtil.getCookieValueByName(request, "token");
        JSONObject jsonObject = new JSONObject();

        String user_id = CookieUtil.confirmUser(token);
        if (user_id == null) {
            jsonObject.put("status", 300);
            return jsonObject;
        }

        Boolean hasApplied = false;

        List<Object> list = new ArrayList<>();
        DBUtil db = new DBUtil();
        String sql = "select status from shop where owner_id = ?";
        list.add(user_id);
        List<Shop> shopList = db.queryInfo(sql, list, Shop.class);

        if (shopList.size() != 0) {
            int status = shopList.get(0).getStatus();
            if (status == 0) {
                jsonObject.put("status", 600);  //正在审查
                return jsonObject;
            } else if (status == 1) {
                jsonObject.put("status", 700);  //已成为卖家
                return jsonObject;
            } else if (status == -1) {
                hasApplied = true;
            } else if (status == 2) {
                jsonObject.put("status", 610);  //邮箱验证中
                return jsonObject;
            }
        }

        sql = "select * from shop where shop_name = ?";
        List<Object> listShopName = new ArrayList<>();
        listShopName.add(shopName);
        shopList = db.queryInfo(sql, listShopName, Shop.class);

        if (shopList.size() != 0 && !hasApplied) {
            jsonObject.put("status", 500);  //店名已有人使用
        } else if (!email.contains("@")) {
            jsonObject.put("status", 800);  // email error
        } else if ((telephone.charAt(0) != '6' && telephone.charAt(0) != '9') || telephone.length() != 8) {
            jsonObject.put("status", 900);  // telephone error
        } else {
            int shop_status = 2;            //插入申请，正在成为卖家

            JSONArray jsonArray = JSONArray.fromObject(fileNameList);
            int jsonArraySize = jsonArray.size();
            ArrayList<String> unionFileName = new ArrayList<>();
            ArrayList<String> addFileNameList = new ArrayList<>();
            for (int i = 0; i < jsonArraySize; i++) {
                String fileName = jsonArray.getString(i);
                addFileNameList.add(fileName);
                unionFileName.add(fileName);
            }
            LogHelper.info("add:" + addFileNameList.toString());

            String fileNameSQL = "select file_name from photo where belong_id = ? and is_del = 0 and photo_type = 4";
            List<Object> getFileNameList = new ArrayList<>();
            getFileNameList.add(String.valueOf(user_id));
            List<Photo> dbFileList = db.queryInfo(fileNameSQL, getFileNameList, Photo.class);
            ArrayList<String> deleteFileNameList = new ArrayList<>();
            for (int i = 0; i < dbFileList.size(); i++) {
                deleteFileNameList.add(dbFileList.get(i).getFile_name());
            }

            // 求差集，获得需要添加的和需要删除的fileName
            addFileNameList.removeAll(deleteFileNameList);
            deleteFileNameList.removeAll(unionFileName);

            for (int i = 0; i < addFileNameList.size(); i++) {
                String fileName = addFileNameList.get(i);
                String path = "/tmp/" + user_id + "/" + fileName;
                File dir = new File(path);
                if (!dir.exists()) {
                    jsonObject.put("status", 1000);     //图片不存在，error
                    return jsonObject;
                }
            }

            // 添加到对象存储器
            for (int i = 0; i < addFileNameList.size(); i++) {
                String fileName = addFileNameList.get(i);
                String path = "/tmp/" + user_id + "/" + fileName;
                LogHelper.info("addFile:" + path);
                String cmd = "/usr/bin/python /root/src/base/COS_API.py upload " + "/" + fileName + " " + path + " > /root/py_cos.log";
                LogHelper.info("add file cmd:" + cmd);
                Process proc = Runtime.getRuntime().exec(cmd);
                proc.waitFor();

                List<Object> insertPhoto = new ArrayList<>();
                insertPhoto.add(fileName);
                insertPhoto.add(user_id);
                insertPhoto.add("http://koprvhdix117-10038234.file.myqcloud.com/" + fileName);
                insertPhoto.add(4);
                String insertPhotoSQL = "insert into `photo` (file_name, belong_id, url, photo_type) values (?, ?, ?, ?)";
                db.insertUpdateDeleteExute(insertPhotoSQL, insertPhoto);
            }
            // 从对象存储器中删除
            for (int i = 0; i < deleteFileNameList.size(); i++) {
                String fileName = addFileNameList.get(i);
                String path = "/" + deleteFileNameList.get(i);
                String cmd = "/usr/bin/python /root/src/base/COS_API.py delete " + path + " > /root/py_cos.log";
                LogHelper.info("delete cmd:" + cmd);
                Process proc = Runtime.getRuntime().exec(cmd);
                proc.waitFor();

                String deleteSQL = "update `photo` set `is_del` = 1 where `file_name` = ?";
                List<Object> deleteFile = new ArrayList<>();
                deleteFile.add(fileName);
                db.insertUpdateDeleteExute(deleteSQL, deleteFile);
            }

            // 邮箱验证
            Jedis jedis = new Jedis("127.0.0.1", 6379);
            String confirmCode = Uuid.getUuid();
            jedis.set(user_id + "email", confirmCode);
            jedis.expire(user_id + "email", 86400);
            SendEmail.send(email, "请在24小时内点击此链接验证您的邮箱：http://123.206.100.98:16120/shop-owner/email/confirming?confirmCode="
                    + confirmCode + "&userId=" + user_id);

            List<Object> list1 = new ArrayList<>();
            DBUtil db1 = new DBUtil();

            list1.add(shopName);
            list1.add(email);
            list1.add(shop_status);
            list1.add(telephone);
            list1.add(user_id);

            if (hasApplied) {
                sql = "update `shop` set `shop_name` = ?, `email` = ?, `status` = ?, `telephone` = ? where `owner_id` = ?";
            } else {
                sql = "insert into shop (shop_name, email, status, telephone, owner_id) values (?,?,?,?,?)";
            }

            db1.insertUpdateDeleteExute(sql, list1);

            jsonObject.put("status", 200);
        }
        return jsonObject;
    }

    @RequestMapping("/shop-owner/email/confirming")
    public JSONObject emailConfirming(@RequestParam(value = "confirmCode") String confirmCode,
                                      @RequestParam(value = "userId") String userId,
                                      HttpServletRequest request, HttpServletResponse response) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        JSONObject jsonObject = new JSONObject();
        long ttl = jedis.ttl(userId + "email");
        if (ttl == -2) {
            jsonObject.put("status", 300);
        } else {
            String code = jedis.get(userId + "email");
            if (code.equals(confirmCode)) {
                jsonObject.put("status", 200);

                DBUtil db = new DBUtil();
                List<Object> listUpdate = new ArrayList<>();
                listUpdate.add(userId);
                String sqlUpdate = "update `shop` set `status`=0 where `owner_id` = ?";
                db.insertUpdateDeleteExute(sqlUpdate, listUpdate);

                jedis.del(userId + "email");
            } else {
                jsonObject.put("status", 300);
            }
        }
        return jsonObject;
    }
}
