package com.example.lapitchat.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.lapitchat.R;
import com.example.lapitchat.data.model.Users;
import com.example.lapitchat.view.fragment.mainCycle.menuPackage.SettingsFragment;
import com.example.lapitchat.view.fragment.mainCycle.menuPackage.allUsers.ProfileFragment;
import com.example.lapitchat.view.fragment.mainCycle.menuPackage.allUsers.UsersFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.ButterKnife;

import static com.example.lapitchat.helper.HelperMethods.replaceFragment;

public class MenuContainerActivity extends BaseActivity {
    private SettingsFragment settingsFragment;
    private UsersFragment usersFragment;
    private DatabaseReference userDatabaseReference;
    private FirebaseAuth mAuth;
    private ProfileFragment profileFragment;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_container);
        ButterKnife.bind(this);
        // initialization
        mAuth = FirebaseAuth.getInstance();
         profileFragment = new ProfileFragment();
         bundle = new Bundle();
        settingsFragment = new SettingsFragment();
        usersFragment = new UsersFragment();

        //get intent info this info sent from main activity
        String intentInfo = getIntent().getAction();

      if(intentInfo.equals("SETTINGS")){
        // go to settings
          replaceFragment(getSupportFragmentManager(),R.id.menu_container_activity_frame,settingsFragment);
      }
      else if(intentInfo.equals("USER")){
          // go to all user
         replaceFragment(getSupportFragmentManager(),R.id.menu_container_activity_frame,usersFragment);
      }
      else if(intentInfo.equals("NOT")){
          bundle=getIntent().getExtras();
          profileFragment.setArguments(bundle);

          replaceFragment(getSupportFragmentManager(),R.id.menu_container_activity_frame,profileFragment);

      }

    }

    @Override
    protected void onStart() {
        super.onStart();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(mAuth.getCurrentUser().getUid());
        userDatabaseReference.child("online").setValue(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        userDatabaseReference.child("online").setValue(false);

    }
}
