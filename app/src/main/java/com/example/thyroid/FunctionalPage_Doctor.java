package com.example.thyroid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FunctionalPage_Doctor extends AppCompatActivity {

    ListView listView;

    int identity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functional_page_doctor);

        Intent from_Main = getIntent();
        String userName= from_Main.getStringExtra("userName");

        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Welcome!");

        listView= findViewById(R.id.FunctionList_Doctor);
        ArrayAdapter<String> adapter= new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, getData());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String string =(String) parent.getItemAtPosition(position);
            switch (string) {
                case "上传报告": {
                    Intent intent = new Intent(FunctionalPage_Doctor.this,
                            ReportPage.class);
                    intent.putExtra("userName",userName);
                    intent.putExtra("identity",identity);
                    Log.d("userName",userName);
                    startActivity(intent);
                    break;
                }
                case "关联用户": {
                    Intent intent = new Intent(FunctionalPage_Doctor.this,
                            RelatedPatient.class);
                    startActivity(intent);
                    break;
                }
                case "用户信息": {
                    Intent intent = new Intent(FunctionalPage_Doctor.this,
                            PersonalPage.class);
                    startActivity(intent);
                    break;
                }
            }
        });
    }

    private String[] getData(){
        return new String[]{"上传报告","关联用户","用户信息"};
    }
}