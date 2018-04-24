package com.callanna.frame.hanlder;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import static android.os.FileObserver.CLOSE_WRITE;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类 来接管程序,并记录 发送错误报告.. 註冊方式
 * CrashHandler crashHandler = CrashHandler.getInstance(); //注册crashHandler
 * crashHandler.init(getApplicationContext()); //发送以前没发送的报告(可选)
 * crashHandler.sendPreviousReportsToServer();
 */

/**
 * Description   程序异常崩溃记录类
 * Created by chenqiao on 2015/9/6.
 */
public class CrashHandler implements UncaughtExceptionHandler {
    /**
     * Debug Log tag
     */
    public static final String TAG = "CrashHandler";
    /**
     * 是否开启日志输出,在Debug状态下开启, 在Release状态下关闭以提示程序性能
     */
    public static final boolean DEBUG = true;

    private static final String ANR_TRACE_FILEPATH = "/data/anr/traces.txt";
    /**
     * 系统默认的UncaughtException处理类
     */
    private UncaughtExceptionHandler mDefaultHandler;
    /**
     * CrashHandler实例
     */
    private volatile static CrashHandler INSTANCE;
    /**
     * 程序的Context对象
     */
    private Context mContext;
    /**
     * 监听文件夹变化
     */
    private FileObserver fileObserver = null;


    /**
     * 使用Properties来保存设备的信息和错误堆栈信息
     */
    private Properties mDeviceCrashInfo = new Properties();
    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";
    private static final String STACK_TRACE = "STACK_TRACE";
    private static final String ANR_TRACE = "ANR_TRACE";
    /**
     * 错误报告文件的扩展名
     */
    private static final String CRASH_REPORTER_EXTENSION = ".txt";

    private static CrashFileSaveListener saveListener;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        synchronized (CrashHandler.class) {
            if (INSTANCE == null) {
                INSTANCE = new CrashHandler();
            }
            return INSTANCE;
        }
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     *
     * @param ctx
     */
    public void init(Context ctx) {
       init(ctx,null);

    }
    public void init(Context ctx,CrashFileSaveListener listener) {
        mContext = ctx;
        saveListener = listener;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        startANRListener();
    }
    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {

            if(saveListener == null){
                Looper.prepare();
                restartApp();
            }
        }

    }
    public void restartApp(){

        Intent intent = mContext.getPackageManager()
                .getLaunchIntentForPackage(mContext.getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis()+1000 , restartIntent);
        System.exit(10);
    }
    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }
        final String msg = ex.getLocalizedMessage();
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "程序出错啦!:" + msg, Toast.LENGTH_SHORT)
                        .show();
                Looper.loop();
            }

        }.start();
        // 收集设备信息
        collectCrashDeviceInfo(mContext);
        // 保存错误报告文件
        String crashFileName = saveCrashInfoToFile(ex);
        // 发送错误报告到服务器
        sendPreviousReports(crashFileName);
        return true;
    }

    /**
     * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告
     */
    public void sendPreviousReports(String filename) {
        //sendCrashReportsToServer(mContext);
        if(saveListener != null){
            saveListener.crashFileSaveTo(filename);
        }
    }



    /**
     * 获取错误报告文件名
     *
     * @param ctx
     * @return
     */
    private String[] getCrashReportFiles(Context ctx) {
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(CRASH_REPORTER_EXTENSION);
            }
        };

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String filePath = mContext.getExternalFilesDir("Crash").getAbsolutePath();
            File dir = new File(filePath);
            return dir.list(filter);
        } else {
            File filesDir = ctx.getFilesDir();
            return filesDir.list(filter);
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return
     */
    private String saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        String result = info.toString();
        printWriter.close();
        mDeviceCrashInfo.put(STACK_TRACE, result);
        String fileName = "";
        try {
            long timestamp = System.currentTimeMillis();
            FileOutputStream trace;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HHmmss", Locale.getDefault());
            String time = format.format(new Date(timestamp));
            fileName = "crash-" + time + CRASH_REPORTER_EXTENSION;
            String filePath;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                filePath = mContext.getExternalFilesDir("Crash").getAbsolutePath();
            } else {
                filePath = mContext.getFilesDir().getAbsolutePath();
            }
            File file = new File(filePath, fileName);
            trace = new FileOutputStream(file);
            mDeviceCrashInfo.storeToXML(trace, "crashLog");
            trace.flush();
            trace.close();
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing report file..." + fileName, e);
        }
        return null;
    }
    /**
     * 保存错误信息到文件中
     *
     * @return
     */
    private String saveANRInfoToFile(String anrmsg) {

        StringBuilder anrlog = readFile(ANR_TRACE_FILEPATH);
        if(!TextUtils.isEmpty(anrlog)){
            mDeviceCrashInfo.put(ANR_TRACE, anrlog);
        }
        mDeviceCrashInfo.put(STACK_TRACE, anrmsg);
        String fileName = "";
        try {
            long timestamp = System.currentTimeMillis();
            FileOutputStream trace;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HHmmss", Locale.getDefault());
            String time = format.format(new Date(timestamp));
            fileName = "crash-" + time + CRASH_REPORTER_EXTENSION;
            String filePath;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                filePath = mContext.getExternalFilesDir("Crash").getAbsolutePath();
            } else {
                filePath = mContext.getFilesDir().getAbsolutePath();
            }
            File file = new File(filePath, fileName);
            trace = new FileOutputStream(file);
            Log.d(TAG, "saveANRInfoToFile: 3");
            mDeviceCrashInfo.storeToXML(trace, "crashLog");
            Log.d(TAG, "saveANRInfoToFile: 4");
            trace.flush();
            trace.close();
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing report file..."  + fileName, e);
        }
        return null;
    }
    public static StringBuilder readFile(String filePath) {
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder("");
        if (file == null || !file.isFile()) {
            return null;
        }
        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), "UTF-8");
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null && fileContent.length()<=10240) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            reader.close();
            Log.d("duanyl", "readFile: "+fileContent);
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }
    /**
     * 收集程序崩溃的设备信息
     *
     * @param ctx
     */
    public void collectCrashDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                mDeviceCrashInfo.put(VERSION_NAME,
                        pi.versionName == null ? "not set" : pi.versionName);
                mDeviceCrashInfo.put(VERSION_CODE, String.valueOf(pi.versionCode));
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Error while collect package info", e);
        }
        // 使用反射来收集设备信息.在Build类中包含各种设备信息,
        // 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mDeviceCrashInfo.put(field.getName(), field.get(null).toString());
                if (DEBUG) {
                    Log.d(TAG, field.getName() + " : " + field.get(null).toString());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error while collect crash info", e);
            }
        }
    }


    protected synchronized void startANRListener() {
        Log.d("duanyl", "startANRListener: ");
        fileObserver= new FileObserver("/data/anr/", CLOSE_WRITE) {
                public void onEvent(int event, String path) {
                    Log.d("duanyl", "onEvent: "+path);
                    if(path != null) {
                        String filepath = "/data/anr/" + path;
                        if(filepath.contains("trace")) {
                            filiterANR();
                        }
                    }
                }
            };
            try {
                fileObserver.startWatching();
                Log.d(TAG, "start anr monitor!" );

            } catch (Throwable var2) {
                fileObserver = null;
                Log.d(TAG, "start anr monitor failed!" );

            }

    }
    private long lastTimes = 0;
    private void filiterANR( ) {
        try {
            long nowTime = System.currentTimeMillis() ;
            if(nowTime - lastTimes < 10000L) {
                Log.d(TAG,"should not process ANR too Fre in 10000");
            } else {
                   lastTimes = nowTime;
                    ActivityManager.ProcessErrorStateInfo errorStateInfo = findError(mContext, 10000L);
                    if(errorStateInfo == null) {
                        Log.d(TAG,"proc state is unvisiable!" );
                    } else if(errorStateInfo.pid == android.os.Process.myPid()) {
                        Log.d(TAG,"not mind proc!"+ errorStateInfo.processName );
                        String msg = "Found ANR in !"+errorStateInfo.processName+":\r\n "+errorStateInfo.longMsg+"\n\n";
                        String crashFileName = "";
                        crashFileName = saveANRInfoToFile(msg);
                        if(saveListener != null){
                            saveListener.crashFileSaveTo(crashFileName);
                        }else {
                            restartApp();
                        }

                    } else {
                        Log.d(TAG,"found visiable anr , start to process!" );
                    }
            }
        } catch (Throwable throwable) {
            Log.d(TAG,"handle anr error  "+  throwable.getMessage());
        }
    }
    protected ActivityManager.ProcessErrorStateInfo findError(Context context, long time) {
        time = time < 0L?0L:time;
        //z.c("to find!", new Object[0]);
        ActivityManager var4 = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        long var5 = time;
        int index = 0;
        do {
            Log.d(TAG, "waiting!" );
            List errorStateInfoList = var4.getProcessesInErrorState();
            if(errorStateInfoList != null) {
                Iterator var9 = errorStateInfoList.iterator();
                while(var9.hasNext()) {
                    ActivityManager.ProcessErrorStateInfo errorStateInfo = (ActivityManager.ProcessErrorStateInfo)var9.next();
                    if(errorStateInfo.condition == 2  ) {
                        Log.d(TAG, "a: found!"+errorStateInfo.processName+","+errorStateInfo.shortMsg+","+errorStateInfo.longMsg+",");
                        return errorStateInfo;
                    }
                }
            }
        } while((long)(index++) < var5);
        Log.d(TAG,"end!" );
        return null;
    }

}