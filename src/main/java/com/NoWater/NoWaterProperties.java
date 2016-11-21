package com.NoWater;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
/**
 * Created by 李鹏飞 on 2016/11/20 0020.
 */

public final class NoWaterProperties {
    private static String port;
    private static String db_user;

        static {
            Properties prop = new Properties();
            InputStream in = NoWaterProperties.class.getResourceAsStream("conf/NoWater.properties");

            try {
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

/*          public static void main(String args[]){
              System.out.println(getParam1());
              System.out.println(getParam2());
          }*/

}
