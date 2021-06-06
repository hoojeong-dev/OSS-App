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
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

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
        models.add(new CategoryModel(R.drawable.my, "My", ""));
        models.add(new CategoryModel(R.drawable.society, "Society", ""));
        models.add(new CategoryModel(R.drawable.sports, "Sports", ""));
        models.add(new CategoryModel(R.drawable.entertain, "Entertain", ""));
        models.add(new CategoryModel(R.drawable.politics, "Politics", ""));
        models.add(new CategoryModel(R.drawable.digital, "IT", ""));
        models.add(new CategoryModel(R.drawable.economic, "Economic", ""));
        models.add(new CategoryModel(R.drawable.foreign, "Foreign", ""));
        models.add(new CategoryModel(R.drawable.culture, "Culture", ""));

        adapter = new CategoryAdapter(models, this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);

        Integer[] colors_temp = {
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.white)
        };

        colors = colors_temp;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position < (adapter.getCount() - 1) && position < (colors.length - 1)) {
                    viewPager.setBackgroundColor(
                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );
                }
                else {
                    viewPager.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    public void pageback(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}