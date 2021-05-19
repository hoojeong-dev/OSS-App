package com.example.oss_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.List;

public class ContentPage extends Fragment {

    List<NewsListModel> models = NewsList.models;
    int page, position;
    String content;
    TextView contentView;

    public static ContentPage newInstance(int page, int position) {
        ContentPage fragment = new ContentPage();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putInt("somePosition", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        page = getArguments().getInt("someInt", 0);
        position = getArguments().getInt("somePosition");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_content_page, container, false);

        content = models.get(position).getContent();
        contentView = (TextView) view.findViewById(R.id.content);
        contentView.setText(content);

        return view;
    }
}