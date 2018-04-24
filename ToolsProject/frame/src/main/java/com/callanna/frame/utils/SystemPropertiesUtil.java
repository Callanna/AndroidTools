package com.callanna.frame.utils;

import java.lang.reflect.Method;

/**
 * Description
 * Created by chenqiao on 2016/8/3.
 */
public class SystemPropertiesUtil {

    public static String getString(String key, String def) {
        try {
            Method m = Class.forName("android.os.SystemProperties").getMethod("get", String.class, String.class);
            return (String) m.invoke(null, key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    public static int getInt(String key, int def) {
        try {
            Method m = Class.forName("android.os.SystemProperties").getMethod("getInt", String.class, int.class);
            return (int) m.invoke(null, key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    public static long getLong(String key, long def) {
        try {
            Method m = Class.forName("android.os.SystemProperties").getMethod("getLong", String.class, long.class);
            return (long) m.invoke(null, key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    public static boolean getBoolean(String key, boolean def) {
        try {
            Method m = Class.forName("android.os.SystemProperties").getMethod("getBoolean", String.class, boolean.class);
            return (boolean) m.invoke(null, key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }
}
