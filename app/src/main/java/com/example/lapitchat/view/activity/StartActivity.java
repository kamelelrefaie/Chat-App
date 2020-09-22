package com.example.lapitchat.view.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.lapitchat.R;
import com.example.lapitchat.view.fragment.userCycle.StartFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.lapitchat.helper.HelperMethods.replaceFragment;

public class StartActivity extends BaseActivity {


    @BindView(R.id.start_toolbar)
    Toolbar startToolbar;
    @BindView(R.id.start_toolbar_back)
    ImageButton startToolbarBack;
    @BindView(R.id.start_toolbar_title)
    TextView startToolbarTitle;
    @BindView(R.id.start_toolbar_sub_view)
    RelativeLayout startToolbarSubView;
    @BindView(R.id.start_activity_frame)
    FrameLayout startActivityFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        replaceFragment(getSupportFragmentManager(), R.id.start_activity_frame, new StartFragment());
    }

    public void setToolBar(int visibility, String title, View.OnClickListener backActionBtn) {
        startToolbarSubView.setVisibility(visibility);

        if (visibility == View.VISIBLE) {
            startToolbarTitle.setText(title);
            startToolbarBack.setOnClickListener(backActionBtn);
        }

    }
}