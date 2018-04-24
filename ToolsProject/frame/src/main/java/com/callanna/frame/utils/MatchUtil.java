package com.callanna.frame.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Callanna on 2016/7/12.
 */
public class MatchUtil {
    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        Pattern p = Pattern
                .compile("[1][358]\\d{9}");
        Matcher m = p.matcher(mobiles);
        System.out.println(m.matches() + "---");
        return m.matches();
    }
    /**
     * 匹配国内电话号码
     * */
    public static boolean isPhoneNumberValid(String phoneNumber)
    {
    /* 匹配形式如 0511-4405222 或 021-87888822*/
        String expression ="\\d{3}-\\d{8}|\\d{4}-\\d{7}";
    /*创建Pattern*/
        Pattern pattern = Pattern.compile(expression);
    /*将Pattern 以参数传入Matcher作Regular expression*/
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    /**
     *  验证邮箱
     * @param strEmail
     * @return
     */
   public static boolean isEmail(String strEmail){
       String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
       Pattern p = Pattern.compile(strPattern);
       Matcher m = p.matcher(strEmail);
       return m.matches();
   }

    /**
     *   ^\w+　　//匹配由数字、26个英文字母或者下划线组成的字符串
     */


   public static boolean isNumberAndLetter(String s){
       String strPattern = "^[A-Za-z0-9]+";
       Pattern p = Pattern.compile(strPattern);
       Matcher matcher = p.matcher(s);
       return matcher.matches();
   }

    /**
     *         ^\w+　　//匹配由数字、26个英文字母或者下划线组成的字符串
     */
    public static boolean isOKAccoutNumber(String s){
        String strPattern = "^\\w+";
        Pattern p = Pattern.compile(strPattern);
        Matcher matcher = p.matcher(s);
        return matcher.matches();
    }

    /**
     * 匹配中文字符
     */
    public static boolean isChineseWord(String ch){
        String strPattern = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(strPattern);
        Matcher matcher = p.matcher(ch);
        return matcher.matches();
    }

    /**
     * 匹配身份证
     */
    public static boolean isIDCardNumber(String id){
        String strPattern = "\\d{15}|\\d{18}";//中国的身份证为15位或18位
        Pattern p = Pattern.compile(strPattern);
        Matcher matcher = p.matcher(id);
        return matcher.matches();
    }

    /**
     *   验证URL
     */
    public static boolean isURL(String url){
        String strPattern = "[a-zA-z]+://[^\\s]*";
        Pattern p = Pattern.compile(strPattern);
        Matcher matcher = p.matcher(url);
        return matcher.matches();
    }

    /**
     *  验证QQ号
     */
    public static boolean isQQNumber(String qq){
        String strPattern = "[1-9][0-9]{4,}";//腾讯QQ号从10000开始
        Pattern p = Pattern.compile(strPattern);
        Matcher matcher = p.matcher(qq);
        return matcher.matches();
    }

    /**
     *   验证邮政编码
     */
    public static boolean isPostalCodes(String code){
        String strPattern = "[1-9]\\d{5}(?!\\d)";//中国邮政编码为6位数字
        Pattern p = Pattern.compile(strPattern);
        Matcher matcher = p.matcher(code);
        return matcher.matches();
    }

    /**
     * 验证IP地址
     */
    public static boolean isIPAddress(String ip){
        String strPattern = "\\d+\\.\\d+\\.\\d+\\.\\d+";//中国邮政编码为6位数字
        Pattern p = Pattern.compile(strPattern);
        Matcher matcher = p.matcher(ip);
        return matcher.matches();
    }
}
