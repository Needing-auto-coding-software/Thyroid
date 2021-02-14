package com.example.thyroid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class EditReportPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_report_page);
        ImageView imageView = findViewById(R.id.Image_EditReportPage);

        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("查看结果");

        Intent static_intent = getIntent();
        if (static_intent!=null){
            //转为bitmap
            Bitmap bitmapReceive = static_intent.getParcelableExtra("bitmap");
            imageView.setImageBitmap(bitmapReceive);
        }
    }

}