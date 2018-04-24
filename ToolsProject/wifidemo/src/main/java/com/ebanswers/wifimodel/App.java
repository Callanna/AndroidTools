package com.ebanswers.wifimodel;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by lishihui on 2017/4/18.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "dd4453de0d", false);
    }
}
