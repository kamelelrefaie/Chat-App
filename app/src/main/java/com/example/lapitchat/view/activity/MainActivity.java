package com.example.lapitchat.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.lapitchat.R;
import com.example.lapitchat.adapter.ViewPagerAdapter;
import com.example.lapitchat.view.fragment.mainCycle.ChatMainFragment;
import com.example.lapitchat.view.fragment.mainCycle.FriendsMainFragment;
import com.example.lapitchat.view.fragment.mainCycle.MainFragment;
import com.example.lapitchat.view.fragment.mainCycle.RequestMainFragment;
import com.example.lapitchat.view.fragment.mainCycle.SettingsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.lapitchat.helper.HelperMethods.replaceFragment;

public class MainActivity extends BaseActivity {
    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_activity_tl)
    TabLayout mainActivityTl;
    @BindView(R.id.main_activity_frame)
    ViewPager mainActivityFrame;
    @BindView(R.id.main_toolbar_sub_view)
    RelativeLayout mainToolbarSubView;
    @BindView(R.id.main_activity_of)
    FrameLayout mainActivityOf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        replaceFragment(getSupportFragmentManager(), R.id.main_activity_frame, new MainFragment());
        setSupportActionBar(toolbar);
        mainActivityTl.setupWithViewPager(mainActivityFrame);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);

        viewPagerAdapter.addFragment(new RequestMainFragment(), getString(R.string.requests));
        viewPagerAdapter.addFragment(new ChatMainFragment(), getString(R.string.chat));
        viewPagerAdapter.addFragment(new FriendsMainFragment(), getString(R.string.friends));


        mainActivityFrame.setAdapter(viewPagerAdapter);

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
                replaceFragment(getSupportFragmentManager(), R.id.main_activity_of, new SettingsFragment());

        }
        return true;
    }

    public void setToolBar(int visibility) {
        mainToolbarSubView.setVisibility(visibility);
        mainActivityTl.setVisibility(visibility);
    }

    public void setFrame(int visibility){
       mainActivityOf.setVisibility(visibility);
    }


}