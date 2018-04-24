package com.ebanswers.cleantool.tools;

import android.content.Context;
import android.util.Log;

import com.ebanswers.cleantool.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RootUtil {
    /**
     * 第一次获取root权限后，push 到 /system/bin/ 目录下的 二进制可执行程序 的名称
     */
    public static final String ROOT_SU = "su";
    /**
     * 调用su获取root权限再把zlsu写到system/bin目录下
     * 以便永久获取root权限（一旦获取成功，下次不再调用su）
     */
    public static void preparezlsu(Context ctx) {
        try {
            File zlsu = new File("/system/bin/" + ROOT_SU);

            InputStream suStream = ctx.getResources().openRawResource(R.raw.su);
            /**
             * 如果zlsu存在，则和raw目录下的zlsu比较大小，大小相同则不替换
             */
            if (zlsu.exists()) {
                if (zlsu.length() == suStream.available()) {
                    suStream.close();
                    return;
                }
            }
            /**
             * 先把zlsu 写到/data/data/com.zl.movepkgdemo中 然后再调用 su 权限 写到
             * /system/bin目录下
             */
            byte[] bytes = new byte[suStream.available()];
            DataInputStream dis = new DataInputStream(suStream);
            dis.readFully(bytes);
            String pkgPath = ctx.getApplicationContext().getPackageName();
            // "/data/data/com.zl.movepkgdemo/zlsu"
            String zlsuPath = "/data/data/" + pkgPath + File.separator +ROOT_SU;
            File zlsuFileInData = new File(zlsuPath);
            if (!zlsuFileInData.exists()) {
                System.out.println(zlsuPath + " not exist! ");
                try {
                    System.out.println("creating " + zlsuPath + "......");
                    zlsuFileInData.createNewFile();
                } catch (IOException e) {
                    System.out.println("create " + zlsuPath + " failed ! ");
                }
                System.out.println("create " + zlsuPath + " successfully ! ");
            }
            FileOutputStream suOutStream = new FileOutputStream(zlsuPath);
            suOutStream.write(bytes);
            suOutStream.close();

            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("mount -oremount,rw /dev/block/mtdblock3 /system\n");
            //			"busybox cp /data/data/com.zl.movepkgdemo/zlsu /system/bin/zlsu \n"
            os.writeBytes("busybox cp " + zlsuPath + " /system/bin/" +ROOT_SU + "\n");
            //			"busybox chown 0:0 /system/bin/zlsu \n"
            os.writeBytes("busybox chown 0:0 /system/bin/" + ROOT_SU + "\n");
            //			"chmod 4755 /system/bin/zlsu \n"
            os.writeBytes("chmod 4755 /system/bin/" +ROOT_SU + "\n");
            os.writeBytes("exit\n");
            os.flush();
            Log.d("ROOT","root success");
        } catch (Exception e) {
            Log.d("ROOT","root error");
            System.out.println("RootUtil preparezlsu: error");
            e.printStackTrace();
        }
    }
}
