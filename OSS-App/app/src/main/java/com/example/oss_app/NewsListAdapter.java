package com.example.oss_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewsListAdapter extends BaseAdapter {

    List<NewsListModel> models;
    TextView titleTextView;
    String category;

    public NewsListAdapter(List<NewsListModel> models, String category) {
        this.models = models;
        this.category = category;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int position) {
        return models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_news_list_item, parent, false);
        }

        TranslateAnimation translateAnimation = new TranslateAnimation(300, 0, 0, 0);
        Animation alphaAnimation = new AlphaAnimation(0, 1);
        translateAnimation.setDuration(500);
        alphaAnimation.setDuration(1300);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(translateAnimation);
        animation.addAnimation(alphaAnimation);
        convertView.setAnimation(animation);

        titleTextView = (TextView) convertView.findViewById(R.id.newstitle);
        titleTextView.getShadowRadius();
        titleTextView.setText(models.get(position).getTitle());

        LinearLayout clickLayout = (LinearLayout) convertView.findViewById(R.id.clickLayout);
        clickLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NewsContent.class);
                intent.putExtra("position", position);
                v.getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
