package com.NoWater.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Koprvhdix on 2016/12/20.
 */
public class timeUtil {
    public static String timeCountdown(String time, int count) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = sdf.parse(time);
            Calendar orderdate = Calendar.getInstance();
            orderdate.setTime(date);
            Calendar nowdate = Calendar.getInstance();
            long time1 = orderdate.getTimeInMillis() + count * 24 * 3600 * 1000;
            long time2 = nowdate.getTimeInMillis();

            long day = (time1 - time2) / (1000 * 24 * 3600);
            long hour = ((time1 - time2) - (day * 24 * 3600 * 1000)) / (1000 * 3600);
            if (count == 1) {
                return "Leave " + hour + " Hours";
            } else {
                return "Leave " + day + " Days " + hour + " Hours";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";

    }
}
