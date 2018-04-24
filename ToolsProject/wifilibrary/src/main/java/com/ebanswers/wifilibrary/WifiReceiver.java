package com.ebanswers.wifilibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Description  Wifi连接广播接收,使用静态注册，添加以下广播
 * Created by chenqiao on 2015/11/10.
 */
public class WifiReceiver extends BroadcastReceiver {

    private static final String TAG = "WifiReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
            int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
            if (error == WifiManager.ERROR_AUTHENTICATING) {
                wifiStatus(WIFI_FAIL, "密码错误");
            }
        } else if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
            wifiStatus(WIFI_UPDATE);
        } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {// wifi连接上与否
            System.out.println("网络状态改变");
            wifiStatus(WIFI_UPDATE);
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getState() == NetworkInfo.State.DISCONNECTED) {
                switch (info.getDetailedState()) {
                    case DISCONNECTED:
                        System.out.println("wifi网络连接断开");
                        wifiStatus(WIFI_DISCONNECT);
                        break;
                    case FAILED:
                        //WARNING: 这个好像并不会有这个广播出现
                        System.out.println("wifi连接失败");
                        wifiStatus(WIFI_DISCONNECT);
                        break;
                    default:
                        System.out.println("其他失败广播：" + info.getDetailedState().name());
                        break;
                }
            } else if (info.getState() == NetworkInfo.State.DISCONNECTING) {
                System.out.println("wifi正在断开连接");
            } else if (info.getState() == NetworkInfo.State.CONNECTED) {
                System.out.println("wifi网络连接成功");
                wifiStatus(WIFI_CONNECT);
            } else if (info.getState() == NetworkInfo.State.CONNECTING) {
                wifiStatus(WIFI_OPENING);
            } else if (info.getState() == NetworkInfo.State.SUSPENDED) {
                System.out.println("wifi连接暂停");
            } else if (info.getState() == NetworkInfo.State.UNKNOWN) {
                System.out.println("wifi出错，未知原因");
            }

        } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {// wifi打开与否
            int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
            if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
                System.out.println("系统关闭wifi");
                wifiStatus(WIFI_CLOSE);
            } else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
                System.out.println("系统开启wifi");
                wifiStatus(WIFI_OPEN);
            } else if (wifistate == WifiManager.WIFI_STATE_ENABLING) {
                System.out.println("系统正在开启wifi");
                wifiStatus(WIFI_OPENING);
            } else {
                System.out.println("wifi处于其他状态：" + wifistate);
            }
        } else if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {// wifi扫描结果可得
            System.out.println("Wifi扫描结果已得");
            wifiStatus(WIFI_UPDATE);
        }
    }

    /**
     * 打开了WIFI
     */
    private static final int WIFI_OPEN = 0x01;
    /**
     * 关闭了WIFI
     */
    private static final int WIFI_CLOSE = 0x02;
    /**
     * wifi正在打开
     */
    private static final int WIFI_OPENING = 0x03;
    /**
     * wifi结果刷新
     */
    private static final int WIFI_UPDATE = 0x04;
    /**
     * wifi连接错误
     */
    private static final int WIFI_FAIL = 0x05;
    /**
     * wifi连接失败
     */
    private static final int WIFI_DISCONNECT = 0x06;
    /**
     * wifi连接成功
     */
    private static final int WIFI_CONNECT = 0x07;

    private static List<WifiStateChange> mWifiStateChanges;

    /**
     * 绑定wifi改变事件
     *
     * @param wifiStateChange
     */
    public static void bindWifiState(WifiStateChange wifiStateChange) {
        if (mWifiStateChanges == null) {
            mWifiStateChanges = new ArrayList<>();
        }
        if (wifiStateChange != null) {
            mWifiStateChanges.add(wifiStateChange);
        }
    }

    /**
     * 解绑wifi改变事件
     *
     * @param wifiStateChange
     */
    public static void unBindWifiState(WifiStateChange wifiStateChange) {
        if (mWifiStateChanges != null) {
            mWifiStateChanges.remove(wifiStateChange);
        }
    }

    /**
     * wifi连接状态改变
     *
     * @param wifiTag
     */
    private void wifiStatus(int wifiTag, String... strings) {
        if (mWifiStateChanges != null) {
            //            LogUtils.d(TAG + " --> wifiStatus(): mWifiStateChanges = " + mWifiStateChanges.toString() + " mWifiStateChanges.size = " + mWifiStateChanges.size());
            for (WifiStateChange wifiStateChange : mWifiStateChanges) {
                switch (wifiTag) {
                    case WIFI_OPEN:
                        wifiStateChange.openWifi();
                        break;
                    case WIFI_CLOSE:
                        wifiStateChange.closeWifi();
                        break;
                    case WIFI_OPENING:
                        wifiStateChange.openingWifi();
                        break;
                    case WIFI_UPDATE:
                        wifiStateChange.updateWifiList();
                        break;
                    case WIFI_FAIL:
                        wifiStateChange.failWifi(strings[0]);
                        break;
                    case WIFI_DISCONNECT:
                        wifiStateChange.disconnectWifi();
                        wifiStateChange.updateWifiList();
                        break;
                    case WIFI_CONNECT:
                        wifiStateChange.updateWifiList();
                        wifiStateChange.connectWifi();
                        break;
                }
            }
        }
    }


    public interface WifiStateChange {
        //打开了wifi
        void openWifi();

        //关闭了wifi
        void closeWifi();

        //正在打开wifi
        void openingWifi();

        //连接成功
        void connectWifi();

        //连接失败
        void disconnectWifi();

        //wifi结果有刷新
        void updateWifiList();

        //异常出错
        void failWifi(String failMsg);
    }
}