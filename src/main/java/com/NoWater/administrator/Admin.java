package com.NoWater.administrator;

/**
 * Created by ByYoung on 2016/11/25.
 */
public class Admin {
    private String name;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Admin(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
