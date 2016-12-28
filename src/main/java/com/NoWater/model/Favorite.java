package com.NoWater.model;

/**
 * Created by qingpeng on 2016/12/8.
 */
public class Favorite {
    private int favorite_id;
    private String favorite_time;
    private int user_id;
    private int favorite_type;
    private int is_del;
    private int id;

    public int getFavoriteId() {
        return favorite_id;
    }

    public void setFavoriteId(int favorite_id) {
        this.favorite_id = favorite_id;
    }

    public String getFavoriteTime() {
        return favorite_time;
    }

    public void setFavoriteTime(String favorite_time) {
        this.favorite_time = favorite_time;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public int getFavoriteType() {
        return favorite_type;
    }

    public void setFavoriteType(int favorite_type) {
        this.favorite_type = favorite_type;
    }

    public int getIsDel() {
        return is_del;
    }

    public void setIsDel(int is_del) {
        this.is_del = is_del;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
