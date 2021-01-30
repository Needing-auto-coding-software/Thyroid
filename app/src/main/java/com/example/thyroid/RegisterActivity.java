package com.example.thyroid;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.thyroid.R.id.BirthDateText_Register;

public class RegisterActivity extends AppCompatActivity {
    Button registerButton;
    TextView userNameText;
    TextView passwordText;
    TextView passwordConfirmText;
    TextView realNameText;
    TextView birthDateText;
    TextView phoneNumberText;
    TextView noticeText;

    String userName;
    String realName;
    String password;
    String passwordConfirm;
    String birthDate;
    String phoneNumber;


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
        noticeText = findViewById(R.id.NoticeText_Register);
        registerButton = findViewById(R.id.RegisterButton_Register);

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

            if(userName.equals("") || password.equals("") || passwordConfirm.equals("")
                || realName.equals("") || birthDate.equals("") || phoneNumber.equals("")){
                AlertDialog();
            }
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

}