package com.example.oss_app;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;


public class CategoryPage extends Activity {

    ViewPager viewPager;
    CategoryAdapter adapter;
    List<CategoryModel> models;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_page);
        MainActivity.viewPoint = findViewById(R.id.view_point);

        if (MainActivity.mode == 0)
            MainActivity.viewPoint.setVisibility(View.INVISIBLE);
        else
            MainActivity.viewPoint.setVisibility(View.VISIBLE);

        NewsListDAO newsListDAO = new NewsListDAO();
        MyContentsDAO myContentsDAO = new MyContentsDAO();
        newsListDAO.LoadData();
        myContentsDAO.LoadData();

        models = new ArrayList<>();
        models.add(new CategoryModel(R.drawable.society, "Society", ""));
        models.add(new CategoryModel(R.drawable.sports, "Sports", ""));
        models.add(new CategoryModel(R.drawable.entertain, "Entertain", ""));
        models.add(new CategoryModel(R.drawable.politics, "Politics", ""));
        models.add(new CategoryModel(R.drawable.digital, "IT", ""));
        models.add(new CategoryModel(R.drawable.economic, "Economic", ""));
        models.add(new CategoryModel(R.drawable.foreign, "Foreign", ""));
        models.add(new CategoryModel(R.drawable.culture, "Culture", ""));
        models.add(new CategoryModel(R.drawable.my, "My", ""));

        adapter = new CategoryAdapter(models, this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);
    }

    public void pageback(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}