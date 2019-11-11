package com.example.chattest;

import java.util.Date;

public class Message {
    private String message;
    private String sender;
    private long time;

    public Message(String message, String sender) {
        this.message = message;
        this.sender = sender;
        time = new Date().getTime();
    }

    public Message() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
