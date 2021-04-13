package com.example.oss_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsList extends AppCompatActivity {

    ListView listView;
    NewsListAdapter adapter;
    ArrayList<NewsListModel> models;
    TextView category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        category = findViewById(R.id.category);

        Intent intent = getIntent();
        category.setText(intent.getExtras().getString("value"));

        models = new ArrayList<>();
        //데이터베이스와 연동하여 기사 데이터 리스트에 저장
        adapter = new NewsListAdapter(models, this);

        listView = findViewById(R.id.viewPager);
        listView.setAdapter(adapter);

    }
}