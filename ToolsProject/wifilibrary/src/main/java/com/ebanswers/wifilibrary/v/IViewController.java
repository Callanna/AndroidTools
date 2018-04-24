package com.ebanswers.wifilibrary.v;

import android.net.wifi.ScanResult;

import com.ebanswers.wifilibrary.dialog.DialogUtils;

/**
 * @author
 * Created by lishihui on 2017/4/10.
 */

public interface IViewController {
    void refreshList();
    void showInputPasswordDialog(ScanResult scanResult, DialogUtils.DialogCallBack dialogCallBack);
    void closeInputPasswordDialog();
    void showDisconnectDialog(ScanResult scanResult, DialogUtils.DialogCallBack dialogCallBack);
    void closeDisconnectDialog();
    void showLoadDialog();
    void closeLoadDialog();
    void showAddWifiDialog(DialogUtils.DialogAddWifiCallBack dialogAddWifiCallBack);
    void closeAddWifiDialog();
    void openToggle();
    void closeToggle();
    void showOpenTip();
    void showCloseTip();
}
