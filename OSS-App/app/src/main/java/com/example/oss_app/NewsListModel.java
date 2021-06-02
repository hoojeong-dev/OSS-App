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

    public void setCategory(String category) {
        this.category = category;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setSentiment(String sentiment) {
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
