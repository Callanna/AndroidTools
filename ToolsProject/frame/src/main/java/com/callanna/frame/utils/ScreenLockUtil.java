package com.callanna.frame.utils;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Callanna on 2016/7/25.
 */
public class ScreenLockUtil {

    private static final int CLOSE_SRCEEN = 0x0001;
    private static ScreenLockUtil instance;
    private boolean isSrceenEnabled = true;
    private PowerManager powerManager;
    private KeyguardManager keyguardManager;
    private PowerManager.WakeLock pwl;
    private KeyguardManager.KeyguardLock kgl;

    private ScreenLockUtil(Context context) {
        init(context);
    }

    public void init(Context context) {
        //获取电源管理器对象
        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        pwl = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "bright");
        //得到键盘锁管理器对象
        keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        kgl = keyguardManager.newKeyguardLock("unLock");
    }

    public static ScreenLockUtil getInstance(Context context) {
        synchronized (ScreenLockUtil.class) {
            if (instance == null) {
                instance = new ScreenLockUtil(context);
            }
            return instance;
        }
    }

    public void unlock() {
        if (!pwl.isHeld()) {
            pwl.acquire();
        }
    }

    public void lock() {
        if (pwl.isHeld()) {
            //锁屏
            kgl.reenableKeyguard();
            //释放wakeLock，关灯
            pwl.release();
        }
    }

    public static void goToSleep(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        try {
            Method method = powerManager.getClass().getMethod("goToSleep", String.class);
            method.setAccessible(true);
            method.invoke(powerManager, SystemClock.uptimeMillis());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断当前应用是否在前台
     *
     * @param context
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        String currentPackageName = context.getPackageName();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
        String packageName = info.topActivity.getPackageName();
        if (currentPackageName.equals(packageName)) {
            return true;
        }
        return false;
    }


    public static boolean setSleepTime(Context context,int time) {
        boolean result = Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, time);
        return result;
    }

    public static int getSleepTime(Context context) {
        int result = 0;
        try {
            result = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

}
