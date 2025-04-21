package com.yourcompany.yourapp.model;

public class Notification {
    private String title;
    private String message;
    private String category;

    public Notification(String title) {
      this.title = title;
    }
    public Notification(String title, String message, String category) {
        this.title = title;
        this.message = message;
        this.category = category;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    // toString()
    @Override
    public String toString() {
        return "Notification{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
