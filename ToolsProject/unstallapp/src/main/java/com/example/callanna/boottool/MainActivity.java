package com.example.callanna.boottool;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_otapath,et_apkpath,et_apkname;
    private Button btn_boot,btn_deleteapk,btn_uninstallapk;
    private RadioButton rb_local,rb_sd;
    private String rootPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_otapath = findViewById(R.id.et_otapath);
        et_apkpath = findViewById(R.id.et_sysapkpath);
        et_apkname = findViewById(R.id.et_apk);

        btn_boot = findViewById(R.id.btn_boot);
        btn_deleteapk = findViewById(R.id.btn_uninstall);
        btn_uninstallapk = findViewById(R.id.btn_uninstallapk);

        rb_local = findViewById(R.id.rb_local);
        rb_sd = findViewById(R.id.rb_sd);

        ((RadioGroup)findViewById(R.id.rg_root)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
               setRootPath();
            }
        });

        btn_boot.setOnClickListener(this);
        btn_deleteapk.setOnClickListener(this);
        btn_uninstallapk.setOnClickListener(this);

        if(!APKUtil.checkAppInstalledByPackageName(getBaseContext(),"com.softwinner.autoupdate")){
            copyAutoUpdateFileToSd();
        }
        setRootPath();
    }

    public void setRootPath(){
        if(rb_local.isChecked()){
            rootPath = FileUtils.getRootPath();
        }else{
            rootPath = FileUtils.getUsbRootPath();
        }
        et_otapath.setText(rootPath);
    }
    String text = "";
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_boot:
                text = et_otapath.getText().toString();
                if(TextUtils.isEmpty(text)){
                    Toast.makeText(getBaseContext(),"文件地址不能为空！",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!text.endsWith(".zip")){
                    Toast.makeText(getBaseContext(),"文件限制为zip文件",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (FileUtils.isFileExist(text) && APKUtil.checkAppInstalledByPackageName(getBaseContext(),"com.softwinner.autoupdate")) {
                    Intent mIntent = new Intent("softwinner.intent.action.autoupdate");
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    mIntent.putExtra("file", text);
                    startActivity(mIntent);
                }

                break;
            case R.id.btn_uninstall:
                text = et_apkpath.getText().toString();
                if(TextUtils.isEmpty(text)){
                    Toast.makeText(getBaseContext(),"文件名称不能为空！",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!text.endsWith(".apk")){
                    Toast.makeText(getBaseContext(),"文件限制为apk文件",Toast.LENGTH_SHORT).show();
                    return;
                }
                text = "/system/app/"+text;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ShellUtils.execCommand("mount -o rw,remount /system ",true);
                        //ShellUtils.execCommand("remount ",true);
                        ShellUtils.execCommand("rm "+text,true);
                    }
                }).start();

                //FileUtils.deleteApkFile(text);
                break;
            case R.id.btn_uninstallapk:
                text = et_apkname.getText().toString();
                if(TextUtils.isEmpty(text)){
                    Toast.makeText(getBaseContext(),"包名不能为空！",Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ShellUtils.execCommand("mount -o rw,remount ",true);
                        ShellUtils.execCommand("pm uninstall "+text,true);
                        //ShellUtils.execCommand("reboot",true);
                    }
                }).start();
                //APKUtil.uninstallApkByPackage(getBaseContext(),text);
                //ShellUtils.execCommand(" uninstall "+text,false);

                break;
        }
    }

    private  void copyAutoUpdateFileToSd() {
        String filePath = FileUtils.getRootPath() + "/" + "AutoUpdate.apk";
        if(!FileUtils.isFileExist(filePath)) {
            InputStream myInput = null;
            OutputStream myOutput = null;
            try {
                myOutput = new FileOutputStream(filePath);
                myInput = getApplication().getAssets().open("AutoUpdate.apk");
                byte[] buffer = new byte[1024];
                int length = myInput.read(buffer);
                while (length != 0) {
                    if (length > 0) {
                        myOutput.write(buffer, 0, length);
                        length = myInput.read(buffer);
                    } else if (length <= 0) {
                        APKUtil.installApkByPath(getApplication(), filePath);
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(myOutput != null) {
                        myOutput.flush();
                        myOutput.close();
                    }
                    if(myInput != null){
                        myInput.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        boolean falg ;
        Log.d("duanyl", "Ac dispatchTouchEvent: " +ev.getAction());
//        falg = ;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.d("duanyl", "Ac onTouchEvent: "+event.getAction());

        return super.onTouchEvent(event);
    }
}
