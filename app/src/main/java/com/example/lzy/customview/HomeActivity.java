package com.example.lzy.customview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class HomeActivity extends AppCompatActivity {

    private ListView mListView;
    private ListViewEx mListViewEx;
    private String[] strings = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "7", "8", "9", "10", "7", "8", "9", "10"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mListView = (ListView) findViewById(R.id.listView);
        mListViewEx = (ListViewEx) findViewById(R.id.listView2);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1
                , strings);
        mListView.setAdapter(adapter);
        mListViewEx.setAdapter(adapter);
    }

}
