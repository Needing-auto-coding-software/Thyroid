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
                    addDPRelation("doc","swj",2);
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

    private void addDPRelation(String docUsername,String patUsername,int type){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //初始化OkHttpClient对象
                    OkHttpClient client = new OkHttpClient();

                    //构建params类型请求体
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("doctor",docUsername)
                          .add("patient",patUsername)
                          .add("type",""+type);

                    //构建请求
                    Request request = new Request.Builder()
                            .url("http://192.168.31.226:8080/dp/adddp")
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
                                Toast.makeText(FunctionalPage_Doctor.this,"无法关联用户，未知错误",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    else if(status.equals("success")){

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(FunctionalPage_Doctor.this,
                                        RelatedPatient.class);

                                intent.putExtra("patient",patUsername);

                                startActivity(intent);
                            }
                        });
                    }

                }catch(Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FunctionalPage_Doctor.this,"网络错误，关联用户失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }
}