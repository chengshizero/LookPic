package com.example.administrator.lookpic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText et_path;
    private ImageView iv;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
           Bitmap bitmap = (Bitmap) msg.obj;
           iv.setImageBitmap(bitmap);

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }
    private void initView(){
        et_path = (EditText)findViewById(R.id.et_path);
        iv = (ImageView)findViewById(R.id.iv);
    }
    public void click(View v){
       new Thread(){
           @Override
           public void run() {
               try {
                   File file = new File(getCacheDir(),"test.jpg");
                   if(file.exists() && file.length()>0){
                       //使用緩存
                       System.out.println("使用緩存");
                       Bitmap cachebitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                       Message msg = Message.obtain();
                       msg.obj = cachebitmap;
                       handler.sendMessage(msg);

                   }else {
                       //first
                       System.out.println("first");

                   String path = et_path.getText().toString().trim();
                   URL url = new URL(path);
                   HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                   conn.setRequestMethod("GET");
                   conn.setConnectTimeout(5000);
                   int code = conn.getResponseCode();
                   if(code==200) {
                       InputStream in = conn.getInputStream();
                       //緩存

                       FileOutputStream fos = new FileOutputStream(file);
                       int len = -1;
                       byte[] bufffer = new byte[1024];
                       while ((len = in.read(bufffer)) != -1) {
                           fos.write(bufffer, 0, len);
                       }
                       fos.close();
                       //關流  如直接餵圖 下方in接不到 ;上方改成file緩存 下方接file路徑
                       in.close();
//                      Bitmap bitmap = BitmapFactory.decodeStream(in);
                       Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                       Message msg = Message.obtain();
                       msg.obj = bitmap;
                       handler.sendMessage(msg);
                   }
                   }
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
       }.start();

    }
}
