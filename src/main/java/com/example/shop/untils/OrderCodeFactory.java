package com.example.shop.untils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class OrderCodeFactory {
    private static String getDateTime(){
        DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());/*可以有参数,如果没有参数获取的是当前的时间对象,*/
    }

    private static int getRandom(Long n){
        Random random =new Random();
        /*获取5位随机数*/
        return (int) (random.nextDouble()*(90000))+10000;
    }
    public static String getOrderCode(Long userId){
        return getDateTime()+getRandom(userId);
    }
}
