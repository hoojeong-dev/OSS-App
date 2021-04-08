package com.example.oss_app;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class CategoryPage extends Activity {

    ViewPager viewPager;
    Adapter adapter;
    List<Model> models;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_page);

        models = new ArrayList<>();
        models.add(new Model(R.drawable.society_image, "Society", ""));
        models.add(new Model(R.drawable.sports_image, "Sports", ""));
        models.add(new Model(R.drawable.poster, "Entertain", ""));
        models.add(new Model(R.drawable.namecard, "Politics", ""));
        models.add(new Model(R.drawable.brochure, "Editorial", ""));
        models.add(new Model(R.drawable.it_image, "IT", ""));
        models.add(new Model(R.drawable.economic_image, "Economic", ""));
        models.add(new Model(R.drawable.foreign_image, "Foreign", ""));
        models.add(new Model(R.drawable.brochure, "Culture", ""));
        models.add(new Model(R.drawable.sticker, "Press", ""));

        adapter = new Adapter(models, this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);

        Integer[] colors_temp = {
                getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4),
                getResources().getColor(R.color.color5),
                getResources().getColor(R.color.color6),
                getResources().getColor(R.color.color7),
                getResources().getColor(R.color.color8),
                getResources().getColor(R.color.color9),
                getResources().getColor(R.color.color10)
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
}