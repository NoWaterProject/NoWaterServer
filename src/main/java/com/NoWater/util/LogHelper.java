package util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Created by wukai on 16-11-19.
 */
public class LogHelper {
    private static Logger logger;

    static {
        PropertyConfigurator.configure("conf/log4j.properties");
        logger = Logger.getLogger(LogHelper.class);
    }

    public static void info(String log) {
        logger.info(log);
    }

    public static void error(String log) {
        logger.error(log);
    }
}
