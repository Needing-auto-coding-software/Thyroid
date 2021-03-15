package com.example.thyroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class EditPassword extends AppCompatActivity {

    EditText passwordText;
    EditText passwordConfirmText;
    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        confirm = findViewById(R.id.confirm);
        passwordText = findViewById(R.id.et_edit);
        passwordConfirmText = findViewById(R.id.et_edit_confirm);
        String password = passwordText.getText().toString();
        String passwordConfirm = passwordConfirmText.getText().toString();
        confirm.setOnClickListener(v -> {
            if (!passwordConfirm.equals(password)) {
                AlertDialog("两次输入密码不一致");
            }
        });
    }


    private void AlertDialog(String message) {
        //Alert Dialog
        new AlertDialog.Builder(EditPassword.this)
                .setTitle("提示")
                .setMessage(message)
                .setNegativeButton("Close", (dialog, which) -> {
                    //do nothing - it will close on its own
                })
                .show();
    }
}