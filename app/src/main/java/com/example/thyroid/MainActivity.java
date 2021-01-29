package com.example.thyroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button registerButton;
    Button loginButton;
    TextView userNameText;
    TextView passwordText;
    String userName;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerButton = findViewById(R.id.RegisterButton_Main);
        loginButton = findViewById(R.id.LoginButton_Main);
        userNameText = findViewById(R.id.UserNameText_Main);
        passwordText = findViewById(R.id.PasswordText_Main);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = userNameText.getText().toString();
                password = passwordText.getText().toString();

                if(userName.equals("") || password.equals("")){
                    AlertDialog();
                }
            }
        });
    }

    private void AlertDialog()
    {
        //Alert Dialog
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("提示")
                .setMessage("用户名或密码不得为空")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing - it will close on its own
                    }
                })
                .show();

    };


}