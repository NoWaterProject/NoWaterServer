package com.NoWater;
import java.io.*;
import java.util.Properties;

/**
 * Created by 李鹏飞 on 2016/11/20 0020.
 */

public final class NoWaterProperties {
    private static String port;
    private static String db_user;

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
            port = prop.getProperty("port").trim();
            db_user = prop.getProperty("database.user").trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 私有构造方法，不需要创建对象
     */
    private NoWaterProperties() {
    }

    public static String getPort() {
            return port;
        }

    public static String getDbUser() {
            return db_user;
        }

}
