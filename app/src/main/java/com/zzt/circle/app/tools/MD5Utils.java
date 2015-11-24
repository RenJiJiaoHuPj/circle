package com.zzt.circle.app.tools;

import java.security.MessageDigest;

/**
 * Created by zzt on 15-6-13.
 */
public class MD5Utils {
    public static String str2MD5(String str) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = str.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char md5[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                md5[k++] = hexDigits[byte0 >>> 4 & 0xf];
                md5[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(md5);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
