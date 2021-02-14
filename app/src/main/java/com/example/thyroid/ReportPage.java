package com.example.thyroid;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.FileNotFoundException;


public class ReportPage extends AppCompatActivity {

    private final int FROM_ALBUM = 1;//表示从相册获取照片
    private final int FROM_CAMERA = 2;//表示从相机获取照片
    ImageView imageView;
    Button takePhoto;
    Button album;
    Button upload;
    boolean hasPhoto = false;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_page);

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
                Intent intent = new Intent(ReportPage.this,EditReportPage.class);
                intent.putExtra("bitmap",bitmap);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == FROM_ALBUM  &&  resultCode == Activity.RESULT_OK  &&  data != null){

            Uri imageUri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(imageUri));
                //int res = FaceClassified.runClassified(bitmap);
                imageView.setImageBitmap(bitmap);
                hasPhoto = true;
            }catch (FileNotFoundException e){
                Log.e("Exception", e.getMessage(), e);

            }
        }

        //从相机返回
        if(requestCode == FROM_CAMERA  &&  resultCode == Activity.RESULT_OK  &&  data != null){
            bitmap = (Bitmap) data.getExtras().get("data");
            //int res = FaceClassified.runClassified(photo);
            imageView.setImageBitmap(bitmap);
            hasPhoto = true;
        }

        super.onActivityResult(requestCode, resultCode, data);
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

}