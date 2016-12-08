package com.NoWater.model;

/**
 * Created by wukai on 2016/12/7.
 */
public class Photo {
    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void setPhoto_id(int photo_id) {
        this.photo_id = photo_id;
    }

    public void setProduct_id(int belong_id) {
        this.belong_id = belong_id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFile_name() {

        return file_name;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public int getProduct_id() {
        return belong_id;
    }

    public String getUrl() {
        return url;
    }

    private String file_name;
    private int photo_id;
    private int belong_id;
    private String url;

    public int getPhoto_type() {
        return photo_type;
    }

    public int getIs_del() {
        return is_del;
    }

    public void setPhoto_type(int photo_type) {
        this.photo_type = photo_type;
    }

    public void setIs_del(int is_del) {
        this.is_del = is_del;
    }

    private int photo_type;
    private int is_del;
}
