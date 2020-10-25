package com.example.lapitchat.data.local;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class OffCap extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
