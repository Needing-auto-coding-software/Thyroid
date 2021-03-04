package com.example.thyroid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class EditName extends AppCompatActivity {

    private EditText edit_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle("修改用户名");

        edit_name = (EditText) findViewById(R.id.et_edit);
        //edit_name.setText(loginUser.getName());

        //设置监听器
        //如果点击完成，则更新loginUser并销毁
//        tl_title.getTextView_forward().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loginUser.setName(edit_name.getText().toString());
//                setResult(RESULT_OK);
//                finish();
//            }
//        });
    }

}
