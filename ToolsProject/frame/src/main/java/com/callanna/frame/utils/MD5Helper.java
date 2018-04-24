package com.callanna.frame.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Description  MD5工具类
 * Created by chenqiao on 2015/9/18.
 */
public class MD5Helper {

    private static MessageDigest md = null;

    static {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (byte aB : b) resultSb.append(byteToHexString(aB));
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * M5D加密
     *
     * @param origin      需要加密的字串
     * @param charsetname 编码格式
     * @return MD5校验码
     */
    public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = origin;
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes(charsetname)));
        } catch (Exception exception) {
            return null;
        }
        return resultString;
    }

    /**
     * MD5加密byte数组
     *
     * @param bytes byte数组数据
     * @return MD5校验码
     */
    public static String MD5Encode(byte[] bytes) {
        md.update(bytes);
        return byteArrayToHexString(md.digest());
    }

    /**
     * 获取文件的MD5值
     *
     * @param file 文件
     * @return 文件的MD5值，出错则为null
     */
    public static String getFileMD5String(File file) {
        try {
            InputStream fis = new FileInputStream(file);
            return getMD5StringFromInputStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从输入流获取MD5值
     *
     * @param is 输入流
     * @return 返回MD5值，出错则为null
     */
    public static String getMD5StringFromInputStream(InputStream is) {
        try {
            byte[] buffer = new byte[1024];
            int numRead;
            while ((numRead = is.read(buffer)) > 0) {
                md.update(buffer, 0, numRead);
            }
            is.close();
            return byteArrayToHexString(md.digest());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
}