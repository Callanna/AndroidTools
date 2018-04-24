/**********************************************************************
 * AUTHOR：YOLANDA
 * DATE：2015年2月27日上午10:04:32
 * Copyright © 56iq. All Rights Reserved
 *======================================================================
 * EDIT HISTORY
 *----------------------------------------------------------------------
 * |  DATE      | NAME       | REASON       | CHANGE REQ.
 *----------------------------------------------------------------------
 * | 2015年2月27日    | YOLANDA    | Created      |
 *
 * DESCRIPTION：create the File, and add the content.
 *
 ***********************************************************************/
package com.callanna.frame.utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * @author YOLANDA
 * @Time 2015年2月27日 上午10:04:32
 */
public class FileUtil {
	
	/**
	 * 得到SD卡根目录
	 * @author YOLANDA
	 * @return
	 */
	public static File getRootPath(){
		File path = null;
		if (FileUtil.sdCardIsAvailable()) {
			path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
		} else {
			path = Environment.getDataDirectory();
		}
		return path;
	}
	
	/**
	 * SD卡是否可用
	 * @author YOLANDA
	 * @return
	 */
	public static boolean sdCardIsAvailable() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File sd = new File(Environment.getExternalStorageDirectory().getPath());
			if (sd.canWrite())
				return true;
			else
				return false;
		} else
			return false;
	}
	
	/**
	 * 文件或者文件夹是否存在
	 * @author YOLANDA
	 * @param filePath
	 * @return
	 */
	public static boolean exists(String filePath){
		File file = new File(filePath);
		return file.exists();
	}

	/**
	 * 删除某个文件
	 * @param path 文件路径
	 */
	public static void delFile(String path) {
		File f = new File(path);
		if (f.exists()) {
			f.delete();
		}
	}

	/**
	 * 删除文件夹和里边的所有
	 * @author YOLANDA
	 * @param folderPath
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			File f = new File(filePath);
			f.delete(); // 删除空文件夹
		} catch (NullPointerException e) {
		} catch (Exception e) {
		}
	}

	/**
	 * 删除指定文件夹下所有文件
	 * @author YOLANDA
	 * @param path
	 * @return
	 */
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile() && !temp.toString().contains("appconfig")) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 文件复制
	 * @author YOLANDA
	 * @param srcFile 原路径
	 * @param destFile 目标路径
	 * @return
	 */
	public static boolean copy(String srcFile, String destFile) {
		try {
			FileInputStream in = new FileInputStream(srcFile);
			FileOutputStream out = new FileOutputStream(destFile);
			byte[] bytes = new byte[1024];
			int c;
			while ((c = in.read(bytes)) != -1) {
				out.write(bytes, 0, c);
			}
			in.close();
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 复制整个文件夹内
	 * @author YOLANDA
	 * @param oldPath String 原文件路径如：c:/fqf
	 * @param newPath String 复制后路径如：f:/fqf/ff
	 * @return boolean
	 */
	public static void copyFolder(String oldPath, String newPath) {
		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (NullPointerException e){
		} catch (Exception e) {
		}
	}

	/**
	 * 重命名文件
	 * @author YOLANDA
	 * @param resFilePath
	 * @param newFilePath
	 * @return
	 */
	public static boolean renameFile(String resFilePath, String newFilePath) {
		File resFile = new File(resFilePath);
		File newFile = new File(newFilePath);
		return resFile.renameTo(newFile);
	}
	
	/**
	 * 获取磁盘可用空间
	 * @author YOLANDA
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static long getSDCardAvailaleSize() {
		File path = getRootPath();
		StatFs stat = new StatFs(path.getPath());
		long blockSize ,availableBlocks;
		if(Build.VERSION.SDK_INT > 17){
			blockSize = stat.getBlockSizeLong();
			availableBlocks = stat.getAvailableBlocksLong();
		}else{
			blockSize = stat.getBlockSize();
			availableBlocks = stat.getAvailableBlocks();
		}
		return availableBlocks * blockSize;
	}
	
	/**
	 * 获取某个目录可用大小
	 * @author YOLANDA
	 * @param path
	 * @return
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static long getDirSize(String path) {
		StatFs stat = new StatFs(path);
		long blockSize ,availableBlocks;
		if(Build.VERSION.SDK_INT > 17){
			blockSize = stat.getBlockSizeLong();
			availableBlocks = stat.getAvailableBlocksLong();
		}else{
			blockSize = stat.getBlockSize();
			availableBlocks = stat.getAvailableBlocks();
		}
		return availableBlocks * blockSize;
	}

	/**
	 * 获取文件夹大小 
	 * @author YOLANDA
	 * @param path
	 * @return
	 */
	public static long getFileAllSize(String path) {
		File file = new File(path);
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] children = file.listFiles();
				long size = 0;
				for (File f : children)
					size += getFileAllSize(f.getPath());
				return size;
			} else {
				long size = file.length();
				return size;
			}
		} else {
			return 0;
		}
	}
	
	/**
	 * 创建一个文件夹
	 * @author YOLANDA
	 * @param path
	 * @return
	 */
	public static boolean initDirctory(String path) {
		boolean result = false;
		File file = new File(path);
		if (!file.exists()) {
			result = file.mkdir();
		} else if (!file.isDirectory()) {
			file.delete();
			result = file.mkdir();
		}else if(file.exists()){
			result = true;
		}
		return result;
	}

	/**
	 * 复制文件
	 * @author YOLANDA
	 * @param from
	 * @param to
	 * @throws IOException
	 */
	public static void copyFile(File from, File to) throws IOException {
		if (!from.exists()) {
			throw new IOException("The source file not exist: " + from.getAbsolutePath());
		}
		FileInputStream fis = new FileInputStream(from);
		try {
			copyFile(fis, to);
		} finally {
			fis.close();
		}
	}

	/**
	 * 复制文件
	 * @author YOLANDA
	 * @param from
	 * @param to
	 * @return
	 * @throws IOException
	 */
	public static long copyFile(InputStream from, File to) throws IOException {
		long totalBytes = 0;
		FileOutputStream fos = new FileOutputStream(to);
		try {
			byte[] data = new byte[1024];
			int len;
			while ((len = from.read(data)) > -1) {
				fos.write(data, 0, len);
				totalBytes += len;
			}
			fos.flush();
		} finally {
			fos.close();
		}
		return totalBytes;
	}

	/**
	 * 用UTF8保存一个文件
	 * @author YOLANDA
	 * @param path 文件完成路径
	 * @param content 要写入的内容
	 * @throws Exception
	 */
	public static void saveFileUTF8(String path, String content, Boolean append) throws Exception {
		FileOutputStream fos = new FileOutputStream(path, append);
		Writer out = new OutputStreamWriter(fos, "UTF-8");
		out.write(content);
		out.flush();
		out.close();
		fos.flush();
		fos.close();
	}

	/**
	 * 用UTF8读取一个文件
	 * @author YOLANDA
	 * @param path 文件完整路径
	 * @return
	 */
	public static String getFileUTF8(String path) {
		String result = "";
		InputStream fin = null;
		try {
			fin = new FileInputStream(path);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			fin.close();
			result = new String(buffer,"UTF-8");
		} catch (FileNotFoundException e) {
		} catch (UnsupportedEncodingException e) {
		} catch (IOException e) {
		}
		return result;
	}
	
	/**
	 * 得到一个文件Intent
	 * @author YOLANDA
	 * @param path
	 * @param mimetype
	 * @return
	 */
	public static Intent getFileIntent(String path, String mimetype){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(path)), mimetype);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return intent;
	}

}
