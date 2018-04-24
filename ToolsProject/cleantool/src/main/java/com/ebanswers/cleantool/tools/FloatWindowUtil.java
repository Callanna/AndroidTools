package com.ebanswers.cleantool.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.ebanswers.cleantool.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Callanna on 2016/11/10.
 */

public class FloatWindowUtil {
    //标志位
    public static final String FLOAT_MAIN = "FLOAT_LAYOUT_MIAN";
    private Map<String, View> viewMap;
    private WindowManager mWindowManager = null;
    private Context mContext = null;
    private WindowManager.LayoutParams params;

    private static FloatWindowUtil instance;

    public static FloatWindowUtil getIntsance() {
        if (instance == null) {
            instance = new FloatWindowUtil();
        }
        return instance;
    }

    private FloatWindowUtil() {
        initParams();
    }


    /**
     * 显示弹出框
     *
     * @param context
     */
    public void showPopupWindow(Context context, View view, String tag) {
        if (viewMap == null) {
            viewMap = new HashMap<>();
        }
        if (viewMap.get(tag) == null) {
            viewMap.put(tag, view);
        }
        // 获取应用的Context
        mContext = context.getApplicationContext();
        // 获取WindowManager
        mWindowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
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
        params.windowAnimations = R.style.MyAnimationWindowIn;
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
    public static void closeKeyboard(Activity mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusView = mContext.getCurrentFocus();
        if (imm != null && focusView != null) {
            imm.hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
