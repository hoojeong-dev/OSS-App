package com.example.oss_app;

public class NewsListModel {
    private String title;

    public NewsListModel(String title) {
        this.title = title;
    }

    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return title;
    }
}
