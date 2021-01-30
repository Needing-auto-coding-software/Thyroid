package com.example.thyroid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RelatedPatient extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_patient);

        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("关联用户");

        listView = findViewById(R.id.ListView_RelatedPatient);
        ArrayAdapter<String> adapter= new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, getData());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String string =(String) parent.getItemAtPosition(position);
            Intent intent = new Intent(this,HistoricalReportPage.class);
            intent.putExtra("patient_name",string);
            startActivity(intent);
        });
    }

    private String[] getData(){
        //从数据库中取
        return  new String[]{"用户1","用户2","用户3"};
    }
}