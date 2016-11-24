package com.NoWater.util;
import java.io.*;
import java.util.Properties;

/**
 * Created by 李鹏飞 on 2016/11/20 0020.
 */

public final class NoWaterProperties {
    private static String db_port;
    private static String db_password;
    private static String db_user;
    private static String db_url;
    static {
        Properties prop = new Properties();
        File propertiesFile;
        String userName = System.getProperty("user.name");
        if (userName.equals("root"))
            propertiesFile = new File("conf/Online.properties");
        else
            propertiesFile = new File("conf/Offline.properties");

        try {
            InputStream in = new FileInputStream(propertiesFile);
            prop.load(in);
            db_port = prop.getProperty("port").trim();
            db_user = prop.getProperty("database.user").trim();
            db_password = prop.getProperty("database.password").trim();
            db_url = prop.getProperty("database.url").trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 私有构造方法，不需要创建对象
     */
    private NoWaterProperties() {
    }

    public static String getDb_port() {
        return db_port;
    }

    public static String getDb_password() {
        return db_password;
    }

    public static String getDb_user() {
        return db_user;
    }

    public static String getDb_url() {
        return db_url;
    }
}
