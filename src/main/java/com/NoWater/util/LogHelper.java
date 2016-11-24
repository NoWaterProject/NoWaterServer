package com.NoWater.util;

import org.apache.log4j.Logger;

/**
 * Created by wukai on 2016/11/22.
 */
public final class LogHelper {
    static Logger logger = Logger.getLogger(LogHelper.class);

    private LogHelper() {
    }

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void debug(String msg) {
        logger.debug(msg);
    }

    public static void error(String msg) {
        logger.error(msg);
    }
}
