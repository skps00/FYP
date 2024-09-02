package com.calendar.fyp;

import java.util.ArrayList;
import java.util.List;

public class MessageListManager {
    private static List<Message> messageList = new ArrayList<>();

    public static void saveMessageList(List<Message> list) {
        messageList = list;
    }

    public static List<Message> getMessageList() {
        return messageList;
    }

    public static void clearMessage() {
        messageList = new ArrayList<>();
    }
}
