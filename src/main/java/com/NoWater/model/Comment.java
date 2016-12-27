package com.NoWater.model;

/**
 * Created by ByYoung on 2016/12/25.
 */
public class Comment {
    private int comment_id;
    private String comment_content;
    private int user_id;
    private String user_name;
    private int order_id;
    private int product_id;
    private int star;
    private String time;

    public int getStar() {
        return star;
    }

    public String getTime() {
        return time;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCommentId() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public String getCommentContent() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getOrderId() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getProductId() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }
}
