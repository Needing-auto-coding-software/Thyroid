package com.example.thyroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddOrSearchDPRelation extends AppCompatActivity {

    Button getPatientList;
    Button searchPatient;
    TextView getPatUsername;
    String docUsername;
    String patUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_search_relation);

        getPatientList = findViewById(R.id.PatientListButton);
        searchPatient = findViewById(R.id.SearchPatientButton);
        getPatUsername = findViewById(R.id.UserNameText);

        Intent fromFunctionalPage = getIntent();
        docUsername = fromFunctionalPage.getStringExtra("docUsername");

        getPatientList.setOnClickListener(v -> {
            searchDPRelation(docUsername);
        });

        searchPatient.setOnClickListener(v -> {
            patUsername = getPatUsername.getText().toString();

            if(patUsername == null){
                AlertDialog();
            }else {
                findUserByUsername(patUsername);
            }

        });


    }

    private void AlertDialog()
    {
        //Alert Dialog
        new AlertDialog.Builder(AddOrSearchDPRelation.this)
                .setTitle("提示")
                .setMessage("所填内容不得为空")
                .setNegativeButton("Close", (dialog, which) -> {
                    //do nothing - it will close on its own
                })
                .show();
    }

    private void findUserByUsername(String username){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //初始化OkHttpClient对象
                    OkHttpClient client = new OkHttpClient();

                    //构建params类型请求体
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("username",username);

                    //构建请求
                    Request request = new Request.Builder()
                            .url("http://" + MainActivity.serviceIP + "/user/getbyusername")
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
                                Toast.makeText(AddOrSearchDPRelation.this,"错误:" + errMsg,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    else if(status.equals("success")){
                        addDPRelation(docUsername,patUsername,2);

                    }

                }catch(Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddOrSearchDPRelation.this,"网络错误，关联用户失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

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
                            .url("http://" + MainActivity.serviceIP + ":8080/dp/adddp")
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
                                Toast.makeText(AddOrSearchDPRelation.this,"无法关联用户，未知错误",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    else if(status.equals("success")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddOrSearchDPRelation.this,"关联成功",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }catch(Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddOrSearchDPRelation.this,"网络错误，关联用户失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }

    private void searchDPRelation(String docUsername){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //初始化OkHttpClient对象
                    OkHttpClient client = new OkHttpClient();

                    //构建params类型请求体
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("username",docUsername);

                    //构建请求
                    Request request = new Request.Builder()
                            .url("http://" + MainActivity.serviceIP + ":8080/dp/docgetdp")
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
                                Toast.makeText(AddOrSearchDPRelation.this,"您未关联过该用户",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    else if(status.equals("success")){
                        JSONArray data = jsonObject.getJSONArray("data");

                        int size = data.length();
                        int[] arrayId = new int[1000];
                        //String[] arrayDoctor = new String[1000];
                        String[] arrayPatient = new String[1000];
                        int[] arrayRelationship = new int[1000];


                        for(int i = size - 1; i >= 0; i--){
                            arrayId[size - i - 1] = data.getJSONObject(i).getInt("id");
                            //arrayDoctor[size - i - 1] = data.getJSONObject(i).getString("doctor");
                            arrayPatient[size - i - 1] = data.getJSONObject(i).getString("patient");
                            arrayRelationship[size - i - 1] = data.getJSONObject(i).getInt("relationship");

                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(AddOrSearchDPRelation.this,RelatedPatient.class);
                                intent.putExtra("size",size);
                                intent.putExtra("arrayId",arrayId);
                                intent.putExtra("doctor",docUsername);
                                intent.putExtra("arrayPatient",arrayPatient);
                                intent.putExtra("arrayRelationship",arrayRelationship);
                                startActivity(intent);

                            }
                        });
                    }

                }catch(Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddOrSearchDPRelation.this,"网络错误，关联用户失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }

}