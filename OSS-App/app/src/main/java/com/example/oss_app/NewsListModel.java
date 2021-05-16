package com.example.oss_app;

public class NewsListModel {
    public String category;
    public String key;
    public String title;
    public String content;
    public String keyword;
    public String summary;
    public String sentiment;

    public NewsListModel(){

    }

    public NewsListModel(String category, String key, String title, String content, String keyword, String summary, String sentiment) {
        this.category = category;
        this.key = key;
        this.title = title;
        this.content = content;
        this.keyword = keyword;
        this.summary = summary;
        this.sentiment = sentiment;
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
        return keyword;
    }

    public String getSummary(){
        return summary;
    }

    public String getSentiment(){
        return sentiment;
    }
}
