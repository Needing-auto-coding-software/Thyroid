package com.example.thyroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FunctionalPage_Patient extends AppCompatActivity {
    ListView listView;
    TextView textView;

    int identity = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functional_page_patient);

        Intent from_Main = getIntent();
        String userName= from_Main.getStringExtra("userName");

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Welcome!");

        textView = findViewById(R.id.textView);
        textView.setText(userName);

        listView = findViewById(R.id.FunctionList_Patient);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, getData());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String string = (String) parent.getItemAtPosition(position);
            switch (string) {
                case "上传报告": {
                    Intent intent = new Intent(FunctionalPage_Patient.this,
                            ReportPage.class);
                    intent.putExtra("userName",userName);
                    intent.putExtra("identity",identity);
                    Log.d("userName",userName);
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
                    getHistoricalReport(userName);
                    break;
                }
            }
        });
    }

    private String[] getData() {
        return new String[]{"上传报告", "历史报告", "用户信息"};
    }

    //查看历史报告
    private void getHistoricalReport(String userName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //初始化OkHttpClient对象
                    OkHttpClient client = new OkHttpClient();

                    //构建params类型请求体
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("username",userName);

                    //构建请求
                    Request request = new Request.Builder()
                            .url("http://" + MainActivity.serviceIP + ":8080/case/getcases")
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
                                Toast.makeText(FunctionalPage_Patient.this,"您还未上传过报告",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    else if(status.equals("success")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        int size = jsonArray.length();
                        int[] arrayCaseId = new int[1000];
                        String[] arrayUsername = new String[1000];
                        String[] arrayCropImgPath = new String[1000];
                        String[] arrayDetectionsPath = new String[1000];
                        String[] arrayReportPath = new String[1000];
                        String[] arrayCreateTime = new String[1000];

                        //历史报告要保存以前所有的查询历史
                        for(int i = size - 1; i >= 0 ; i--){
                            JSONObject jsonObjectData = jsonArray.getJSONObject(i);
                            int caseId = jsonObjectData.getInt("id");
                            String username = jsonObjectData.getString("username");
                            String cropImgPath = jsonObjectData.getString("cropImgPath");
                            String detectionsPath = jsonObjectData.getString("detectionsPath");
                            String reportPath = jsonObjectData.getString("reportPath");
                            String createTime = jsonObjectData.getString("createTime");

                            Log.d("userName",username);
                            Log.d("cropImgPath",cropImgPath);
                            Log.d("detectionsPath",detectionsPath);
                            Log.d("reportPath",reportPath);
                            Log.d("createTime",createTime);

                            arrayCaseId[size - i - 1] = caseId;
                            arrayUsername[size - i - 1] = username;
                            arrayCropImgPath[size - i - 1] = cropImgPath;
                            arrayDetectionsPath[size - i - 1] = detectionsPath;
                            arrayReportPath[size - i - 1] = reportPath;
                            arrayCreateTime[size - i - 1] = createTime;
                        }


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent = new Intent(FunctionalPage_Patient.this,
                                        HistoricalReportPage.class);

                                intent.putExtra("size",size);
                                intent.putExtra("arrayUsername",arrayUsername);
                                intent.putExtra("arrayCaseId",arrayCaseId);
                                intent.putExtra("arrayCropImgPath",arrayCropImgPath);
                                intent.putExtra("arrayDetectionsPath",arrayDetectionsPath);
                                intent.putExtra("arrayReportPath",arrayReportPath);
                                intent.putExtra("arrayCreateTime",arrayCreateTime);
                                startActivity(intent);

                            }
                        });


                    }

                }catch(Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FunctionalPage_Patient.this,"网络错误，查询失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }
}