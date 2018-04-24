package com.ebanswers.wifilibrary;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Description
 * Created by chenqiao on 2015/11/17.
 */
public class WifiConfig {
    public static final String WIFI_STATE_CHANGED = "wifi_state_changed";
    public static final String WIFI_ENABLE = "wifi_enable";
    public static final String WIFI_DISABLED = "wifi_disabled";
    public static final String WIFI_SCANNING = "wifi_scanning";
    public static final String WIFI_SCAN_RESULT = "wifi_scan_result";
    public static final String WIFI_CONNECTED = "wifi_connected";
    public static final String WIFI_DISCONNECTED = "wifi_disconnected";
    public static final String WIFI_AUTHENTICATE_FAILED = "wifi_authenticate_failed";

    private static WifiConfig instance;
    private final SharedPreferences sharedPrefs;

    private WifiConfig(Context context) {
        sharedPrefs = context.getApplicationContext().getSharedPreferences("wifi_preferences", Context.MODE_PRIVATE);// .MODE_PRIVATE);
    }

    public static WifiConfig getInstance(Context context) {
        if (instance == null) {
            synchronized (WifiConfig.class) {
                if (instance == null) {
                    instance = new WifiConfig(context);
                }
            }
        }
        return instance;
    }

    /**
     * 保存Wifi密码
     *
     * @param ssid   wifi名称
     * @param passwd 密码
     */
    public void savePasswd(String ssid, String passwd) {
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putString(ssid, passwd);
        edit.commit();
    }

    /**
     * 保存着的Wifi
     */
//    public void setSaveWifiId(String SSID,int wifiNetId) {
//        SharedPreferences.Editor edit = sharedPrefs.edit();
//        edit.putInt(SSID+"wifi", wifiNetId);
//        edit.commit();
//    }
//
//    public int getSaveWifiId(String SSID) {
//        return sharedPrefs.getInt(SSID+"wifi", -1);
//    }
//
//    public void setSsid(String SSID){
//        SharedPreferences.Editor edit = sharedPrefs.edit();
//        edit.putString("ssid_wifi", SSID);
//        edit.commit();
//    }
//
//    public String getSsid(){
//        return sharedPrefs.getString("ssid_wifi", "");
//    }
    /**
     * 获取已保存的Wifi密码
     *
     * @param ssid wifi名称
     */
    public String getPasswd(String ssid) {
        return sharedPrefs.getString(ssid, "");
    }

    /**
     * 移除已保存的wifi密码
     *
     * @param ssid wifi名称
     */
    public void removePasswd(String ssid) {
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.remove(ssid);
        edit.commit();
    }

    public static void destory(){
        instance = null;
    }
}
