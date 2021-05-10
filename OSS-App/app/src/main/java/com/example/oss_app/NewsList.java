package com.example.oss_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsList extends Activity {

    ListView listView;
    NewsListAdapter adapter;
    //List<NewsListModel> models = new ArrayList<>();
    TextView category;
    String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        listView = findViewById(R.id.listview);
        category = findViewById(R.id.category);

        Intent intent = getIntent();
        str = intent.getExtras().getString("value");
        category.setText(str);

        //models.add(new NewsListModel("key","title", "content"));
        //adapter = new NewsListAdapter(NewsListDAO.models, this);
        adapter = new NewsListAdapter();
        listView.setAdapter(adapter);
    }
}