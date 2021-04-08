package com.example.oss_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class NewsList extends AppCompatActivity {

    TextView category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        category = findViewById(R.id.category);

        Intent intent = getIntent();
        category.setText(intent.getExtras().getString("value"));
    }
}