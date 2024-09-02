package com.calendar.fyp;

import java.util.ArrayList;

public class Message {
    private String sender;
    private String message;
    private String time;
    private ArrayList<Event> eventArrayList;
    private int type;

    public Message(String sender, String message, String time, int type) {
        this.sender = sender;
        this.message = message;
        this.time = time;
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public ArrayList<Event> getEventArrayList() {
        return eventArrayList;
    }

    public void setEventArrayList(ArrayList<Event> eventArrayList) {
        this.eventArrayList = eventArrayList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}