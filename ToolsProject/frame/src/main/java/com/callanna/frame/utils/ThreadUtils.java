package com.callanna.frame.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author
 * Created by lishihui on 2017/1/12.
 */

public class ThreadUtils {
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static ExecutorService moreThread = Executors.newCachedThreadPool();
    public static void executeSingle(Runnable runnable){
        executorService.execute(runnable);
    }
    public static void exeMore(Runnable runnable){moreThread.execute(runnable);}
    public static void exit(){
        if (!executorService.isShutdown()){
            executorService.shutdown();
        }
        if (!moreThread.isShutdown()){
            moreThread.shutdown();
        }
    }
}
