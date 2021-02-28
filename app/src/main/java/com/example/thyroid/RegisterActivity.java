package com.example.thyroid;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.example.util.DateUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



import static com.example.thyroid.R.id.BirthDateText_Register;

public class RegisterActivity extends AppCompatActivity {
    Button registerButton;
    TextView userNameText;
    TextView passwordText;
    TextView passwordConfirmText;
    TextView realNameText;
    TextView birthDateText;
    TextView phoneNumberText;
    TextView EmailText;
    TextView noticeText;

    String userName;
    String realName;
    String password;
    String passwordConfirm;
    String birthDate;
    String phoneNumber;
    String Email;

    RadioGroup identity_radioGroup;
    RadioButton personalButton;
    RadioButton doctorButton;
    int identity;

    RadioGroup gender_radioGroup;
    RadioButton male_Button;
    RadioButton female_Button;
    int gender;

    TimePickerView pvTime; //时间选择器对象
    //取得ActionBar对象


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //调用hide方法，隐藏actionbar
        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        userNameText = findViewById(R.id.UserNameText_Register);
        passwordText = findViewById(R.id.PasswordText_Register);
        passwordConfirmText = findViewById(R.id.PasswordConfirmText_Register);
        realNameText = findViewById(R.id.RealNameText_Register);
        birthDateText = findViewById(BirthDateText_Register);
        phoneNumberText = findViewById(R.id.PhoneNumberText_Register);
        EmailText = findViewById(R.id.EmailText_Register);
        noticeText = findViewById(R.id.NoticeText_Register);
        registerButton = findViewById(R.id.RegisterButton_Register);

        identity_radioGroup = findViewById(R.id.IdentityRadioGroup_Register);
        personalButton =findViewById(R.id.PersonalRadioButton_Register);
        doctorButton = findViewById(R.id.DoctorRadioButton_Register);

        gender_radioGroup = findViewById(R.id.genderRadioGroup_Register);
        male_Button = findViewById(R.id.MaleRadioButton_Register);
        female_Button = findViewById(R.id.FemaleRadioButton_Register);

        password = passwordText.getText().toString();
        passwordConfirm = passwordConfirmText.getText().toString();

        DisableCopyAndPaste(passwordConfirmText);

        birthDateText.setShowSoftInputOnFocus(false); //选中不弹出软键盘


        registerButton.setOnClickListener(v -> {
            userName = userNameText.getText().toString();
            password = passwordText.getText().toString();
            passwordConfirm = passwordConfirmText.getText().toString();
            realName = realNameText.getText().toString();
            birthDate = birthDateText.getText().toString();
            phoneNumber = phoneNumberText.getText().toString();
            Email = EmailText.getText().toString();

            if(userName.equals("") || password.equals("") || passwordConfirm.equals("")
                    || realName.equals("") || birthDate.equals("") || phoneNumber.equals("") || Email.equals("") ||gender == 0|| identity == 0){
                AlertDialog();
            }

            connect_Register();
        });

        birthDateText.setOnClickListener(v -> {
            initTimePicker(); //初始化时间选择器
            pvTime.show();//显示时间选择
        });

        //校验确认密码是否与密码一致
        passwordConfirmText.setFocusable(true);
        passwordConfirmText.setFocusableInTouchMode(true);
        passwordConfirmText.requestFocus();
        passwordConfirmText.requestFocusFromTouch();

        passwordConfirmText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if(!passwordConfirm.equals(password)){
                    noticeText.setText("密码不正确");
                }else{
                    noticeText.setText("");
                }
            }else {
                if(!passwordConfirm.equals("")){
                    if(!passwordConfirm.equals(password)){
                        noticeText.setText("密码不正确");
                    }else{
                        noticeText.setText("");
                    }
                }
            }
        });

        identity_radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.PersonalRadioButton_Register:
                    // 个人用户
                    identity = 2;
                    break;
                case R.id.DoctorRadioButton_Register:
                    // 医生用户
                    identity = 1;
                    break;
                default:
                    identity = 0;
                    break;
            }
        });

        //性别
        gender_radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.MaleRadioButton_Register:
                    // 男
                    gender = 2;
                    break;
                case R.id.FemaleRadioButton_Register:
                    // 女
                    gender = 1;
                    break;
                default:
                    gender = 0;
                    break;
            }
        });


    }

    private void AlertDialog()
    {
        //Alert Dialog
        new AlertDialog.Builder(RegisterActivity.this)
                .setTitle("提示")
                .setMessage("所填内容不得为空")
                .setNegativeButton("Close", (dialog, which) -> {
                    //do nothing - it will close on its own
                })
                .show();
    }

    public void DisableCopyAndPaste(TextView textView) {
        try {
            textView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTimePicker() {

        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 1, 1);//起始时间
        Calendar endDate = Calendar.getInstance();
        //endDate.set(2099, 12, 31);//结束时间
        pvTime = new TimePickerView.Builder(this,
                (date, v) -> birthDateText.setText(getTimes(date)))
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "时", "", "")
                .isCenterLabel(true)
                .setContentSize(21)
                .setDate(selectedDate)
                .setSubmitColor(Color.WHITE)//确定按钮文字颜色
                .setCancelColor(Color.WHITE)//取消按钮文字颜色
                .setRangDate(startDate, endDate)
                .setDecorView(null)
                .build();
    }

    //格式化时间
    private String getTimes(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    //注册信息前后端连接
    private void connect_Register(){
        if(TextUtils.isEmpty(userName)||TextUtils.isEmpty(realName)||TextUtils.isEmpty(password)
                ||TextUtils.isEmpty(passwordConfirm)||TextUtils.isEmpty(birthDate)||TextUtils.isEmpty(phoneNumber)||TextUtils.isEmpty(Email)
                ||gender == 0|| identity == 0){
            Toast.makeText(RegisterActivity.this,"存在输入为空，注册失败",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(passwordConfirm)){
            Toast.makeText(RegisterActivity.this,"两次密码不一致，注册失败",Toast.LENGTH_SHORT).show();
            return;
        }

        //开启新线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //初始化okHttpClient对象
                    OkHttpClient client= new OkHttpClient();

                    //构建params类型请求体
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("username",userName)
                            .add("name",realName)
                            .add("email",Email)
                            .add("telephone",phoneNumber)
                            .add("gender",""+gender)
                            .add("age",""+ DateUtil.getAge(DateUtil.parse(birthDate))) //目前age是空
                            .add("usertype",""+identity)
                            .add("password",password);


                    //构建请求
                    Request request = new Request.Builder()
                            .url("http://10.136.189.11:8080/user/register") //要改成自己的ip地址
                            .post(params.build())
                            .build();

                    //返回数据
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
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
                                Toast.makeText(RegisterActivity.this,"注册失败:"+errMsg,Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    else if(status.equals("success")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();

                                //注册成功后，自动跳转到登录页面
                                Intent jump_to_main = new Intent(RegisterActivity.this,MainActivity.class);
                                startActivity(jump_to_main);
                            }
                        });
                    }

                        /*for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Log.d("userName",jsonObject.getString("username"));
                            Log.d("realName",jsonObject.getString("name"));
                            Log.d("Email",jsonObject.getString("email"));
                            Log.d("telephone",jsonObject.getString("telephone"));
                            Log.d("gender","" + jsonObject.getInt("gender"));
                            Log.d("identity","" + jsonObject.getInt("usertype"));
                            Log.d("password",jsonObject.getString("password"));
                        }*/


                }catch(Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

}