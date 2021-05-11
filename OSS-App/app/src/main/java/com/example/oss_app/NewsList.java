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
    static List<NewsListModel> models = new ArrayList<>();
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

        for(int i=0; i<NewsListDAO.allmodels.size(); i++){
            if(NewsListDAO.allmodels.get(i).getCategory().equals(str)){
                models.add(NewsListDAO.allmodels.get(i));
            }
        }

        for(int i=0; i<models.size(); i++){
            System.out.println(models.get(i).getTitle() + "   " + models.get(i).getTitle());
        }

        adapter = new NewsListAdapter(models, str);
        listView.setAdapter(adapter);
    }
}