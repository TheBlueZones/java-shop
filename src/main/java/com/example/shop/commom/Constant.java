package com.example.shop.commom;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Constant {
    public static final String IMOOC_MALL_USER = "imooc_mall_user";
    public static final String SALT = "svklmsda&&][]";
    public static  String FILE_UOLOAD_DIR;
    /*final must been initialization*/

    @Value("${file.upload.dir}")
    /*用set方法给变量赋值*/
    public void setFileUploadDir(String fileUploadDir) {
        FILE_UOLOAD_DIR = fileUploadDir;
    }

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC= Sets.newHashSet("price desc","price asc");
    }

    public interface SaleStatus{
        int NOT_SALE=0;/*Product unavailablity*/
        int SALE=1;/*Product availablity*/
    }
    public interface Cart{
        int UN_SELECTED=0;/*cart not selected*/
        int SELECTED=1;/*cart selected*/
    }
}
