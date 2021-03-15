package com.example.thyroid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class EditRealname extends AppCompatActivity {

    private EditText edit_realname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle("修改真实姓名");

        edit_realname = (EditText) findViewById(R.id.et_edit);
    }
}