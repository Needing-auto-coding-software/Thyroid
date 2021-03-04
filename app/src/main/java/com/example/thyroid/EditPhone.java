package com.example.thyroid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.security.identity.EphemeralPublicKeyNotFoundException;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Pattern;

public class EditPhone extends AppCompatActivity {


    private EditText edit_phone;
    private Button confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        confirm = findViewById(R.id.confirm);
        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle("修改手机号码");
        String regexPhone = "[1][3,5,7,8][0-9]\\d{8}";
        Pattern patternPhone = Pattern.compile(regexPhone);

        edit_phone = (EditText) findViewById(R.id.et_edit);
        String phone  = edit_phone.getText().toString();
        confirm.setOnClickListener(v ->{
            if (!patternPhone.matcher(phone).matches()) {
                AlertDialog("手机号不正确");
            } else{

            }
        });
    }

    private void AlertDialog(String message) {
        //Alert Dialog
        new AlertDialog.Builder(EditPhone.this)
                .setTitle("提示")
                .setMessage(message)
                .setNegativeButton("Close", (dialog, which) -> {
                    //do nothing - it will close on its own
                })
                .show();
    }
}