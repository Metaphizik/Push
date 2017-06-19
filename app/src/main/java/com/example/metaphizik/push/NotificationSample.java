package com.example.metaphizik.push;


import java.util.Map;

public class NotificationSample {

    private String date;
    private String text;
    private String author;
    private Map<String, Boolean> to;

    public NotificationSample() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Map<String, Boolean> getTo() {
        return to;
    }

    public void setTo(Map<String, Boolean> to) {
        this.to = to;
    }
}
