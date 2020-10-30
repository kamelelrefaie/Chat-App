package com.example.lapitchat.helper.notification;


import com.example.lapitchat.data.model.Data;

public class NotificationSender {

    public Data data;
    public String to;

    public NotificationSender(Data data, String to) {
        this.data = data;
        this.to = to;
    }

    public NotificationSender() {
    }
}
