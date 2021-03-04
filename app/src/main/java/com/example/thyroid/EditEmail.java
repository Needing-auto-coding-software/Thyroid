package com.example.thyroid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import java.util.regex.Pattern;

public class EditEmail extends AppCompatActivity {

    private EditText edit_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("修改邮箱");
        String regexEmail = "/w+([-+.]/w+)*@/w+([-.]/w+)*/./w+([-.]/w+)*";
        Pattern patternEmail = Pattern.compile(regexEmail);
        edit_email = (EditText) findViewById(R.id.et_edit);
        String email = edit_email.getText().toString();
        edit_email.setOnClickListener(v ->{
            if (!patternEmail.matcher(email).matches()) {
                AlertDialog("邮箱不正确");
            } else{

            }
        });
    }

    private void AlertDialog(String message) {
        //Alert Dialog
        new AlertDialog.Builder(EditEmail.this)
                .setTitle("提示")
                .setMessage(message)
                .setNegativeButton("Close", (dialog, which) -> {
                    //do nothing - it will close on its own
                })
                .show();
    }
}