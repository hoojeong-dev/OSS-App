package com.example.oss_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.List;

public class SummaryPage extends Fragment {

    List<NewsListModel> models = NewsList.models;
    int page, position;
    String keywords, summary;
    TextView keywordsView, summaryView;

    public static SummaryPage newInstance(int page, int position) {
        SummaryPage fragment = new SummaryPage();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putInt("somePosition", position);
        System.out.println(position);
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
        View view = inflater.inflate(R.layout.activity_summary_page, container, false);

        keywords = models.get(position).getKeywords();
        summary = models.get(position).getSummary();

        keywordsView = (TextView) view.findViewById(R.id.keywords);
        summaryView = (TextView) view.findViewById(R.id.summary);

        keywordsView.setText(keywords);
        summaryView.setText(summary);

        return view;
    }
}