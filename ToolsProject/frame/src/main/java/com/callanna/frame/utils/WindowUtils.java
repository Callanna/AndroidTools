package com.callanna.frame.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Callanna on 2016/9/23.
 */
public class WindowUtils {
    public static final String ALARM = "alarm";
    public static final String SRCEEN_LOCK = "SRCEEN_LOCK";
    private Map<String, View> viewMap;
    private WindowManager mWindowManager = null;
    private WindowManager.LayoutParams params;

    private static WindowUtils instance;

    public static WindowUtils getIntsance() {
        if (instance == null) {
            instance = new WindowUtils();
        }
        return instance;
    }

    private WindowUtils() {
        initParams();
    }


    /**
     * 显示弹出框
     *
     * @param context
     */
    public void showPopupWindow(Context context, View view, String tag, boolean isFull) {
        if (viewMap == null) {
            viewMap = new HashMap<>();
        }
        hidePopupWindow(tag);
        if (viewMap.get(tag) == null) {
            viewMap.put(tag, view);
        }
        if (params != null) {
            if (isFull) {
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
            } else {
                params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            }
        }
        // 获取WindowManager
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(viewMap.get(tag), params);

    }

    /**
     * 初始化LayoutParams
     */
    private void initParams() {
        params = new WindowManager.LayoutParams();
        // 类型
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        // WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        // 设置flag
        int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;//窗口占满整个屏幕，忽略周围的装饰边框（例如状态栏）。此窗口需考虑到装饰边框的内容。
        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
        params.flags = flags;
        // 不设置这个弹出框的透明遮罩显示为黑色
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.CENTER;
    }

    /**
     * 隐藏弹出框
     */
    public void hidePopupWindow(String tag) {
        if (viewMap.get(tag) != null) {
            mWindowManager.removeView(viewMap.get(tag));
            viewMap.remove(tag);
        }
    }

    public static void hideStatusBar(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.hideNaviBar");
        intent.putExtra("hide", true);
        context.sendBroadcast(intent);
    }

    public static void showStatusBar(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.hideNaviBar");
        intent.putExtra("hide", false);
        context.sendBroadcast(intent);
    }

    public static void setBright(int value, Activity context) {
        // 设置系统亮度模式
        int mode = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, -1);
        if (mode == 1) {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);//为手动调节屏幕亮度
        }
//      Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, value);
//        context.getContentResolver().notifyChange(uri,null);
    }

    public static int getBright(Context context) {
        // 获取系统亮度模式
        try {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return 255;
    }

    // 根据亮度值修改当前window亮度
    public static void changeAppBrightness(Context context, int brightness) {
        Window window = ((Activity) context).getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
        }
        window.setAttributes(lp);
    }

    //设置屏幕亮度的函数
    public static void setScreenBrightness(Activity context, float num) {
        WindowManager.LayoutParams layoutParams = context.getWindow().getAttributes();
        layoutParams.screenBrightness = num;//设置屏幕的亮度
        context.getWindow().setAttributes(layoutParams);
    }
}
