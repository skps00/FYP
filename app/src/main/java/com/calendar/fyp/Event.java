package com.calendar.fyp;

import android.util.Log;

import java.time.LocalDateTime;

public class Event {
    private long id;
    private String title = null;
    private LocalDateTime startTime = null;
    private LocalDateTime endTime = null;
    private String description = null;
    private String category;

    public Event(String title, LocalDateTime startTime) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = null;
        this.description = null;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isTitle() {
        return title != null;
    }

    public boolean isStartTime() {
        return startTime != null;
    }

    public boolean isTimePeriod() {
        return endTime != null;
    }

    public boolean isDescription() {
        return description != null;
    }

    public void show(String TAG){
        Log.v(TAG, "Title: " + title + "\nStartTime: " + startTime + "\nEndTime: " + endTime + "\nCategory: " + category + "\nID: " + id);

    }
}