package com.example.lapitchat.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.lapitchat.helper.notification.Token;
import com.example.lapitchat.view.fragment.mainCycle.ChatMainFragment;
import com.example.lapitchat.view.fragment.mainCycle.FriendsMainFragment;
import com.example.lapitchat.view.fragment.mainCycle.RequestMainFragment;
import com.example.lapitchat.view.fragment.mainCycle.menuPackage.SettingsFragment;
import com.example.lapitchat.view.fragment.mainCycle.menuPackage.allUsers.UsersFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.lapitchat.helper.HelperMethods.replaceFragment;

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
    public RequestMainFragment requestMainFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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


        UpdateToken();

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
                replaceFragment(getSupportFragmentManager(), R.id.main_activity_of, new SettingsFragment());
                break;
            case R.id.menu_main_btn_all:
                replaceFragment(getSupportFragmentManager(), R.id.main_activity_of, new UsersFragment());
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
        }

    }

    /**
     *  send user to start activity
     */
    private void sendToStart() {
        startActivity(new Intent(MainActivity.this, StartActivity.class));
        finish();
    }


    @Override
    public void superBackPressed() {
        if (backTime + 2000 > System.currentTimeMillis()) {
            finish();
            return;
        } else {
            Toast.makeText(MainActivity.this, "press back again to exit", Toast.LENGTH_SHORT).show();
            backTime = System.currentTimeMillis();
        }
    }

    private void UpdateToken(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }
}


