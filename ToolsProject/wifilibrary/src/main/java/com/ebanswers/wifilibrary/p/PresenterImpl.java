package com.ebanswers.wifilibrary.p;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ebanswers.wifilibrary.NetUtils;
import com.ebanswers.wifilibrary.WifiAdmin;
import com.ebanswers.wifilibrary.WifiConfig;
import com.ebanswers.wifilibrary.WifiReceiver;
import com.ebanswers.wifilibrary.dialog.DialogUtils;
import com.ebanswers.wifilibrary.m.ModelControllerImpl;
import com.ebanswers.wifilibrary.v.IViewController;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Created by lishihui on 2017/4/10.
 */

public class PresenterImpl implements IPresenter {
    private IViewController viewController;
    private Context mContext;
    private WifiReceiver wifiReceiver;
    private ModelControllerImpl modelController;
    private Executor threadPool = Executors.newCachedThreadPool();
    private String current_ssid, current_password;

    public PresenterImpl(Context context, IViewController controller,List<ScanResult> list) {
        mContext = context;
        this.viewController = controller;
        modelController = new ModelControllerImpl(context, this,list);
        WifiReceiver.bindWifiState(modelController);
        wifiReceiver = new WifiReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.SCAN_RESULTS");
        intentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
        mContext.registerReceiver(wifiReceiver, intentFilter);
    }

    @Override
    public void init(final List<ScanResult> list) {
        if (WifiAdmin.getInstance(mContext).isWifiEnable()) {
            viewController.openToggle();
            openWifiAndScan(list);
        } else {
            viewController.closeToggle();
            viewController.showCloseTip();
        }
    }

    @Override
    public void openWifiAndScan(final List<ScanResult> list) {
        viewController.showOpenTip();
        scan();
    }

    private void scan() {
        WifiAdmin.getInstance(mContext).OpenWifi();
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                WifiAdmin.getInstance(mContext).startScan();
            }
        });
    }

    @Override
    public void closeSystemWifi() {
        viewController.showCloseTip();
        WifiAdmin.getInstance(mContext).closeWifi();
    }

    @Override
    public void connect(final ScanResult scanResult) {
        Log.d("isSuccess1", "scanResult");
        //判断点击是否是当前已经连接的
        if (scanResult.BSSID.equals(WifiAdmin.getInstance(mContext).getBSSID()) && NetUtils.isWifi(mContext)) {
            viewController.showDisconnectDialog(scanResult, new DialogUtils.DialogCallBack() {
                @Override
                public void callBack(View view, ScanResult scanResult, String str) {
                    viewController.closeDisconnectDialog();
                    Log.d("disconnect", "scanResult.SSID:" + scanResult.SSID);
                    WifiConfig.getInstance(mContext).removePasswd(scanResult.SSID);
                    if (WifiAdmin.getInstance(mContext).getNetworkId() != -1) {
                        int id = WifiAdmin.getInstance(mContext).getNetworkId();
                        WifiAdmin.getInstance(mContext).disconnectWifi(id);
                        WifiAdmin.getInstance(mContext).removeWifi("\"" + scanResult.SSID + "\"", id);
                    }

                }

                @Override
                public void ignore() {

                }

                @Override
                public void cancel() {
                    viewController.closeDisconnectDialog();
                }
            });
        } else {
            final String security = scanResult.capabilities.toLowerCase();
            if (!security.contains("wpa") && !security.contains("wep")) {
                connectWifi(scanResult.SSID, "", "");
                viewController.showLoadDialog();
            } else {
                viewController.showInputPasswordDialog(scanResult, new DialogUtils.DialogCallBack() {
                    @Override
                    public void callBack(View view, ScanResult scanResult, String passward) {
                        viewController.closeInputPasswordDialog();
                        viewController.showLoadDialog();
                        current_ssid = scanResult.SSID;
                        current_password = passward;
                        Log.d("isSuccess1", "scanResult.SSID:" + scanResult.SSID);
                        Log.d("isSuccess1", "passward:" + passward);
                        if (security.contains("wpa")) {
                            Log.d("lishihui_netId", "wpa:" + scanResult.SSID + ",psd:" + passward);
                            connectWifi(scanResult.SSID, passward, "wpa");
                        } else if (security.contains("wep")) {
                            connectWifi(scanResult.SSID, passward, "wep");
                        }
                    }

                    @Override
                    public void ignore() {
                        Log.d("isSuccess1", "ignore()");
                        Log.d("isSuccess1", "scanResult.BSSID:" + scanResult.BSSID);
                        viewController.closeInputPasswordDialog();
                        WifiConfig.getInstance(mContext).removePasswd(scanResult.SSID);
                        WifiAdmin.getInstance(mContext).removeWifi("\"" + scanResult.SSID + "\"");
                    }

                    @Override
                    public void cancel() {
                        viewController.closeInputPasswordDialog();
                    }
                });

            }

        }

    }

    private void connectWifi(String ssid, String password, String secrity) {
        int netId = WifiAdmin.getInstance(mContext).IsConfiguration("\"" + ssid + "\"");
        if (netId != -1) {
            Log.d("connectWifi", "justId");
            WifiAdmin.getInstance(mContext).ConnectWifi(netId);
        } else {
            if (TextUtils.isEmpty(secrity)) {
                Log.d("connectWifi", "nopsd");
                WifiAdmin.getInstance(mContext).addNetwork(WifiAdmin.getInstance(mContext).createWifiInfo(ssid, "", 1));
            } else if (secrity.equals("wpa")) {
                Log.d("connectWifi", "wpa");
                WifiAdmin.getInstance(mContext).addNetwork(WifiAdmin.getInstance(mContext).createWifiInfo(ssid, password, 3));
            } else if (secrity.equals("wep")) {
                Log.d("connectWifi", "wep");
                WifiAdmin.getInstance(mContext).addNetwork(WifiAdmin.getInstance(mContext).createWifiInfo(ssid, password, 2));
            }
        }
    }


    public void updateData() {
        WifiAdmin.getInstance(mContext).updateConfigure();
        viewController.refreshList();
    }

    @Override
    public void addWifi() {
        viewController.showAddWifiDialog(new DialogUtils.DialogAddWifiCallBack() {
            @Override
            public void callBack(String ssid, String password, int type) {
                viewController.closeAddWifiDialog();
                current_ssid = ssid;
                current_password = password;
                Log.d("isSuccess1", "scanResult.SSID:" + ssid);
                Log.d("isSuccess1", "passward:" + password);
                int netId = WifiAdmin.getInstance(mContext).IsConfiguration("\"" + ssid + "\"");
                if (netId != -1) {
                    Log.d("connectWifi", "justId");
                    WifiAdmin.getInstance(mContext).ConnectWifi(netId);
                }else {
                    WifiAdmin.getInstance(mContext).addNetwork(WifiAdmin.getInstance(mContext).createWifiInfo(ssid, password, type));
                }
                viewController.showLoadDialog();
            }

            @Override
            public void cancel() {
                viewController.closeAddWifiDialog();
            }

        });
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void savePassword(String ssid) {
        if (ssid.equals(current_ssid))
            WifiConfig.getInstance(mContext).savePasswd(current_ssid, current_password);
    }

    @Override
    public void removePassword() {
        WifiConfig.getInstance(mContext).removePasswd(current_ssid);
    }


    @Override
    public void destory() {
        mContext.unregisterReceiver(wifiReceiver);
        WifiConfig.destory();
        mContext = null;
    }

    @Override
    public IViewController getViewController() {
        return viewController;
    }


}
