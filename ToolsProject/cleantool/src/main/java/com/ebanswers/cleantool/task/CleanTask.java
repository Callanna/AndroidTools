package com.ebanswers.cleantool.task;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.ebanswers.cleantool.data.CacheListItem;
import com.ebanswers.cleantool.tools.SDCardUtils;
import com.ebanswers.cleantool.tools.StorageUtil;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.ebanswers.cleantool.tools.StorageUtil.deleteFolder;

/**
 * Created by Callanna on 2017/2/21.
 */

public class CleanTask {
    private static CleanTask instance;
    private Context context;
    private boolean isDeleteOtherData = false;
    private PackageManager packageManager;

    private CleanTask(Context context) {
        this.context = context;
        packageManager = context.getPackageManager();

    }

    public static CleanTask getInstance(Context context) {
        synchronized (CleanTask.class) {
            if (instance == null) {
                instance = new CleanTask(context);
            }
            return instance;
        }
    }

    private OnActionListener mOnActionListener;
    private long mCacheSize = 0;

    public static interface OnActionListener {
        public void onScanStarted();

        public void onScanProgressUpdated(int current, int max, long cacheSize, String packageName);

        public void onScanCompleted(List<CacheListItem> apps);

        public void onCleanStarted();

        public void onCleanCompleted(long cacheSize);
    }

    public void setmOnActionListener(OnActionListener mOnActionListener) {
        this.mOnActionListener = mOnActionListener;
    }

    public void startScanCache() {
        new TaskScan().execute();
    }

    public void cleanCache() {
        new TaskClean().execute();
    }


    private class TaskScan extends AsyncTask<Void, Object, List<CacheListItem>> {
        private int mAppCount = 0;


        @Override
        protected void onPreExecute() {
            if (mOnActionListener != null) {
                mOnActionListener.onScanStarted();
            }
        }


        @Override
        protected List<CacheListItem> doInBackground(Void... params) {
            mCacheSize = 0;

            final List<ApplicationInfo> packages
                    = context.getPackageManager().getInstalledApplications(
                    PackageManager.GET_META_DATA);
            publishProgress(0, packages.size(), 0, "开始扫描");
            final List<CacheListItem> apps = new ArrayList<>();
            File file = null;
            for (ApplicationInfo pkg : packages) {
                file = new File(SDCardUtils.getRootPath().getAbsolutePath() + "/Android/data/" + pkg.packageName);
                try {
                    mCacheSize += StorageUtil.getFolderSize(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("duanyl", "doInBackground: "+mCacheSize +",packages.size():"+packages.size() + pkg.packageName);
                publishProgress(++mAppCount, packages.size(), mCacheSize, pkg.packageName);
            }

            File[] files = new File(SDCardUtils.getRootPath().getAbsolutePath() + "/").listFiles();
            for (int i = 0; i < files.length; i++) {
                if (isDelete(files[i].getName())) {
                    try {
                        mCacheSize += StorageUtil.getFolderSize(files[i]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    publishProgress(++mAppCount, packages.size(), mCacheSize, "");
                }
            }
            return new ArrayList<>(apps);
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
        protected void onPostExecute(List<CacheListItem> result) {
            if (mOnActionListener != null) {
                mOnActionListener.onScanCompleted(result);
            }
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
            final List<ApplicationInfo> packages
                    = context.getPackageManager().getInstalledApplications(
                    PackageManager.GET_META_DATA);
            for (ApplicationInfo pkg : packages) {
                //不要清理输入法，搜狗输入法，百度输入法等。
                if (!pkg.packageName.contains("android") && !pkg.packageName.equals(context.getPackageName()) && !pkg.packageName.contains("input")) {
                    cleanCache(pkg.packageName);
                }
            }
//            if (isDeleteOtherData) {
//                deleteOtherData();
//            }
            return mCacheSize;
        }


        @Override
        protected void onPostExecute(Long result) {
            mCacheSize = 0;
            if (mOnActionListener != null) {
                mOnActionListener.onCleanCompleted(result);
            }
        }
    }

    public void cleanCache(String appPackageName) {
        deleteFolder(SDCardUtils.getRootPath().getAbsolutePath() + "/Android/data/" + packageManager);
        Process sh;
        DataOutputStream os;
        try {
            sh = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(sh.getOutputStream());
            final String Command = "pm clear " + appPackageName + "\n";
            os.writeBytes(Command);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteOtherData() {

        File[] files = new File(SDCardUtils.getRootPath().getAbsolutePath() + "/").listFiles();
        for (int i = 0; i < files.length; i++) {
            if (isDelete(files[i].getName())) {
                boolean result = deleteFolder(files[i].getAbsolutePath());
            }
        }
    }

    private LinkedList<String> listFiles = new LinkedList<>();

    public void setListFiles(LinkedList<String> listFiles) {
        this.listFiles.clear();
        this.listFiles.addAll(listFiles);
        if (listFiles != null) {
            isDeleteOtherData = true;
        } else {
            isDeleteOtherData = false;
        }
    }

    private boolean isDelete(String name) {
        for (int i = 0; i < listFiles.size(); i++) {
            if (name.equals(listFiles.get(i)) || name.contains(".")) {
                return false;
            }
        }
        return true;
    }

}
