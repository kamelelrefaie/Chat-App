package com.example.lapitchat.view.fragment.mainCycle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lapitchat.R;
import com.example.lapitchat.view.fragment.BaseFragment;

public class SettingsFragment extends BaseFragment {
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_settings, container, false);





        return view;
    }
}
