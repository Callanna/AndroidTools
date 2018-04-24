package com.ebanswers.wifilibrary.m;

import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Created by lishihui on 2017/4/10.
 */

public interface ScanCallBack{
    void callBack(List<ScanResult> list);
}
