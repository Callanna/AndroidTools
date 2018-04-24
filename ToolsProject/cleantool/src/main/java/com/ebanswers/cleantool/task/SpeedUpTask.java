package com.ebanswers.cleantool.task;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.ebanswers.cleantool.R;
import com.ebanswers.cleantool.data.AppProcessInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Callanna on 2017/2/21.
 */

public class SpeedUpTask {
    private static SpeedUpTask instance;

    private SpeedUpTask(Context context) {
        mContext = context.getApplicationContext();
        try {
            activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            packageManager = mContext.getPackageManager();
        } catch (Exception e) {

        }
    }

    public static SpeedUpTask getInstance(Context context) {
        synchronized (SpeedUpTask.class) {
            if (instance == null) {
                instance = new SpeedUpTask(context);
            }
            return instance;
        }
    }

    private OnProcessActionListener mOnActionListener;
    private boolean mIsScanning = false;
    private boolean mIsCleaning = false;
    ActivityManager activityManager = null;
    List<AppProcessInfo> list = null;
    PackageManager packageManager = null;
    Context mContext;
    //private FinalDb mFinalDb;

    public interface OnProcessActionListener {
        void onScanStarted();

        void onScanProgressUpdated(int current, int max, long memory, String processName);

        void onScanCompleted(List<AppProcessInfo> apps);

        void onCleanStarted();

        void onCleanCompleted(long cacheSize);
    }


    private class TaskScan
            extends AsyncTask<Void, Object, List<AppProcessInfo>> {

        private int mAppCount = 0;

        private long mAppMemory = 0;


        @Override
        protected void onPreExecute() {
            if (mOnActionListener != null) {
                mOnActionListener.onScanStarted();
            }
        }


        @Override
        protected List<AppProcessInfo> doInBackground(Void... params) {
            list = new ArrayList<>();
            ApplicationInfo appInfo = null;
            AppProcessInfo abAppProcessInfo = null;
            //得到所有正在运行的进程
            List<ActivityManager.RunningAppProcessInfo> appProcessList
                    = activityManager.getRunningAppProcesses();
            publishProgress(0, appProcessList.size(), 0, "开始扫描");

            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
                abAppProcessInfo = new AppProcessInfo(
                        appProcessInfo.processName, appProcessInfo.pid,
                        appProcessInfo.uid);
                String packName = appProcessInfo.processName;
                try {
                    appInfo = packageManager.getApplicationInfo(
                            appProcessInfo.processName, 0);

                    if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        abAppProcessInfo.isSystem = true;
                    } else {
                        abAppProcessInfo.isSystem = false;
                    }
                    Drawable icon = appInfo.loadIcon(packageManager);
                    String appName = appInfo.loadLabel(packageManager)
                            .toString();
                    abAppProcessInfo.icon = icon;
                    abAppProcessInfo.appName = appName;
                    //abAppProcessInfo.packName = packName;
                } catch (PackageManager.NameNotFoundException e) {
                    abAppProcessInfo.icon = mContext.getResources()
                            .getDrawable(
                                    R.mipmap.ic_launcher);
                    //String packName = appProcessInfo.processName;
                    appInfo = getApplicationInfo(appProcessInfo.processName.split(":")[0]);
                    if (appInfo != null) {
                        Drawable icon = appInfo.loadIcon(packageManager);
                        abAppProcessInfo.icon = icon;
                        packName = appProcessInfo.processName.split(":")[0];
                    }
                    abAppProcessInfo.isSystem = true;
                    abAppProcessInfo.appName = appProcessInfo.processName;
                    //abAppProcessInfo.packName = packName;
                }
                abAppProcessInfo.packName = packName;
                long memory = activityManager.getProcessMemoryInfo(new int[]{
                        appProcessInfo.pid})[0].getTotalPrivateDirty() * 1024;
                abAppProcessInfo.memory = memory;
                // List<Ignore> ignores = mFinalDb.findAll(Ignore.class);
                list.add(abAppProcessInfo);
                mAppMemory += memory;
                publishProgress(++mAppCount, appProcessList.size(),
                        mAppMemory, abAppProcessInfo.processName);
            }

            return list;
        }


        @Override
        protected void onProgressUpdate(Object... values) {
            if (mOnActionListener != null) {
                mOnActionListener.onScanProgressUpdated(
                        Integer.parseInt(values[0] + ""),
                        Integer.parseInt(values[1] + ""),
                        Long.parseLong(values[2] + ""), values[3] + "");
            }
        }


        @Override
        protected void onPostExecute(List<AppProcessInfo> result) {
            if (mOnActionListener != null) {
                mOnActionListener.onScanCompleted(result);
            }

            mIsScanning = false;
        }
    }


    public void killBackgroundProcesses(String processName) {
        // mIsScanning = true;

        String packageName = null;
        try {
            if (processName.indexOf(":") == -1) {
                packageName = processName;
            } else {
                packageName = processName.split(":")[0];
            }

            activityManager.killBackgroundProcesses(packageName);

            //app使用FORCE_STOP_PACKAGES权限，app必须和这个权限的声明者的签名保持一致！
            Method forceStopPackage = activityManager.getClass()
                    .getDeclaredMethod(
                            "forceStopPackage",
                            String.class);
            forceStopPackage.setAccessible(true);
            forceStopPackage.invoke(activityManager, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 结束进程
     */

    public static void killProcess(String packageName) {
        Process sh = null;
        DataOutputStream os = null;
        try {
            sh = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(sh.getOutputStream());
            final String Command = "am force-stop " + packageName + "\n";
            os.writeBytes(Command);
            os.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class TaskClean extends AsyncTask<Void, Void, Long> {


        @Override
        protected void onPreExecute() {
            if (mOnActionListener != null) {
                mOnActionListener.onCleanStarted();
            }
        }


        @Override
        protected Long doInBackground(Void... params) {
            long beforeMemory = 0;
            long endMemory = 0;
            ActivityManager.MemoryInfo memoryInfo
                    = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            beforeMemory = memoryInfo.availMem;
            List<ActivityManager.RunningAppProcessInfo> appProcessList
                    = activityManager.getRunningAppProcesses();
            ApplicationInfo appInfo = null;
            for (ActivityManager.RunningAppProcessInfo info : appProcessList) {
                String packName = info.processName;
                try {
                    packageManager.getApplicationInfo(info.processName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    appInfo = getApplicationInfo(
                            info.processName.split(":")[0]);
                    if (appInfo != null) {
                        packName = info.processName.split(":")[0];
                    }
                }

                if(!info.processName.equals(mContext.getPackageName()) && !info.processName.contains("android") && !info.processName.contains("input")) {
                    Log.d("duan","processName:"+info.processName);
                    killProcess(info.processName);
                    killBackgroundProcesses(info.processName);
                }
            }
            activityManager.getMemoryInfo(memoryInfo);
            endMemory = memoryInfo.availMem;
            return endMemory - beforeMemory;
        }


        @Override
        protected void onPostExecute(Long result) {
            if (mOnActionListener != null) {
                mOnActionListener.onCleanCompleted(result);
            }
        }
    }

    public void setOnActionListener(OnProcessActionListener listener) {
        mOnActionListener = listener;
    }


    public ApplicationInfo getApplicationInfo(String processName) {
        if (processName == null) {
            return null;
        }
        List<ApplicationInfo> appList = packageManager.getInstalledApplications(
                PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo appInfo : appList) {
            if (processName.equals(appInfo.processName)) {
                return appInfo;
            }
        }
        return null;
    }


    public boolean isScanning() {
        return mIsScanning;
    }


    public boolean isCleaning() {
        return mIsCleaning;
    }

    public void killAllProcess(){
        new TaskClean().execute();
    }

    public void stratScanProcess(){
        new TaskScan().execute();
    }
}
