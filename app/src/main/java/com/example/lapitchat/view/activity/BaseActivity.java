package com.example.lapitchat.view.activity;


import androidx.appcompat.app.AppCompatActivity;

import com.example.lapitchat.view.fragment.BaseFragment;

public class BaseActivity extends AppCompatActivity {
    public BaseFragment baseFragment;

    @Override
    public void onBackPressed() {
        baseFragment.onBack();
    }

    public void superBackPressed() {
        super.onBackPressed();

    }
}
