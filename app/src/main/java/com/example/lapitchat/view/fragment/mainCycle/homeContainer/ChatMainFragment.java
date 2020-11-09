package com.example.lapitchat.view.fragment.mainCycle.homeContainer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lapitchat.R;
import com.example.lapitchat.view.activity.MainActivity;
import com.example.lapitchat.view.fragment.BaseFragment;

public class ChatMainFragment extends BaseFragment {
    private long backTime;
    public ChatMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chat, container, false);





        return view;
    }


    @Override
    public void onBack() {

        if (backTime + 2000 > System.currentTimeMillis()) {
            getActivity().finish();

        } else {
            Toast.makeText(getActivity(), "press back again to exit", Toast.LENGTH_SHORT).show();
            backTime = System.currentTimeMillis();
        }
    }
}
