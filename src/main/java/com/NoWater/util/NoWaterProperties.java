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

    private static String[] address = {"kowloon", "nt_island", "hongkongisland"};
    private static String[][] address2 = {
            {"Cheung Sha Wan", "Choi Wan", "Diamond Hill", "Ho Man Tin", "Hung Hom", "Jordan", "Jordan Road", "Kowloon Bay", "Kowloon City", "Kowloon Tong", "Kwun Tong", "La Salle Road", "Lai Chi Kok", "Lam Tin", "Lok Fu", "Ma Tau Wai", "Mei Foo", "Mong Kok", "Ngau Chi Wan", "Ngau Tau Kok", "Rainbow Village", "San Po Kong", "Sau Mau Ping", "Sham Shui Po", "Shek Kip Mei", "Tai Kok Tsui", "To Kwa Wan", "Tsim Sha Tsui", "Tsz Wan Shan", "Wong Tai Sin", "Yau Ma Tei", "Yau Tong"},
            {"Chek Lap Kok", "Chinese University", "Clear Water Bay", "Discovery Bay", "Fairview Park", "Fanling", "Fo Tan", "HKUST", "Kwai Chung", "Long Ping", "Ma On Shan", "Ma Wan", "Pat Heung", "Sai Kung (North)", "Sai Kung (South)", "Science Park", "Sha Tin", "Sheung Shui", "Siu Lek Yuen", "Tai Po", "Tai Wai", "Tin Shui Wai", "Tseung Kwan O", "Tsing Yi", "Tsuen Wan", "Tuen Mun", "Tung Chung", "Wu Kai Sha", "Yuen Long"},
            {"Aberdeen", "Admiralty", "Ap Lei Chau", "Big Wave Bay", "Causeway Bay", "Central", "Central Sheung Wan", "Central South", "Chai Wan", "Gloucester Road", "Happy Valley", "Harbour Road", "Jardine's Lookout", "Kennedy Town", "Lai Tak Tsuen", "Mid-Levels", "Mid-Levels West", "North Point", "Pok Fu Lam", "Quarry Bay", "Sai Wan", "Shau Kei Wan", "Shek O", "Sheung Wan West", "Siu Sai Wan", "So Kon Po", "Southern District", "Stanley", "Tai Hang Road", "The Peak", "Tim Mei Ave", "Tin Hau", "Wah Fu", "Wan Chai", "Wong Chuk Hang"}
    };

    public static String[] getAddress() {
        return address;
    }

    public static String[][] getAddress2() {
        return address2;
    }

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
