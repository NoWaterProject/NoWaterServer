package com.NoWater.util;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBUtil {

    public static String driver = "com.mysql.jdbc.Driver";

    public static String url = NoWaterProperties.getDb_url();


    public static String name = NoWaterProperties.getDb_user();


    public static String pwd = NoWaterProperties.getDb_password();


    private Connection conn;

    private PreparedStatement pts;

    private ResultSet rs;

    public PreparedStatement getPts() {
        return pts;
    }

    public void setPts(PreparedStatement pts) {
        this.pts = pts;
    }

    public ResultSet getRs() {
        return rs;
    }

    public void setRs(ResultSet rs) {
        this.rs = rs;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public DBUtil() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void connDB() {
        try {
            conn = DriverManager.getConnection(url, name, pwd);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean insertUpdateDeleteExute(String sql, List<Object> param) {
        this.connDB();
        try {

            pts = conn.prepareStatement(sql);
            pts.clearParameters();

            if (null != param && param.size() != 0) {
                int i = 1;
                for (Object object : param) {
                    pts.setObject(i++, object);
                }
            }

            int ex = pts.executeUpdate();

            return ex > 0 ? true : false;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                pts.close();
                conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return false;
    }

    public <T> List<T> queryInfo(String sql, List<Object> param, Class<T> zlass) throws InstantiationException,
            IllegalAccessException,
            NoSuchFieldException,
            SecurityException {

        try {

            List<T> list = new ArrayList<T>();

            this.connDB();

            pts = conn.prepareStatement(sql);

            pts.clearParameters();

            if (null != param && param.size() != 0) {
                int i = 1;
                for (Object object : param) {
                    pts.setObject(i++, object);
                }
            }
            System.out.println(pts);
            rs = pts.executeQuery();

            ResultSetMetaData rsd = rs.getMetaData();

            int count = rsd.getColumnCount();
            while (rs.next()) {
                T t = zlass.newInstance();
                for (int i = 0; i < count; i++) {
                    String colName = rsd.getColumnName(i + 1);
                    Object colValue = rs.getObject(colName);
                    if (null == colValue) {
                        colValue = "";
                    }

                    Field field = zlass.getDeclaredField(colName);
                    field.setAccessible(true);
                    field.set(t, colValue);

                }

                list.add(t);
            }

            return list;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                pts.close();
                conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

}
