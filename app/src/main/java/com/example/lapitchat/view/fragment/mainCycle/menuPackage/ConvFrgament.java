package com.example.lapitchat.view.fragment.mainCycle.menuPackage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lapitchat.R;
import com.example.lapitchat.view.fragment.BaseFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ConvFrgament extends BaseFragment {
    private Unbinder unbinder;
    private Bundle bundle;
    private String userId;
    public ConvFrgament() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_empty, container, false);
        unbinder = ButterKnife.bind(this, view);
        bundle = this.getArguments();
        userId =  bundle.getString("USER_ID");





        return view;
    }
}
