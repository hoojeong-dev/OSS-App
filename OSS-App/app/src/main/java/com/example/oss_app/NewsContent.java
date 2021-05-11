package com.example.oss_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsContent extends AppCompatActivity {

    List<NewsListModel> models = NewsList.models;
    int position;
    String key, title, content;
    TextView titleView, contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);

        Intent intent = getIntent();
        position = intent.getExtras().getInt("position");

        title = models.get(position).getTitle();
        content = models.get(position).getContent();

        titleView = (TextView) findViewById(R.id.title);
        contentView = (TextView) findViewById(R.id.content);
        titleView.setText(models.get(position).getTitle());
        //contentView.setText(models.get(position).getContent());
    }

    public void getModels(List<NewsListModel> models) {
        this.models = models;
    }
}