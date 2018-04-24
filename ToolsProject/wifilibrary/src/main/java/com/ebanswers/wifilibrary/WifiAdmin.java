package com.ebanswers.wifilibrary;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lishihui on 2017/4/13.
 */

public class WifiAdmin {
    private static WifiAdmin wifiAdmin = null;
    private List<WifiConfiguration> mWifiConfiguration; //无线网络配置信息类集合(网络连接列表)
    private List<ScanResult> mWifiList; //检测到接入点信息类 集合
    private List<ScanResult> filterWifiLists;
    //描述任何Wifi连接状态
    private WifiInfo mWifiInfo;

    WifiManager.WifiLock mWifilock; //能够阻止wifi进入睡眠状态，使wifi一直处于活跃状态
    public WifiManager mWifiManager;

    /**
     * 获取该类的实例（懒汉）
     *
     * @param context
     * @return
     */
    public static WifiAdmin getInstance(Context context) {
        if (wifiAdmin == null) {
            wifiAdmin = new WifiAdmin(context);
        }
        return wifiAdmin;
    }

    private WifiAdmin(Context context) {
        //获取系统Wifi服务   WIFI_SERVICE
        this.mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //获取连接信息
        this.mWifiInfo = this.mWifiManager.getConnectionInfo();
        mWifiList = new ArrayList<>();
        filterWifiLists = new ArrayList<>();
    }


    //判定指定WIFI是否已经配置好,依据WIFI的地址BSSID,返回NetId
    public int IsConfiguration(String SSID) {
        List<WifiConfiguration> wifiConfigList = mWifiManager.getConfiguredNetworks();
        Log.i("IsConfiguration", String.valueOf(wifiConfigList.size()));
        for (int i = 0; i < wifiConfigList.size(); i++) {
            if (wifiConfigList.get(i).SSID.equals(SSID)) {//地址相同
                return wifiConfigList.get(i).networkId;
            }
        }
        return -1;
    }

    /**
     * 锁定WifiLock，当下载大文件时需要锁定
     **/
    public void AcquireWifiLock() {
        this.mWifilock.acquire();
    }

    /**
     * 创建一个WifiLock
     **/
    public void CreateWifiLock() {
        this.mWifilock = this.mWifiManager.createWifiLock("Test");
    }

    /**
     * 解锁WifiLock
     **/
    public void ReleaseWifilock() {
        if (mWifilock.isHeld()) { //判断时候锁定
            mWifilock.acquire();
        }
    }

    public boolean isWifiEnable() {
        return mWifiManager.isWifiEnabled();
    }

    /**
     * 打开Wifi
     **/
    public void OpenWifi() {
        if (!this.mWifiManager.isWifiEnabled()) { //当前wifi不可用
            this.mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭Wifi
     **/
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }


    /**
     * 添加指定网络
     **/
    public void addNetwork(WifiConfiguration paramWifiConfiguration) {
        int id = IsConfiguration(paramWifiConfiguration.SSID);
        if (id == -1) {
            id = mWifiManager.addNetwork(paramWifiConfiguration);
            Log.d("connectWifi", "updateNetwork:" + id);
        }
        if (id != -1)
            mWifiManager.enableNetwork(id, true);
    }

    //连接指定Id的WIFI
    public void ConnectWifi(int wifiId) {
        mWifiManager.enableNetwork(wifiId, true);
    }

    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }


    public void removeWifi(String ssid, int netId) {
        int id = IsConfiguration(ssid);
        Log.d("lishihuiId", "id:" + id + ",netId:" + netId);
        if (id != -1 && id == netId) {
            boolean flag = mWifiManager.removeNetwork(id);
            mWifiManager.saveConfiguration();
            Log.d("lishihuiId", "flag_ssid_id:" + flag);
        }
    }

    public void removeWifi(String ssid) {
        int id = IsConfiguration(ssid);
        Log.d("lishihuiId", "id:" + id);
        if (id != -1) {
            boolean flag = mWifiManager.removeNetwork(id);
            mWifiManager.saveConfiguration();
            Log.d("lishihuiId", "flag_id:" + flag);
        }
    }


    /**
     * 连接指定配置好的网络
     *
     * @param index 配置好网络的ID
     */
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index > mWifiConfiguration.size()) {
            return;
        }
        //连接配置好的指定ID的网络
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);
    }

    /**
     * 根据wifi信息创建或关闭一个热点
     *
     * @param paramWifiConfiguration
     * @param paramBoolean 关闭标志
     */
    public void createWifiAP(WifiConfiguration paramWifiConfiguration, boolean paramBoolean) {
        try {
            Class localClass = this.mWifiManager.getClass();
            Class[] arrayOfClass = new Class[2];
            arrayOfClass[0] = WifiConfiguration.class;
            arrayOfClass[1] = Boolean.TYPE;
            Method localMethod = localClass.getMethod("setWifiApEnabled", arrayOfClass);
            WifiManager localWifiManager = this.mWifiManager;
            Object[] arrayOfObject = new Object[2];
            arrayOfObject[0] = paramWifiConfiguration;
            arrayOfObject[1] = Boolean.valueOf(paramBoolean);
            localMethod.invoke(localWifiManager, arrayOfObject);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建一个wifi信息
     *
     * @param SSID 名称
     * @param password 密码
     * @param type 是"ap"还是"wifi"
     * @return
     */
    public WifiConfiguration createWifiInfo(String SSID, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (type == 0) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == 1) { // WIFICIPHER_NOPASS
            config.wepKeys[0] = "\"" + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == 2) {// WIFICIPHER_WEP
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == 3) { // WIFICIPHER_WPA
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private int getNetworkId(String wifissid) {
        List<WifiConfiguration> wifiConfigurationList = mWifiManager.getConfiguredNetworks();
        if (wifiConfigurationList != null && wifiConfigurationList.size() != 0) {
            for (int i = 0; i < wifiConfigurationList.size(); i++) {
                WifiConfiguration wifiConfiguration = wifiConfigurationList.get(i);
                // wifiSSID就是SSID
                if (wifiConfiguration.SSID != null && wifiConfiguration.SSID.equals(wifissid)) {
                    return wifiConfiguration.networkId;
                }
            }
        }
        return -1;
    }


    /**
     * 获取热点名
     **/
    public String getApSSID() {
        try {
            Method localMethod = this.mWifiManager.getClass().getDeclaredMethod("getWifiApConfiguration", new Class[0]);
            if (localMethod == null)
                return null;
            Object localObject1 = localMethod.invoke(this.mWifiManager, new Object[0]);
            if (localObject1 == null)
                return null;
            WifiConfiguration localWifiConfiguration = (WifiConfiguration) localObject1;
            if (localWifiConfiguration.SSID != null)
                return localWifiConfiguration.SSID;
            Field localField1 = WifiConfiguration.class.getDeclaredField("mWifiApProfile");
            if (localField1 == null)
                return null;
            localField1.setAccessible(true);
            Object localObject2 = localField1.get(localWifiConfiguration);
            localField1.setAccessible(false);
            if (localObject2 == null)
                return null;
            Field localField2 = localObject2.getClass().getDeclaredField("SSID");
            localField2.setAccessible(true);
            Object localObject3 = localField2.get(localObject2);
            if (localObject3 == null)
                return null;
            localField2.setAccessible(false);
            String str = (String) localObject3;
            return str;
        } catch (Exception localException) {
        }
        return null;
    }

    /**
     * 获取wifi名
     **/
    public String getBSSID() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return (mWifiInfo == null) ? "" : mWifiInfo.getBSSID();
    }

    /**
     * 得到配置好的网络
     **/
    public List<WifiConfiguration> getConfiguration() {
        return this.mWifiConfiguration;
    }

    /**
     * 获取ip地址
     **/
    public int getIPAddress() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    /**
     * 获取物理地址(Mac)
     **/
    public String getMacAddress() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    /**
     * 获取网络id
     **/
    public int getNetworkId() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    /**
     * 获取热点创建状态
     **/
    public int getWifiApState() {
        try {
            int i = ((Integer) this.mWifiManager.getClass().getMethod("getWifiApState", new Class[0]).invoke(this.mWifiManager, new Object[0])).intValue();
            return i;
        } catch (Exception localException) {
        }
        return 4;   //未知wifi网卡状态
    }

    /**
     * 获取wifi连接信息
     **/
    public WifiInfo getWifiInfo() {
        return this.mWifiManager.getConnectionInfo();
    }

    /**
     * 得到网络列表
     **/
    public List<ScanResult> getWifiList() {
        return this.mWifiList = mWifiManager.getScanResults();
    }

    /**
     * 查看扫描结果
     **/
    public StringBuilder lookUpScan() {
        StringBuilder localStringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            localStringBuilder.append("Index_" + new Integer(i + 1).toString() + ":");
            //将ScanResult信息转换成一个字符串包
            //其中把包括：BSSID、SSID、capabilities、frequency、level
            localStringBuilder.append((mWifiList.get(i)).toString());
            localStringBuilder.append("\n");
        }
        return localStringBuilder;
    }

    /**
     * 设置wifi搜索结果
     **/
    public void setWifiList() {
        this.mWifiList = this.mWifiManager.getScanResults();
    }

    /**
     * 开始搜索wifi
     **/
    public void startScan() {
        this.mWifiManager.startScan();
        boolean scan = mWifiManager.startScan();
        if (scan) {
            mWifiList = mWifiManager.getScanResults();
            mWifiConfiguration = mWifiManager.getConfiguredNetworks();
            Log.d("wifi_result", "mWifiList.size():" + mWifiList.size());
        }
    }

    public void updateConfigure() {
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }

    public List<ScanResult> getWifiListWithFilting() {
        mWifiList = mWifiManager.getScanResults();
        filterWifiLists.clear();
        boolean tf;
        ScanResult currentResult = null;
        for (ScanResult result : mWifiList) {
            tf = false;
            ArrayList<ScanResult> removeList = new ArrayList<>();
            ArrayList<ScanResult> addList = new ArrayList<>();
            for (ScanResult r : filterWifiLists) {
                if (r.SSID.equals(result.SSID)) {
                    tf = true;
                    int level1 = WifiManager.calculateSignalLevel(r.level, 5);
                    int level2 = WifiManager.calculateSignalLevel(result.level, 5);
                    if (level1 < level2) {
                        removeList.add(r);
                        addList.add(result);
                    }
                    break;
                }
            }
            if (result.BSSID.equals(getBSSID())) {
                currentResult = result;
            }
            filterWifiLists.removeAll(removeList);
            filterWifiLists.addAll(addList);
            if (!tf) {
                filterWifiLists.add(result);
            }
        }
        if (currentResult != null) {
            ArrayList<ScanResult> removeList = new ArrayList<>();
            for (ScanResult filter : filterWifiLists) {
                if (filter.SSID.equals(currentResult.SSID)) {
                    removeList.add(filter);
                }
            }
            filterWifiLists.removeAll(removeList);
            filterWifiLists.add(currentResult);
        }
        for (ScanResult result : filterWifiLists) {
            if (result.BSSID.equals(getBSSID())) {
                filterWifiLists.remove(result);
                filterWifiLists.add(0, result);
                break;
            }
        }
        return filterWifiLists;
    }


    /**
     * 得到接入点的BSSID
     **/
    public String GetBSSID() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }
}