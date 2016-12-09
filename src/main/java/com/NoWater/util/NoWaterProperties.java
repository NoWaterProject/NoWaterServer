package com.NoWater.util;

import redis.clients.jedis.Jedis;

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
    private static String[] address = {"Kowloon(KLN)", "NT_Island(NT)", "HongkongIsland(HK)"};
    private static String[][] address2 = {
            {"Cheung Sha Wan", "Choi Wan", "Diamond Hill", "Ho Man Tin", "Hung Hom", "Jordan", "Jordan Road", "Kowloon Bay", "Kowloon City", "Kowloon Tong", "Kwun Tong", "La Salle Road", "Lai Chi Kok", "Lam Tin", "Lok Fu", "Ma Tau Wai", "Mei Foo", "Mong Kok", "Ngau Chi Wan", "Ngau Tau Kok", "Rainbow Village", "San Po Kong", "Sau Mau Ping", "Sham Shui Po", "Shek Kip Mei", "Tai Kok Tsui", "To Kwa Wan", "Tsim Sha Tsui", "Tsz Wan Shan", "Wong Tai Sin", "Yau Ma Tei", "Yau Tong"},
            {"Chek Lap Kok", "Chinese University", "Clear Water Bay", "Discovery Bay", "Fairview Park", "Fanling", "Fo Tan", "HKUST", "Kwai Chung", "Long Ping", "Ma On Shan", "Ma Wan", "Pat Heung", "Sai Kung (North)", "Sai Kung (South)", "Science Park", "Sha Tin", "Sheung Shui", "Siu Lek Yuen", "Tai Po", "Tai Wai", "Tin Shui Wai", "Tseung Kwan O", "Tsing Yi", "Tsuen Wan", "Tuen Mun", "Tung Chung", "Wu Kai Sha", "Yuen Long"},
            {"Aberdeen", "Admiralty", "Ap Lei Chau", "Big Wave Bay", "Causeway Bay", "Central", "Central Sheung Wan", "Central South", "Chai Wan", "Gloucester Road", "Happy Valley", "Harbour Road", "Jardine's Lookout", "Kennedy Town", "Lai Tak Tsuen", "Mid-Levels", "Mid-Levels West", "North Point", "Pok Fu Lam", "Quarry Bay", "Sai Wan", "Shau Kei Wan", "Shek O", "Sheung Wan West", "Siu Sai Wan", "So Kon Po", "Southern District", "Stanley", "Tai Hang Road", "The Peak", "Tim Mei Ave", "Tin Hau", "Wah Fu", "Wan Chai", "Wong Chuk Hang"}
    };
    private static String cos_appId;
    private static String cos_secretId;
    private static String cos_bucketName;
    private static String cos_secretKey;

    public static String getCos_appId() {
        return cos_appId;
    }

    public static String getCos_secretId() {
        return cos_secretId;
    }

    public static String getCos_secretKey() {
        return cos_secretKey;
    }

    public static String getCos_bucketName() {
        return cos_bucketName;
    }

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

        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.set("ProductAd", "[{\"productId\":1, \"productName\":\"GARMIN VIVOSMART HR+ (CHINESE)\", \"size\":\"1 PC\", \"price\":1799.00, \"photoIdUrl\":\"http://koprvhdix117-10038234.cos.myqcloud.com/Product1.jpg\"}, {\"productId\":2, \"productName\":\"M&M'S BUCKET RED & GREEN\", \"size\":\"710G\", \"price\":145.90, \"photoIdUrl\":\"http://koprvhdix117-10038234.cos.myqcloud.com/Product2.jpg\"},{\"productId\":3, \"productName\":\"GARMIN FORERUNNER 235 (CHINESE)\", \"size\":\"1 PC\", \"price\":2799.00, \"photoIdUrl\":\"http://koprvhdix117-10038234.cos.myqcloud.com/Product3.jpg\"},{\"productId\":4, \"productName\":\"CHILCAS CABERNET SAUVIGNON\", \"size\":\"100CL\", \"price\":89.00, \"photoIdUrl\":\"http://koprvhdix117-10038234.cos.myqcloud.com/Product4.jpg\"}, {\"productId\":5, \"productName\":\"LAURIER SG GATHER 35CM TWIN PACK\", \"size\":\"8SX2\", \"price\":35.90, \"photoIdUrl\":\"http://koprvhdix117-10038234.cos.myqcloud.com/Product5.jpg\"},{\"productId\":6, \"productName\":\"KOTEX WHITE SLIM XLONG TWIN\", \"size\":\"PACK\", \"price\":34.90, \"photoIdUrl\":\"http://koprvhdix117-10038234.cos.myqcloud.com/Product6.jpg\"},{\"productId\":7, \"productName\":\"RITZ CHEESE SANDWICH TRAY PACK\", \"size\":\"324G\", \"price\":23.90, \"photoIdUrl\":\"http://koprvhdix117-10038234.cos.myqcloud.com/Product7.jpg\"},{\"productId\":8, \"productName\":\"WHISKAS TUNA CHUNKS WITH PRAWNS\", \"size\":\"85G\", \"price\":6.90, \"photoIdUrl\":\"http://koprvhdix117-10038234.cos.myqcloud.com/Product8.jpg\"},{\"productId\":9, \"productName\":\"INNOTEC Miami Swing Ceramic Heater\", \"size\":\"UNIT\", \"price\":318.00, \"photoIdUrl\":\"http://koprvhdix117-10038234.cos.myqcloud.com/Product9.jpg\"},{\"productId\":10, \"productName\":\"SARA LEE CHOCOLATE SWIRL POUND CAKE\", \"size\":\"300G\", \"price\":33.90, \"photoIdUrl\":\"http://koprvhdix117-10038234.cos.myqcloud.com/Product10.jpg\"}]");
        jedis.set("ShopAd", "[{\"shopId\":1, \"adPhotoUrl\":\"http://koprvhdix117-10038234.cos.myqcloud.com/HomePageDefault1.jpg\"}, {\"shopId\":2, \"adPhotoUrl\":\"http://koprvhdix117-10038234.cos.myqcloud.com/HomePageDefault2.jpg\"}, {\"shopId\":3, \"adPhotoUrl\":\"http://koprvhdix117-10038234.cos.myqcloud.com/HomePageDefault3.jpg\"}, {\"shopId\":4, \"adPhotoUrl\":\"http://koprvhdix117-10038234.cos.myqcloud.com/HomePageDefault4.jpg\"}, {\"shopId\":5, \"adPhotoUrl\":\"http://koprvhdix117-10038234.cos.myqcloud.com/HomePageDefault5.jpg\"}]");
        LogHelper.info("ProductAd set success");

        try {
            InputStream in = new FileInputStream(propertiesFile);
            prop.load(in);
            db_port = prop.getProperty("port").trim();
            db_user = prop.getProperty("database.user").trim();
            db_password = prop.getProperty("database.password").trim();
            db_url = prop.getProperty("database.url").trim();
            cos_appId = prop.getProperty("cos.appId").trim();
            cos_secretId = prop.getProperty("cos.secretId").trim();
            cos_secretKey = prop.getProperty("cos.secretKey").trim();
            cos_bucketName = prop.getProperty("cos.bucketName").trim();
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
