package com.example.lapitchat.view.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.lapitchat.R;
import com.example.lapitchat.view.fragment.mainCycle.conversation.ConvFrgament;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import butterknife.ButterKnife;

import static com.example.lapitchat.helper.HelperMethods.replaceFragment;

public class ChatActivity extends BaseActivity {
    private ConvFrgament convFrgament;
    private Bundle bundle;
    private DatabaseReference userDatabaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        convFrgament = new ConvFrgament();
        bundle = new Bundle();
        bundle=getIntent().getExtras();
        convFrgament.setArguments(bundle);


        replaceFragment(getSupportFragmentManager(),R.id.chat_activity_frame,convFrgament);
    }

    @Override
    protected void onStart() {
        super.onStart();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(mAuth.getCurrentUser().getUid());
        userDatabaseReference.child("online").setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        userDatabaseReference.child("online").setValue(ServerValue.TIMESTAMP);

    }
}
