package com.callanna.frame.application;

import android.app.Application;

import com.callanna.frame.hanlder.CrashFileSaveListener;
import com.callanna.frame.hanlder.CrashHandler;


/**
 * describe
 * Created by liudong on 2016/9/18.
 */
public abstract class BaseApplication extends Application implements CrashFileSaveListener {
    protected CrashHandler crashHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * 设置默认异常处理Handler
         */

        crashHandler = CrashHandler.getInstance(this);
        crashHandler.init(getApplicationContext());
        onBaseCreate();
    }

    protected abstract void onBaseCreate();

    @Override
    public abstract void crashFileSaveTo(String filePath);

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
