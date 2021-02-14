package com.example.thyroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button registerButton;
    Button loginButton;
    TextView userNameText;
    TextView passwordText;
    String userName;
    String password;

    RadioGroup radioGroup;
    RadioButton personalButton;
    RadioButton doctorButton;

    int identity = 0; //identity=1 为医生; identity=2 为患者; identity=0 未选择

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerButton = findViewById(R.id.RegisterButton_Main);
        loginButton = findViewById(R.id.LoginButton_Main);
        userNameText = findViewById(R.id.UserNameText_Main);
        passwordText = findViewById(R.id.PasswordText_Main);
        radioGroup = findViewById(R.id.RadioGroup_Main);
        personalButton =findViewById(R.id.PersonalRadioButton_Main);
        doctorButton = findViewById(R.id.DoctorRadioButton_Main);

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,
                    RegisterActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            userName = userNameText.getText().toString();
            password = passwordText.getText().toString();

            if(userName.equals("") || password.equals("")){
                AlertDialog();
            }else if(identity == 0){
                AlertDialogChoose();
            }else if(identity == 1){
                Intent intent = new Intent(MainActivity.this,FunctionalPage_Doctor.class);
                startActivity(intent);
            }else if(identity == 2){
                Intent intent = new Intent(MainActivity.this,FunctionalPage_Patient.class);
                startActivity(intent);
            }
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.PersonalRadioButton_Main:
                    // 个人用户
                    identity = 2;
                    break;
                case R.id.DoctorRadioButton_Main:
                    // 医生用户
                    identity = 1;
                    break;
                default:
                    identity = 0;
                    break;
            }
        });
    }

    private void AlertDialog()
    {
        //Alert Dialog
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("提示")
                .setMessage("用户名或密码不得为空")
                .setNegativeButton("Close", (dialog, which) -> {
                    //do nothing - it will close on its own
                })
                .show();
    }

    private void AlertDialogChoose() {
        //Alert Dialog
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("提示")
                .setMessage("请选择用户类型")
                .setNegativeButton("Close", (dialog, which) -> {
                    //do nothing - it will close on its own
                })
                .show();

    }


}