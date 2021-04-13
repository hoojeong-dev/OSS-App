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

public class NewsListAdapter extends BaseAdapter {

    private ArrayList<NewsListModel> models;
    private Context context;
    private TextView titleTextView;

    public NewsListAdapter(ArrayList<NewsListModel> models, Context context){
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_news_list, parent, false);
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

        NewsListModel newsListModel = models.get(position);

        titleTextView.setText(newsListModel.getTitle());

        //클릭 메소드 작성

        return convertView;
    }
}
