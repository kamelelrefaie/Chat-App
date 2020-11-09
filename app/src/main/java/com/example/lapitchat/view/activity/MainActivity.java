package com.example.lapitchat.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.lapitchat.R;
import com.example.lapitchat.adapter.ViewPagerAdapter;
import com.example.lapitchat.view.fragment.mainCycle.homeContainer.ChatMainFragment;
import com.example.lapitchat.view.fragment.mainCycle.homeContainer.FriendsMainFragment;
import com.example.lapitchat.view.fragment.mainCycle.homeContainer.RequestMainFragment;
import com.example.lapitchat.view.fragment.mainCycle.menuPackage.allUsers.ProfileFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * this is launcher activity
 */

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_activity_tl)
    TabLayout mainActivityTl;
    @BindView(R.id.main_activity_vp_frame)
    ViewPager mainActivityVpFrame;
    @BindView(R.id.main_toolbar_sub_view)
    RelativeLayout mainToolbarSubView;
    @BindView(R.id.main_activity_of)
    FrameLayout mainActivityOf;

    long backTime;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseReference;
    public RequestMainFragment requestMainFragment;
    private Intent menuIntent;
    private ProfileFragment profileFragment;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        notificationTab();

        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(toolbar);
        mainActivityTl.setupWithViewPager(mainActivityVpFrame);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);

        requestMainFragment = new RequestMainFragment();
        viewPagerAdapter.addFragment(requestMainFragment, getString(R.string.requests));
        viewPagerAdapter.addFragment(new ChatMainFragment(), getString(R.string.chat));
        viewPagerAdapter.addFragment(new FriendsMainFragment(), getString(R.string.friends));

        mainActivityVpFrame.setAdapter(viewPagerAdapter);

        //setting tabs icons
        mainActivityTl.getTabAt(0).setIcon(R.drawable.ic_request);
        mainActivityTl.getTabAt(1).setIcon(R.drawable.ic_chat);
        mainActivityTl.getTabAt(2).setIcon(R.drawable.ic_friends);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_main_btn_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, StartActivity.class));
                finish();
                break;
            case R.id.menu_main_btn_acc:
                menuIntent = new Intent(this, MenuContainerActivity.class);
                menuIntent.setAction("SETTINGS");
                startActivity(menuIntent);
                finish();
                break;
            case R.id.menu_main_btn_all:
                menuIntent = new Intent(this, MenuContainerActivity.class);
                menuIntent.setAction("USER");
                startActivity(menuIntent);
                finish();
                break;
        }
        return true;
    }

    public void setToolBar(int visibility) {
        mainToolbarSubView.setVisibility(visibility);
        mainActivityTl.setVisibility(visibility);
    }


    public void setFrame(int visibility) {
        mainActivityOf.setVisibility(visibility);
        //showing view pager
        if (visibility == View.VISIBLE) mainActivityVpFrame.setVisibility(View.GONE);
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToStart();
        } else {

            userDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(mAuth.getCurrentUser().getUid());
            userDatabaseReference.child("online").setValue("Online");


        }

    }


    /**
     * send user to start activity
     */
    private void sendToStart() {
        startActivity(new Intent(MainActivity.this, StartActivity.class));
        finish();
    }


    private void notificationTab() {
        profileFragment = new ProfileFragment();
        bundle = new Bundle();
        String intentInfo = getIntent().getAction();
        if (intentInfo == ("USER_PAGE")) {
            // Open Tab
            Bundle extras = getIntent().getExtras();
            if (!extras.isEmpty()) {
                String userId = extras.getString("USER_ID");
                menuIntent = new Intent(this, MenuContainerActivity.class);
                menuIntent.setAction("NOT");
                menuIntent.putExtra("USER_ID", userId);

                startActivity(menuIntent);
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        userDatabaseReference.child("online").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    public void superBackPressed() {
        if (backTime + 2000 > System.currentTimeMillis()) {
            finish();

        } else {
            Toast.makeText(this, "press back again to exit", Toast.LENGTH_SHORT).show();
            backTime = System.currentTimeMillis();
        }
    }
}


