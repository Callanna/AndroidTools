package com.ebanswers.commitlog;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ebanswers.sdk.IQSDK;
import com.ebanswers.sdk.cloud.model.RegisterResult;
import com.ebanswers.sdk.cloud.task.RegisterTask;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private String filename = "";
    private String filename2 = "";
    private String accessToken = "";
    private String deviceid = "";
    private TextView textView ;
    private byte[] data = new byte[]{0x20, 0x53, 0x75, 0x63, 0x63, 0x65, 0x73, 0x73, 0x66, 0x75, 0x6C, 0x6C, 0x79, 00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView =  ((TextView)findViewById(R.id.tv_file));
        filename =  "/mnt/sdcard/adblog.txt";
        filename2 =  "/mnt/sdcard/upadblog.txt";

        sharedPrefs = getSharedPreferences("smart_freezer", Context.MODE_PRIVATE);
        uploadLog();
        IQSDK.init(this);
        IQSDK.registerDevice(new RegisterTask.OnRegisterListener() {
            @Override
            public void onGetToken(String access_token) {
                Log.d("duanyl", "onGetToken: "+access_token);
               setToken(access_token);
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onRegisterSuccessfully(RegisterResult result) {
               deviceid = result.getDevice_id();
                setDeviceId(deviceid);
                Log.d("duanyl", "onRegisterSuccessfully deviceid: "+deviceid);
            }
        });

        findViewById(R.id.btn_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File f = new File(filename);
                        if(f.exists()){
                            f.delete();
                        }
                        Log.d("duanyl", "onCreate: "+ f.getPath());
                        //ShellUtils.execCommand("adb logcat -v time -f "+filename,true);
                        ShellUtils.execCommand("logcat -v time -f "+filename,true);
                    }


                }).start();
                ((TextView)findViewById(R.id.tv_file)).setText(filename);
            }
        });
        findViewById(R.id.btn_stoplog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                       // ShellUtils.execCommand("adb kill-server ",true);
                        ShellUtils.execCommand("^C" ,true);
                        ShellUtils.execCommand("^C" ,true);
                        ShellUtils.execCommand("^C" ,true);
                        ShellUtils.execCommand("logcat -g" ,true);
                        File file = new File(filename);
                        if(file.exists()) {
                            file.setWritable(false);
                            file.setReadOnly();
                        }
                        FileUtils.copyFile(file.getPath(),filename2);
                    }
                }).start();

            }
        });
        findViewById(R.id.btn_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = getTextFileIntent(filename,false);
              startActivity(intent);

            }
        });

        findViewById(R.id.btn_pull).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               uploadLog();
            }
        });

    }
    /**
     * 重启应用
     */
    private void restartApplication() {
        Context context = getApplicationContext();
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis(), restartIntent);
        System.exit(0);
    }
    private void uploadLog() {
        final File file = new File(filename2);
        Log.d("duanyl", "uploadLog: 1");
        if(file.exists() && !getToken().equals("")){
            file.setWritable(false);
            file.setReadOnly();
            Log.d("duanyl", "uploadLog: 2");
            OkHttpClient okHttpClient = new OkHttpClient();
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("access_token", getToken());
            builder.addFormDataPart("device_id", getDeviceID());
            builder.addFormDataPart("file", file.getName(),RequestBody.create(MediaType.parse("application/octet-stream"),file));
            //创建RequestBody
            RequestBody body = builder.build();
            //创建Request
            final Request request = new Request.Builder().url("https://api.53iq.com/2/device_log")
                    .addHeader("User-Agent","android")
                    .header("Content-Type","text/html; charset=utf-8;")
                    .post(body).build();
            //单独设置参数 比如读取超时时间
            final Call call = okHttpClient.newBuilder().writeTimeout(1000, TimeUnit.SECONDS).build().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(filename +"\n"+"fail :"+e.toString());
                        }
                    });
                   ///file.delete();
                    Log.e(TAG,  "fail :"+e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String string = response.body().string();
                        Log.e(TAG, "response ----->" + string);
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(filename +"\n"+""+string);
                            }
                        });
                    } else {
                        Log.e(TAG, "fail");
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(filename +"\n"+"fail :");
                            }
                        });
                    }
                }
            });
        }
    }


    //Android获取一个用于打开文本文件的intent
    public  Intent getTextFileIntent(String param, boolean paramBoolean){
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean){
            Uri uri1 = Uri.parse(param );
            intent.setDataAndType(uri1, "text/plain");
        }else{
            Uri uri2 = Uri.fromFile(new File(param ));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }
    private SharedPreferences sharedPrefs;

    public void setToken(String key ) {

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("TOKEN", key);
        editor.apply();
    }

    public String getToken(){
        return sharedPrefs.getString("TOKEN","");
    }

    public void setDeviceId(String key ) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("DeviceID", key);
        editor.apply();
    }

    public String getDeviceID(){
        return sharedPrefs.getString("DeviceID","");
    }
}
