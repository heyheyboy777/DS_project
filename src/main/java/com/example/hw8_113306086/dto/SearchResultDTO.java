package com.example.hw8_113306086.dto;
public class SearchResultDTO {
    private String title;
    private String url;
    private double score;
    public void setTitle(String title) {
        this.title = title;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setScore(double score) {
        this.score = score;
}
     public double getScore() { return score; }
        public String getTitle() { return title; }
        public String getUrl() { return url; }
}
