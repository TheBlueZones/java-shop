package com.example.shop.untils;

import com.example.shop.commom.Constant;
import org.apache.tomcat.util.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    public static String getMDstr(String strValue) throws NoSuchAlgorithmException {
        MessageDigest md5=MessageDigest.getInstance("MD5");
       return Base64.encodeBase64String(md5.digest((strValue+ Constant.SALT).getBytes()));
       /*tomacat version base64*/
    }

    public static void main(String[] args) {
        String md5=null;
        try {
            md5 =getMDstr("joker0218");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        System.out.println(md5);
    }
}
