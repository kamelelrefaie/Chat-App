package com.example.lapitchat.data.local;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class OffCap extends Application {
    private DatabaseReference userDatabaseReference;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();
        // enabled firebase offline capabilities
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // check if user sign in or not
        if (mAuth != null) {
            // get user
            mAuth = FirebaseAuth.getInstance();

            //database ref
            userDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(mAuth.getCurrentUser().getUid());

            setOnlineFeature();


        }
    }

    public void setOnlineFeature() {
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot != null) {
                        // make user offline when he close the app
                        userDatabaseReference.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
