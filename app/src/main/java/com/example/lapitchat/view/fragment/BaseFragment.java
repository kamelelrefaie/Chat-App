package com.example.lapitchat.view.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lapitchat.view.activity.BaseActivity;
import com.example.lapitchat.view.activity.MainActivity;
import com.example.lapitchat.view.activity.StartActivity;


public class BaseFragment extends Fragment {

    public BaseActivity baseActivity;
    public StartActivity startActivity;
    public MainActivity mainActivity;

    public void setUpActivity() {
        baseActivity = (BaseActivity) getActivity();
        baseActivity.baseFragment = this;

        try {
            mainActivity =(MainActivity) getActivity();

        }catch (Exception r){

        }
        try {
            startActivity = (StartActivity) getActivity();

        } catch (Exception e) {

        }
    }

    public void onBack() {
        baseActivity.superBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setUpActivity();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

}
