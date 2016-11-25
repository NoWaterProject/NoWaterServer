package com.NoWater.util;

import java.util.UUID;

/**
 * Created by ByYoung on 2016/11/25.
 */
public final class Uuid {

    public static String getUuid() {
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid);
        String re = uuid.toString();
        re.replace("-", "");
        return re;
    }
}
