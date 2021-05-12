package com.example.oss_app;

public class NewsListModel {
    public String category;
    public String key;
    public String title;
    public String content;
    public String keywords;
    public String summary;

    public NewsListModel(){

    }

    public NewsListModel(String category, String key, String title, String content, String keywords, String summary) {
        this.category = category;
        this.key = key;
        this.title = title;
        this.content = content;
        this.keywords = keywords;
        this.summary = summary;
    }

    public String getCategory() {
        return category;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getKeywords(){
        return keywords;
    }

    public String getSummary(){
        return summary;
    }
}
