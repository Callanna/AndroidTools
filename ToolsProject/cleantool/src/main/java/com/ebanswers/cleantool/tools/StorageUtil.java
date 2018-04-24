/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ebanswers.cleantool.tools;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.ebanswers.cleantool.data.SDCardInfo;
import com.ebanswers.cleantool.data.StorageSize;

import java.io.File;
import java.io.FileInputStream;


// TODO: Auto-generated Javadoc

public class StorageUtil {

    // storage, G M K B
    public static String convertStorage(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

    public static StorageSize convertStorageSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        StorageSize sto = new StorageSize();
        if (size >= gb) {

            sto.suffix = "GB";
            sto.value = (float) size / gb;
            return sto;
        } else if (size >= mb) {

            sto.suffix = "MB";
            sto.value = (float) size / mb;

            return sto;
        } else if (size >= kb) {


            sto.suffix = "KB";
            sto.value = (float) size / kb;

            return sto;
        } else {
            sto.suffix = "B";
            sto.value = (float) size;

            return sto;
        }


    }

    public static SDCardInfo getSDCardInfo() {
        // String sDcString = Environment.getExternalStorageState();

        if (Environment.isExternalStorageRemovable()) {
            String sDcString = Environment.getExternalStorageState();
            if (sDcString.equals(Environment.MEDIA_MOUNTED)) {
                File pathFile = Environment
                        .getExternalStorageDirectory();

                try {
                    StatFs statfs = new StatFs(
                            pathFile.getPath());

                    // 获取SDCard上BLOCK总数
                    long nTotalBlocks = statfs.getBlockCount();

                    // 获取SDCard上每个block的SIZE
                    long nBlocSize = statfs.getBlockSize();

                    // 获取可供程序使用的Block的数量
                    long nAvailaBlock = statfs.getAvailableBlocks();

                    // 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)
                    long nFreeBlock = statfs.getFreeBlocks();

                    SDCardInfo info = new SDCardInfo();
                    // 计算SDCard 总容量大小MB
                    info.total = nTotalBlocks * nBlocSize;

                    // 计算 SDCard 剩余大小MB
                    info.free = nAvailaBlock * nBlocSize;

                    return info;
                } catch (IllegalArgumentException e) {

                }
            }
        }
        return null;
    }

    public static SDCardInfo getSystemSpaceInfo(Context context) {
        File path = Environment.getDataDirectory();
        // File path = context.getCacheDir().getAbsoluteFile();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        long availableBlocks = stat.getAvailableBlocks();

        long totalSize = blockSize * totalBlocks;
        long availSize = availableBlocks * blockSize;
        SDCardInfo info = new SDCardInfo();
        info.total = totalSize;
        info.free = availSize;
        return info;


    }

    public static SDCardInfo getRootSpaceInfo() {
        File path = Environment.getRootDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        long availableBlocks = stat.getAvailableBlocks();

        long totalSize = blockSize * totalBlocks;
        long availSize = availableBlocks * blockSize;
        // 获取SDCard上每个block的SIZE
        long nBlocSize = stat.getBlockSize();

        SDCardInfo info = new SDCardInfo();
        // 计算SDCard 总容量大小MB
        info.total = totalSize;

        // 计算 SDCard 剩余大小MB
        info.free = availSize;
        return info;

    }
    /**
     * 获得文件大小
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + getFileSize(fileList[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 获取指定文件大小
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception
    {
        long size = 0;
        if (file.exists()){
            FileInputStream fis =   new FileInputStream(file);
            size = fis.available();
        }
        return size;
    }
    public static long getCacheDataSize(){
        File file = new File(SDCardUtils.getRootPath().getAbsolutePath()+"/Android/data");
        return file.getTotalSpace();
    }

    /**
     * 删除单个文件
     * @param   filePath    被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        Log.d("duanyl", "deleteFile: 1");
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            Log.d("duanyl", "deleteFile: 2");
            return file.delete();
        }
        Log.d("duanyl", "deleteFile: 3");
        return false;
    }

    /**
     * 删除文件夹以及目录下的文件
     * @param   filePath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public static  boolean deleteDirectory(String filePath) {
        boolean flag = false;
        Log.d("duanyl", "deleteDirectory: 1");
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        Log.d("duanyl", "deleteDirectory: 2");
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                Log.d("duanyl", "deleteDirectory: 3");
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                Log.d("duanyl", "deleteDirectory: 4");
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        Log.d("duanyl", "deleteDirectory: 5");
        if (!flag) return false;
        //删除当前空目录
        Log.d("duanyl", "deleteDirectory: 6");
        return dirFile.delete();
    }

    /**
     *  根据路径删除指定的目录或文件，无论存在与否
     *@param filePath  要删除的目录或文件
     *@return 删除成功返回 true，否则返回 false。
     */
    public static boolean deleteFolder(String filePath) {
        File file = new File(filePath);
        Log.d("duanyl", "deleteFolder: 1");
        if (!file.exists()) {
            Log.d("duanyl", "deleteFolder: 2");
            return false;
        } else {
            if (file.isFile()) {
                Log.d("duanyl", "deleteFolder:3");
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                Log.d("duanyl", "deleteFolder: 4");
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }
}
