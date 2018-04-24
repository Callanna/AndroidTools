package com.ebanswers.wifilibrary.m;

import android.content.Context;

/**
 * @author
 * Created by lishihui on 2017/4/10.
 */

public interface IModelController {
    void getWifiList(Context context,ScanCallBack scanCallBack);
    void addNetWork(Context context);
}
