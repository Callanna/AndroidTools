package com.callanna.frame.utils;

import android.content.Context;

import java.io.File;

/**
 * Description  根据是否有外部存储来获取缓存路径
 * Created by chenqiao on 2015/11/19.
 */
public class StorageUtils {

    public static String getCacheDir(Context context) {
            if (context.getExternalCacheDir() == null) {
                return context.getCacheDir().getAbsolutePath();
            } else {
                return context.getExternalCacheDir().getAbsolutePath();
            }
    }

    public static String getFileDir(Context context, String type) {
        if (context.getExternalFilesDir(type) == null) {
            File dir = new File(context.getFilesDir(), type);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return dir.getAbsolutePath();
        } else {
            return context.getExternalFilesDir(type).getAbsolutePath();
        }
    }
}
