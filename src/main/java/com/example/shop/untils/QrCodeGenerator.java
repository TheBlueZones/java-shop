package com.example.shop.untils;

import com.example.shop.commom.Constant;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QrCodeGenerator {
    public static void generateQRCodeImage(String text,int width,
                                           int height ,String filePath)
            throws WriterException , IOException{
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix=qrCodeWriter.encode(text,BarcodeFormat.QR_CODE,width,height);
        Path path= FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix,"PNG",path);
    }

    public static void main(String[] args) throws IOException, WriterException {
        generateQRCodeImage("hello word",350,350,
               "E:/prgraming/java engineer/QRTest.png");
        /*不能直接写 Constant.FILE_UOLOAD_DIR，这是借助spring的能力*/
    }
}
