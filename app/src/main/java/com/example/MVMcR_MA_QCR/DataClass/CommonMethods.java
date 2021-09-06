package com.example.MVMcR_MA_QCR.DataClass;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class CommonMethods {
    public static final int QR_CODE_LENGTH=36;
    // TODO: 11-08-2021  qr code logic to get varient
    public String getVarient(String qr_code){
        String varient="na";
        String[] a = qr_code.split("_");
        String v = a[1].charAt(4)+"";
        switch (v) {
            case "J":
                varient = "N4";
                break;
            case "K":
                varient="N8";
                break;
            case "L":
                varient="N10";
                break;
            default:
                varient="na";
                break;
        }
        if(varient.equals("na")){
            Log.e("v is ", v);
            v=a[1].charAt(5)+""+a[1].charAt(6)+"";
            Log.e("v is ", v);
            switch (v){
                case "SC":
                    varient="P108";
                    break;
                case "CC":
                    varient="BMT";
                    break;
                case "EC":
                    varient="P114";
                    break;
                case "AA":
                    varient="P113";
                    break;
            }
            v=a[1].charAt(11)+""+a[1].charAt(12)+"";
            Log.e("v is", v);
            switch (v) {
                case "MS":
                    varient += "_MS";
                    break;
                case "PS":
                    varient += "_PS";
                    break;
                case "PP":
                    varient += "_PP";
                    break;
            }
        }
        Log.e("v is", v);
        Log.e("varient",varient);
        return varient;
    }

    // TODO: 11-08-2021 qr code logic to get platform
    public String getPlatform(String qr_code){
        String[] a = qr_code.split("_");
        String v=a[1].charAt(4)+"";
        String platform = "na";
        if(v.equals("J")||v.equals("K")||v.equals("L")||v.equals("M"))
            platform="Bolero Neo";
        else{
            v=a[1].charAt(5)+""+a[1].charAt(6)+"";
            if(v.equals("SC")||v.equals("CC")||v.equals("EC")||v.equals("AA")){
                platform="Pickups";
            }
        }

        return platform;

    }

    // TODO: 09-08-2021 get bytes from input stream 
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    // TODO: 11-08-2021 bitmap to base 64(string) 
    public String bitmapToBase64(Bitmap bitmapImg){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapImg.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;

    }
    public Bitmap byteArrayToBitmap(byte[] byteimg){
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteimg, 0, byteimg.length);
        return bitmap;
    }

    // TODO: 11-08-2021 get next day 
    public Date getNextDay(Date date){
        Log.e("minus 24 hours",new Date(date.getTime()-24*60*60*1000).toString());
        return new Date(date.getTime()+24*60*60*1000);

    }
    public String byteArrayToBase64(byte[] byteArray){
        String decodedImage=Base64.encodeToString(byteArray,Base64.DEFAULT);
        return decodedImage;
    }

    // TODO: 06-09-2021 check qr code
    public boolean checkQR(String qr_code){
        if(qr_code.length()>=QR_CODE_LENGTH&&qr_code.contains("_")){
            if(getPlatform(qr_code).equals("na")){
                return false;
            }
            else return true;
        }
        else{
            return false;
        }
    }
}
