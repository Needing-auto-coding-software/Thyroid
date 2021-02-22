package com.example.thyroid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewReportPage extends AppCompatActivity {

    TextView reportText;
    ImageView imageView;

    String imagePath;
    String userName;

    int caseid;
    String cropImgPath;
    String detectionsPath;
    String reportPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report_page);

        reportText = (TextView) findViewById(R.id.ResultText_ViewReportPage);
        imageView = (ImageView) findViewById(R.id.Image_ViewReportPage);

        Intent from_ReportPage = getIntent();
        imagePath = from_ReportPage.getStringExtra("imagePath");
        userName = from_ReportPage.getStringExtra("username");
        caseid = from_ReportPage.getIntExtra("id",caseid);
        cropImgPath = from_ReportPage.getStringExtra("cropImgPath");
        detectionsPath = from_ReportPage.getStringExtra("detectionsPath");
        reportPath = from_ReportPage.getStringExtra("reportPath");

        Log.d("cropImgPath",cropImgPath);
        Log.d("detectionsPath",detectionsPath);
        Log.d("reportPath",reportPath);

        //download_by_path(detectionsPath);
        download_by_path(reportPath,1);
        download_by_path(cropImgPath,0);


        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("查看报告");
    }

    //type=0为图片文件，type=1为文本文件
    private void download_by_path(String path,int type){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //初始化OkHttpClient对象
                    OkHttpClient client = new OkHttpClient();

                    //构建params类型请求体
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("path",path);

                    //构建请求
                    Request request = new Request.Builder()
                            .url("http://192.168.31.226:8080/file/downloadbypath")
                            .post(params.build())
                            .build();

                    //返回数据
                    Response response= client.newCall(request).execute();


                    if(type == 1){
                        //responseData储存文本
                        String responseData = response.body().string();
                        Log.d("下载报告",responseData);

                        show_text(responseData);
                    }else if(type == 0){
                        //inputStream存储图片输入流
                        InputStream inputStream = response.body().byteStream();
                        Log.d("下载图片",inputStream.toString());

                        show_image(inputStream);
                    }


                }catch(Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ViewReportPage.this,"网络错误，报告下载失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void show_text(String text){
        String target = "Model loaded.";
        String str1 = text.substring(0,text.indexOf(target));
        String str2 = text.substring(str1.length()+target.length(), text.length());

        //只显示“Model loaded.”后的文本
        reportText.setText(str2);
    }

    /*
    private void download_by_caseid(int type){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //初始化OkHttpClient对象
                    OkHttpClient client = new OkHttpClient();

                    //构建params类型请求体
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("caseid","" + caseid)
                          .add("type","" + type);

                    //构建请求
                    Request request = new Request.Builder()
                            .url("http://192.168.31.226:8080/file/downloadbycaseid")
                            .post(params.build())
                            .build();

                    //返回数据
                    Response response= client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("下载报告",responseData);


                }catch(Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ViewReportPage.this,"网络错误，报告下载失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
     */

    private void show_image(InputStream inputStream){
        try {
            byte[] data=readStream(inputStream);
            if(data!=null){
                final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                imageView.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //inputStream.close();
    }

    public static byte[] readStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1){
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }


}
