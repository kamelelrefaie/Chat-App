package com.example.lapitchat.view.fragment.mainCycle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.lapitchat.R;
import com.example.lapitchat.helper.LoadingDialog;
import com.example.lapitchat.view.activity.MainActivity;
import com.example.lapitchat.view.fragment.BaseFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.lapitchat.helper.HelperMethods.replaceFragment;

public class StatusFragment extends BaseFragment {
    @BindView(R.id.start_toolbar)
    Toolbar startToolbar;
    @BindView(R.id.start_toolbar_back)
    ImageButton startToolbarBack;
    @BindView(R.id.start_toolbar_title)
    TextView startToolbarTitle;

    @BindView(R.id.status_fragment_til)
    TextInputLayout statusFragmentTil;
    @BindView(R.id.status_fragment_btn_change)
    Button statusFragmentBtnChange;
    Unbinder unbinder;
    private  Bundle bundle;
    private LoadingDialog loadingDialog;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    public StatusFragment() {
        // Required empty public constructor
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        unbinder = ButterKnife.bind(this, view);
        bundle = this.getArguments();
        statusFragmentTil.getEditText().setText(bundle.getString("STATUS_TXT"));
        startToolbarTitle.setText(R.string.account_settings);
        loadingDialog = new LoadingDialog(getActivity());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uID = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uID).child("status");

        return view;
    }

    @OnClick(R.id.status_fragment_btn_change)
    public void onViewClicked() {
        loadingDialog.startLoadingDialog();
        String getStatus = statusFragmentTil.getEditText().getText().toString();
        databaseReference.setValue(getStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                } else {
                    loadingDialog.dismissDialog();
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @OnClick(R.id.start_toolbar_back)
    public void onBackClicked() {
       replaceFragment(getActivity().getSupportFragmentManager(),R.id.main_activity_of,new SettingsFragment());
    }

    @Override
    public void onBack() {
        replaceFragment(getActivity().getSupportFragmentManager(),R.id.main_activity_of,new SettingsFragment());
    }
}
