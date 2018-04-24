package com.ebanswers.wifilibrary.p;

import android.net.wifi.ScanResult;

import com.ebanswers.wifilibrary.v.IViewController;

import java.util.List;

/**
 * Created by lishihui on 2017/4/10.
 */

public interface IPresenter {
    void init(List<ScanResult> list);
    void openWifiAndScan(List<ScanResult> list);
    void closeSystemWifi();
    void connect(ScanResult scanResult);
    void updateData();
    void addWifi();
    void disconnect();
    void savePassword(String ssid);
    void removePassword();
    void destory();
    IViewController getViewController();
}
