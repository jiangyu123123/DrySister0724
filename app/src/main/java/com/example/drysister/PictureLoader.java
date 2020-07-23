package com.example.drysister;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PictureLoader {
    private String imgUrl;
    private byte[] picByte;
    private ImageView loadImg;
//线程将输入流转换为字节流数组
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            try {
                URL url=new URL(imgUrl);
                HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                if(conn.getResponseCode()==200){
                    InputStream in=conn.getInputStream();
                    ByteArrayOutputStream out=new ByteArrayOutputStream();
                    byte[] bytes=new byte[1024];
                    int length=-1;
                    while((length=in.read(bytes))!=-1){
                        out.write(bytes,0,length);
                    }
                    picByte=out.toByteArray();
                    in.close();
                    out.close();
                    handler.sendEmptyMessage(0x123);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


    public void load(ImageView loadImg, String imgUrl) {
        this.loadImg = loadImg;
        this.imgUrl = imgUrl;
        Drawable drawable = loadImg.getDrawable();
        if(drawable != null && drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            if(bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        new Thread(runnable).start();
    }
//Handler将字节流数组转化为Bitmap
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0x123){
                if(picByte!=null){
                    //将网络图片字节流读到内存中
                    Bitmap bitmap=BitmapFactory.decodeByteArray(picByte,0,picByte.length);
                    loadImg.setImageBitmap(bitmap);
                }
            }
        }
    };
}

