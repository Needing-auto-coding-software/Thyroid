package com.example.thyroid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HistoricalReportPage extends AppCompatActivity {
    ListView listView;

    int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_report_page);

        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("历史报告");

        Intent fromFunctionalPage = getIntent();

        String userName = fromFunctionalPage.getStringExtra("username");

        size = fromFunctionalPage.getIntExtra("size",size);
        int[] arrayCaseId = fromFunctionalPage.getIntArrayExtra("arrayCaseId");
        String[] arrayUsername = fromFunctionalPage.getStringArrayExtra("arrayUsername");
        String[] arrayCropImgPath = fromFunctionalPage.getStringArrayExtra("arrayCropImgPath");
        String[] arrayDetectionsPath = fromFunctionalPage.getStringArrayExtra("arrayDetectionsPath");
        String[] arrayReportPath = fromFunctionalPage.getStringArrayExtra("arrayReportPath");
        String[] arrayCreateTime = fromFunctionalPage.getStringArrayExtra("arrayCreateTime");


        listView = findViewById(R.id.ReportList_HistoricalReportPage);
        ArrayAdapter<String> adapter= new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, getData());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String string =(String) parent.getItemAtPosition(position);

            String str = string.substring(2);
            int caseId = Integer.parseInt(str);

            Intent intent = new Intent(HistoricalReportPage.this,
                    ViewReportPage.class);

            intent.putExtra("size",size);
            intent.putExtra("username",arrayUsername[caseId - 1]);
            intent.putExtra("caseId",arrayCaseId[caseId - 1]);
            intent.putExtra("cropImgPath",arrayCropImgPath[caseId - 1]);
            intent.putExtra("detectionsPath",arrayDetectionsPath[caseId - 1]);
            intent.putExtra("reportPath",arrayReportPath[caseId - 1]);
            intent.putExtra("createTime",arrayCreateTime[caseId - 1]);
            startActivity(intent);

        });

    }

    private String[] getData(){
        //从数据库中取
        String[] strings = new String[size];
        for(int i = 0; i < size ; i++){
            strings[i] = "报告" + (i + 1) ;
        }
        return strings;
        //return  new String[]{"报告1","报告2","报告3"};
    }

}