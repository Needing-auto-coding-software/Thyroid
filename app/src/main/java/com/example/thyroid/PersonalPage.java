package com.example.thyroid;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class PersonalPage extends AppCompatActivity {
    private final int FROM_ALBUM = 1;//表示从相册获取照片
    private final int FROM_CAMERA = 2;//表示从相机获取照片
    ImageView imageView;
    Bitmap bitmap;

    String userName;
    String realName;
    String password;
    String passwordConfirm;
    String birthday;
    String phoneNumber;


    TextView userNameText;
    TextView realNameText;
    TextView passwordText;
    TextView passwordConfirmText;
    TextView birthdayText;
    TextView phoneNumberText;

    TimePickerView pvTime; //时间选择器对象

    PopupWindow popupWindow;

    Button confirmButton;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);

        imageView = findViewById(R.id.Avatar_PersonalPage);
        imageView.setOnClickListener(v-> changeImage());

        userNameText = findViewById(R.id.UserNameText_Personal);
        passwordText = findViewById(R.id.PasswordText_Personal);
        passwordConfirmText = findViewById(R.id.PasswordConfirmText_Personal);
        realNameText = findViewById(R.id.RealNameText_Personal);
        birthdayText = findViewById(R.id.Birthday_Personal);
        phoneNumberText = findViewById(R.id.PhoneNumber_Personal);

        password = passwordText.getText().toString();
        passwordConfirm = passwordConfirmText.getText().toString();

        confirmButton = findViewById(R.id.ConfirmButton_Personal);

        birthdayText.setShowSoftInputOnFocus(false); //选中不弹出软键盘
        birthdayText.setOnClickListener(v -> {
            initTimePicker(); //初始化时间选择器
            pvTime.show();//显示时间选择
        });


        confirmButton.setOnClickListener(v -> {
            userName = userNameText.getText().toString();
            password = passwordText.getText().toString();
            passwordConfirm = passwordConfirmText.getText().toString();
            realName = realNameText.getText().toString();
            birthday= birthdayText.getText().toString();
            phoneNumber = phoneNumberText.getText().toString();

            if(userName.equals("") || password.equals("") || passwordConfirm.equals("")
                    || realName.equals("") || birthday.equals("") || phoneNumber.equals("")){
                AlertDialog();
            }else if(!password.equals(passwordConfirm)){
                AlertDialogWrong();
            }
        });

    }

    private void changeImage(){
        @SuppressLint("InflateParams") RelativeLayout layout_photo_selected = (RelativeLayout) getLayoutInflater().inflate(R.layout.photo_select,null);
        if(popupWindow==null){
            popupWindow = new PopupWindow(layout_photo_selected, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        }
        //显示popupwindows
        popupWindow.showAtLocation(layout_photo_selected, Gravity.CENTER, 0, 0);
        //设置监听器
        TextView take_photo = layout_photo_selected.findViewById(R.id.take_photo);
        TextView from_albums = layout_photo_selected.findViewById(R.id.from_albums);
        LinearLayout cancel = layout_photo_selected.findViewById(R.id.cancel);
        //拍照按钮监听
        take_photo.setOnClickListener(v->{
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, FROM_CAMERA);
        });
        //相册按钮监听
        from_albums.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, FROM_ALBUM);
        });
        //取消按钮监听
        cancel.setOnClickListener(view -> {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == FROM_ALBUM  &&  resultCode == Activity.RESULT_OK  &&  data != null){
            Uri imageUri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(imageUri));
                //int res = FaceClassified.runClassified(bitmap);
                imageView.setImageBitmap(bitmap);
            }catch (FileNotFoundException e){
                Log.e("Exception", e.getMessage(), e);

            }
        }

        //从相机返回
        if(requestCode == FROM_CAMERA  &&  resultCode == Activity.RESULT_OK  &&  data != null){
            bitmap = (Bitmap) data.getExtras().get("data");
            //int res = FaceClassified.runClassified(photo);
            imageView.setImageBitmap(BitmapUtil.toRoundBitmap(bitmap));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void AlertDialog()
    {
        //Alert Dialog
        new AlertDialog.Builder(PersonalPage.this)
                .setTitle("提示")
                .setMessage("所填内容不得为空")
                .setNegativeButton("Close", (dialog, which) -> {
                    //do nothing - it will close on its own
                })
                .show();
    }

    private void AlertDialogWrong()
    {
        //Alert Dialog
        new AlertDialog.Builder(PersonalPage.this)
                .setTitle("提示")
                .setMessage("两次所填密码不一致")
                .setNegativeButton("Close", (dialog, which) -> {
                    //do nothing - it will close on its own
                })
                .show();
    }

    private void initTimePicker() {

        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 1, 1);//起始时间
        Calendar endDate = Calendar.getInstance();
        //endDate.set(2099, 12, 31);//结束时间
        pvTime = new TimePickerView.Builder(this,
                (date, v) -> birthdayText.setText(getTimes(date)))
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