package com.example.thyroid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FunctionalPage_Patient extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functional_page_patient);

        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Welcome!");

        listView= findViewById(R.id.FunctionList_Patient);
        ArrayAdapter<String> adapter= new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, getData());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String string =(String) parent.getItemAtPosition(position);
            switch (string) {
                case "上传报告": {
                    Intent intent = new Intent(FunctionalPage_Patient.this,
                            ReportPage.class);
                    startActivity(intent);
                    break;
                }
                case "用户信息": {
                    Intent intent = new Intent(FunctionalPage_Patient.this,
                            PersonalPage.class);
                    startActivity(intent);
                    break;
                }
                case "历史报告": {
                    Intent intent = new Intent(FunctionalPage_Patient.this,
                            HistoricalReportPage.class);
                    startActivity(intent);
                    break;
                }
            }
        });
    }

    private String[] getData(){
        return new String[]{"上传报告","历史报告","用户信息"};
    }
}