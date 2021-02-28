package com.example.thyroid;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ReportPage extends AppCompatActivity {

    private final int FROM_ALBUM = 1;//表示从相册获取照片
    private final int FROM_CAMERA = 2;//表示从相机获取照片
    ImageView imageView;
    Button takePhoto;
    Button album;
    Button upload;
    boolean hasPhoto = false;

    Bitmap bitmap;

    File outputImage;
    Uri imageUri; //图像路径

    String userName;

    int identity;

    int caseid;
    String cropImgPath;
    String detectionsPath;
    String reportPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_page);

        Intent from_FunctionalPage = getIntent();
        userName = from_FunctionalPage.getStringExtra("userName");
        identity = from_FunctionalPage.getIntExtra("identity",identity);

        applyWritePermission();//请求权限

        takePhoto = findViewById(R.id.TakePhotoButton_ReportPage);
        album = findViewById(R.id.Album_ReportPage);
        upload = findViewById(R.id.UploadButton_ReportPage);
        imageView = findViewById(R.id.Image_ReportPage);

        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("上传照片");

        album.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, FROM_ALBUM);
        });

        takePhoto.setOnClickListener(v->{

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, FROM_CAMERA);

        });

        upload.setOnClickListener(v->{
            if(!hasPhoto){
                AlertDialog();
            }else{
                connect_upload();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FROM_ALBUM && resultCode == Activity.RESULT_OK && data != null) {
            if (resultCode == RESULT_OK) {
                try{
                    Uri imageUri = data.getData();
                    ContentResolver cr = this.getContentResolver();
                    bitmap = BitmapFactory.decodeStream(cr.openInputStream(imageUri));
                    //int res = FaceClassified.runClassified(bitmap);
                    imageView.setImageBitmap(bitmap);
                    hasPhoto = true;

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("tag",e.getMessage());
                    Toast.makeText(this,"程序崩溃",Toast.LENGTH_SHORT).show();
                }}


                String path = null;
                //判断手机系统版本号
                if (Build.VERSION.SDK_INT >= 19) {
                    //4.4及以上系统用
                    path = handleImageOnKitKat(data);
                } else {
                    //4.4以下系统使用
                    path = handleImageBeforeKitKat(data);
                }

                Log.d("相册图片路径：",path);
                outputImage = new File(path);
            }



        //从相机返回
        if (requestCode == FROM_CAMERA && resultCode == Activity.RESULT_OK && data != null) {
            try {
                bitmap = (Bitmap) data.getExtras().get("data");
                //int res = FaceClassified.runClassified(photo);
                imageView.setImageBitmap(bitmap);
                hasPhoto = true;

                //将拍照获得的bitmap转换成图片文件并保存到本地
                //outputImage = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");

                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                outputImage = new File(directory, System.currentTimeMillis() + ".jpg");
                //FileOutputStream fileOutStream = new FileOutputStream(outputImage);

                Log.d("拍照获得的图片路径为：",outputImage.toString());
                FileOutputStream fileOutStream = new FileOutputStream(outputImage);
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutStream);
                    fileOutStream.flush();
                    fileOutStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("tag", e.getMessage());
                Toast.makeText(this, "程序崩溃", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private String handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }
            else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }
        else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri,null);
        }
        else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        return imagePath;
    }


    private String handleImageBeforeKitKat(@NotNull Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        return imagePath;
    }


    private String getImagePath(Uri uri,String selection){
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    private void applyWritePermission() {

        String permissions1 = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String permissions2 = Manifest.permission.READ_EXTERNAL_STORAGE;
        String permissions3 = Manifest.permission.CAMERA;

        if (Build.VERSION.SDK_INT >= 23) {
            int check1 = ContextCompat.checkSelfPermission(this, permissions1);
            if (check1 != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            int check2 = ContextCompat.checkSelfPermission(this, permissions2);
            if (check2 != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            int check3 = ContextCompat.checkSelfPermission(this, permissions3);
            if (check3 != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
    }

    private void AlertDialog()
    {
        //Alert Dialog
        new AlertDialog.Builder(ReportPage.this)
                .setTitle("提示")
                .setMessage("请先上传或拍摄照片")
                .setNegativeButton("Close", (dialog, which) -> {
                    //do nothing - it will close on its own
                })
                .show();
    }


    private void connect_upload(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();

                    RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"),outputImage);

                    MultipartBody.Builder requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file",outputImage.getName(),fileBody)
                            .addFormDataPart("username",userName);


                    Request request = new Request.Builder()
                            .url("http://10.136.189.11:8080/file/upload")
                            .post(requestBody.build())
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();

                    Log.d("图像上传response:",responseData);

                    JSONObject jsonObject = new JSONObject(responseData);

                    String status = jsonObject.getString("status");

                    if(status.equals("fail")){
                        JSONObject data = jsonObject.getJSONObject("data");
                        String errCode = data.getString("errCode");
                        String errMsg = data.getString("errMsg");
                        Log.d("status",status);
                        Log.d("errCode",errCode);
                        Log.d("errMsg",errMsg);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ReportPage.this,"图像上传失败:" + errMsg ,Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    else if(status.equals("success")){
                        String data = jsonObject.getString("data");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ReportPage.this,"图像上传成功",Toast.LENGTH_SHORT).show();
                                Toast.makeText(ReportPage.this,"报告生成中，请您耐心等待",Toast.LENGTH_SHORT).show();

                                get_report();//生成报告
                                //大概要等一分钟左右

                            }
                        });
                    }


                }catch(Exception e){
                    e.printStackTrace();
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           Toast.makeText(ReportPage.this,"图像上传失败",Toast.LENGTH_SHORT).show();
                       }
                   });
                }
            }
        }).start();
    }

    //生成报告
    private void get_report(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();

                    RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"),outputImage);

                    MultipartBody.Builder requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("username",userName)
                            .addFormDataPart("file",outputImage.getName(),fileBody);


                    Request request = new Request.Builder()
                            .url("http://10.136.189.11:8080/case/addcase")
                            .post(requestBody.build())
                            .build();


                    Response response = client.newBuilder().readTimeout(120,TimeUnit.SECONDS).build().newCall(request).execute();
                    String responseData = response.body().string();

                    Log.d("报告生成response:",responseData);

                    JSONObject jsonObject = new JSONObject(responseData);

                    String status = jsonObject.getString("status");

                    if(status.equals("fail")){
                        JSONObject data = jsonObject.getJSONObject("data");
                        String errCode = data.getString("errCode");
                        String errMsg = data.getString("errMsg");
                        Log.d("status",status);
                        Log.d("errCode",errCode);
                        Log.d("errMsg",errMsg);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ReportPage.this,"报告生成失败:" + errMsg ,Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    else if(status.equals("success")){
                        JSONObject data = jsonObject.getJSONObject("data");
                        caseid = data.getInt("id");
                        cropImgPath = data.getString("cropImgPath");
                        detectionsPath = data.getString("detectionsPath");
                        reportPath = data.getString("reportPath");

                        Log.d("结节位置图像cropImgPath",cropImgPath);
                        Log.d("结节识别结果图像detectionsPath",detectionsPath);
                        Log.d("病历文本reportPath",reportPath);

                        //图像上传且报告生成成功后，患者自动跳转到ViewReportPage，医生自动跳转到EditReportPage

                        if(identity == 1){

                            Intent jump_to_EditReportPage = new Intent(ReportPage.this,EditReportPage.class);
                            jump_to_EditReportPage.putExtra("username",userName);
                            jump_to_EditReportPage.putExtra("id",caseid);
                            jump_to_EditReportPage.putExtra("cropImgPath",cropImgPath);
                            jump_to_EditReportPage.putExtra("detectionsPath",detectionsPath);
                            jump_to_EditReportPage.putExtra("reportPath",reportPath);

                            startActivity(jump_to_EditReportPage);

                        }
                        else if(identity == 2){

                            Intent jump_to_ViewReportPage = new Intent(ReportPage.this,ViewReportPage.class);
                            jump_to_ViewReportPage.putExtra("username",userName);
                            jump_to_ViewReportPage.putExtra("id",caseid);
                            jump_to_ViewReportPage.putExtra("cropImgPath",cropImgPath);
                            jump_to_ViewReportPage.putExtra("detectionsPath",detectionsPath);
                            jump_to_ViewReportPage.putExtra("reportPath",reportPath);

                            startActivity(jump_to_ViewReportPage);

                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ReportPage.this,"报告已生成",Toast.LENGTH_SHORT).show();

                            }
                        });
                    }


                }catch(Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ReportPage.this,"报告生成失败：网络错误",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}
