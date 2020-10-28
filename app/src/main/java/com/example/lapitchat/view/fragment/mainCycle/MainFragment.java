package com.example.lapitchat.view.fragment.mainCycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.lapitchat.R;
import com.example.lapitchat.view.activity.MainActivity;
import com.example.lapitchat.view.activity.StartActivity;
import com.example.lapitchat.view.fragment.BaseFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainFragment extends BaseFragment {

    long backTime;
    Unbinder unbinder;
    private FirebaseAuth mAuth;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);

        mAuth = FirebaseAuth.getInstance();

        //to set up Activity
        setUpActivity();

        //set mainLayout
        mainActivity.setFrame(view.GONE);
        mainActivity.setToolBar(view.VISIBLE);

        return view;
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
        startActivity(new Intent(getActivity(), StartActivity.class));
        getActivity().finish();
    }


    @Override
    public void onBack() {
        super.onBack();
        if (backTime + 2000 > System.currentTimeMillis()) {
            getActivity().finish();
            return;
        } else {
            Toast.makeText(getActivity(), "press back again to exit", Toast.LENGTH_SHORT).show();
            backTime = System.currentTimeMillis();
        }
    }
}
