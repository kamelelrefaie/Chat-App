package com.example.lapitchat.data.model;

public class Data {
    private String Title;
    private String Message;
    private String UId;

    public Data(String title, String message,String uId) {
        Title = title;
        Message = message;
        UId = uId;
    }

    public Data() {
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getUId() {
        return UId;
    }

    public void setUId(String uId) {
        UId = uId;
    }
}
