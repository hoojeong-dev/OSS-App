package com.example.oss_app;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    ViewPager viewPager;
    Adapter adapter;
    List<Model> models;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        models = new ArrayList<>();
        models.add(new Model(R.drawable.brochure, "Brochure", "dkssudgktpdy wp dlfmadms rlagnwjd dlqslek. dk dlrj wlsWk gkrl rnlcksgdk enlwlrpTspdu... tkffuwntpdy wj sjan glaemfdjdy"));
        models.add(new Model(R.drawable.sticker, "Sticker", "dkssudgktpdy wp dlfmadms rlagnwjd dlqslek. dk dlrj wlsWk gkrl rnlcksgdk enlwlrpTspdu... tkffuwntpdy wj sjan glaemfdjdy"));
        models.add(new Model(R.drawable.poster, "Poster", "dkssudgktpdy wp dlfmadms rlagnwjd dlqslek. dk dlrj wlsWk gkrl rnlcksgdk enlwlrpTspdu... tkffuwntpdy wj sjan glaemfdjdy"));
        models.add(new Model(R.drawable.namecard, "Namecard", "dkssudgktpdy wp dlfmadms rlagnwjd dlqslek. dk dlrj wlsWk gkrl rnlcksgdk enlwlrpTspdu... tkffuwntpdy wj sjan glaemfdjdy"));

        adapter = new Adapter(models, this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);

        Integer[] colors_temp = {
                getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4)
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