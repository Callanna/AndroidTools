/**
 * @author Joe
 */
package com.callanna.frame.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Android开发调试日志工具类[支持保存到SD卡]<br>
 * <br>
 * <p>
 * 需要一些权限: <br>
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /> <br>
 * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"
 * /><br>
 *
 * @author PMTOAM
 */
public class LogUtils {

    public static final String CACHE_DIR_NAME = "MyLog";
    public static final String TAG = "Frame";
    public static boolean isDebugModel = false;// 是否输出日志
    public static boolean isSaveDebugInfo = true;// 是否保存调试日志
    public static boolean isSaveCrashInfo = true;// 是否保存报错日志
    private static ExecutorService poolExecutor = Executors.newSingleThreadExecutor();

    public static void v(final String tag, final String msg) {
        if (isDebugModel) {
            Log.v(tag, "--> " + msg);
        }
    }

    public static void d(final String tag, final String msg) {
        if (isDebugModel) {
            Log.d(tag, "--> " + msg);
        }
        if (isSaveDebugInfo) {
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    write(time() + tag + " --> " + msg + "\n");
                }
            });
        }
    }

    public static void i(final String tag, final String msg) {
        if (isDebugModel) {
            Log.i(tag, "--> " + msg);
        }
    }

    public static void w(final String tag, final String msg) {
        if (isDebugModel) {
            Log.w(tag, "--> " + msg);
        }
    }

    /**
     * 调试日志，便于开发跟踪。
     *
     * @param tag
     * @param msg
     */
    public static void e(final String tag, final String msg) {
        if (isDebugModel) {
            Log.e(tag, " [CRASH] --> " + msg);
        }

        if (isSaveCrashInfo) {
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    write(time() + tag + " [CRASH] --> " + msg + "\n");
                }
            });
        }
    }

    /**
     * try catch 时使用，上线产品可上传反馈。
     *
     * @param tag
     * @param tr
     */
    public static void e(final String tag, final Throwable tr) {
        if (isSaveCrashInfo) {
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    write(time() + tag + " [CRASH] --> " + getStackTraceString(tr) + "\n");
                }
            });
        }
    }

    public static void v(String msg) {
        if (isDebugModel) {
            Log.v(TAG, "--> " + msg);
        }
    }

    public static void d(final String msg) {
        if (isDebugModel) {
            Log.d(TAG, "--> " + msg);
        }
        if (isSaveDebugInfo) {
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    write(time() + TAG + " --> " + msg + "\n");
                }
            });
        }
    }

    public static void i(String msg) {
        if (isDebugModel) {
            Log.i(TAG, "--> " + msg);
        }
    }

    public static void w(String msg) {
        if (isDebugModel) {
            Log.w(TAG, "--> " + msg);
        }
    }

    /**
     * 调试日志，便于开发跟踪。
     *
     * @param msg
     */
    public static void e(final String msg) {
        if (isDebugModel) {
            Log.e(TAG, " [CRASH] --> " + msg);
        }

        if (isSaveCrashInfo) {
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    write(time() + TAG + "[CRASH] --> " + msg + "\n");
                }
            });
        }
    }

    /**
     * try catch 时使用，上线产品可上传反馈。
     *
     * @param tr
     */
    public static void e(final Throwable tr) {
        if (isSaveCrashInfo) {
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    write(time() + TAG + " [CRASH] --> " + getStackTraceString(tr) + "\n");
                }
            });
        }
    }

    /**
     * 获取捕捉到的异常的字符串
     *
     * @param tr
     * @return
     */
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * 标识每条日志产生的时间
     *
     * @return
     */
    private static String time() {
        return "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(System.currentTimeMillis())) + "] ";
    }

    /**
     * 以年月日作为日志文件名称
     *
     * @return
     */
    private static String date() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(System.currentTimeMillis()));
    }

    /**
     * 保存到日志文件
     *
     * @param content
     */
    public static synchronized void write(String content) {
        try {
            FileWriter writer = new FileWriter(getFile(), true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取日志文件路径
     *
     * @return
     */
    public static String getFile() {
        File sdDir = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            sdDir = Environment.getExternalStorageDirectory();

        File cacheDir = new File(sdDir + File.separator + CACHE_DIR_NAME);
        if (!cacheDir.exists())
            cacheDir.mkdir();

        File filePath = new File(cacheDir + File.separator + date() + ".log");

        return filePath.toString();
    }

}