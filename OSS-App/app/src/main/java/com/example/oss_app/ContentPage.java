package com.example.oss_app;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.List;

public class ContentPage extends Fragment {

    List<NewsListModel> models = NewsList.models;
    int page, position, count;
    String title, content;
    TextView contentView;
    Typeface bold, regular;

    public static ContentPage newInstance(int page, int position, int count) {
        ContentPage fragment = new ContentPage();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putInt("somePosition", position);
        args.putInt("someCount", count);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        page = getArguments().getInt("someInt", 0);
        position = getArguments().getInt("somePosition");
        count = getArguments().getInt("someCount");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_content_scroll, container, false);

        title = models.get(position).getTitle();
        content = models.get(position).getContent();
        contentView = (TextView) view.findViewById(R.id.content);

        if(count == 1){
            contentView.setText(title);
            contentView.setTextSize(30);
        }
        else{
            contentView.setText(content);
            contentView.setTextSize(20);
        }

        return view;
    }
}