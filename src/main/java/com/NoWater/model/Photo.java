package com.NoWater.model;

import com.NoWater.util.DBUtil;
import net.sf.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wukai on 2016/12/7.
 */
public class Photo {
    private String file_name;
    private int photo_id;
    private int belong_id;
    private String url;
    private int photo_type;
    private int is_del;

    public void setFileName(String file_name) {
        this.file_name = file_name;
    }

    public void setPhotoId(int photo_id) {
        this.photo_id = photo_id;
    }

    public void setProductId(int belong_id) {
        this.belong_id = belong_id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFile_name() {

        return file_name;
    }

    public int getPhotoId() {
        return photo_id;
    }

    public int getProductId() {
        return belong_id;
    }

    public String getUrl() {
        return url;
    }

    public int getPhotoType() {
        return photo_type;
    }

    public int getIsDel() {
        return is_del;
    }

    public void setPhotoType(int photo_type) {
        this.photo_type = photo_type;
    }

    public void setIsDel(int is_del) {
        this.is_del = is_del;
    }

    public static ArrayList<String> getPhotoURL (String getPhotoSQL, int belong_id, int photoType) {
        DBUtil db = new DBUtil();
        List<Object> getPhotoSQLList = new ArrayList<>();
        getPhotoSQLList.add(belong_id);
        getPhotoSQLList.add(photoType);
        List<Photo> photoList;
        try {
            photoList = db.queryInfo(getPhotoSQL, getPhotoSQLList, Photo.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        ArrayList<String> StringList = new ArrayList<>();
        for (int i = 0; i < photoList.size(); i++) {
            StringList.add(photoList.get(i).getUrl());
        }
        return StringList;
    }
}
