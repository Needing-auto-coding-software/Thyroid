package com.example.thyroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static String serviceIP = "10.135.150.188";

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

            /*这部分写在前后端连接里啦！
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
            */

            connect_Login();

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

    private void connect_Login(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //初始化OkHttpClient对象
                    OkHttpClient client = new OkHttpClient();

                    //构建params类型请求体
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("username",userName)
                            .add("password",password);

                    //构建请求
                    Request request = new Request.Builder()
                            .url("http://10.137.65.149:8080/user/login")
                            .post(params.build())
                            .build();

                    //返回数据
                    Response response= client.newCall(request).execute();
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);

                    String status = jsonObject.getString("status");

                    if(status.equals("fail")){
                        JSONObject data = jsonObject.getJSONObject("data");
                        String errCode = data.getString("errCode");
                        String errMsg = data.getString("errMsg");


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run(){
                                if(errCode.equals("10001")){
                                    AlertDialog();
                                    //Toast.makeText(MainActivity.this,"登录失败：用户名或密码不得为空",Toast.LENGTH_SHORT).show();
                                }
                                else if(errCode.equals("20002")){
                                    Toast.makeText(MainActivity.this,"登录失败：" + errMsg,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    else if(status.equals("success")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(identity == 0){
                                    AlertDialogChoose();
                                }
                                else if(identity == 1){
                                    Intent intent = new Intent(MainActivity.this,FunctionalPage_Doctor.class);
                                    intent.putExtra("userName",userName);
                                    startActivity(intent);
                                }
                                else if(identity == 2){
                                    Intent intent = new Intent(MainActivity.this,FunctionalPage_Patient.class);
                                    intent.putExtra("userName",userName);
                                    startActivity(intent);
                                }
                            }
                        });
                    }

                }catch(Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,"网络错误，登录失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

        
    }
}