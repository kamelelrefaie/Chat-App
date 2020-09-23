package com.example.lapitchat.view.fragment.mainCycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.lapitchat.R;
import com.example.lapitchat.view.fragment.BaseFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends BaseFragment {
    @BindView(R.id.settings_fragment_img)
    CircleImageView settingsFragmentImg;
    @BindView(R.id.settings_fragment_txt_display)
    TextView settingsFragmentTxtDisplay;
    @BindView(R.id.settings_fragment_txt_status)
    TextView settingsFragmentTxtStatus;
    @BindView(R.id.settings_fragment_btn_image)
    Button settingsFragmentBtnImage;
    @BindView(R.id.settings_fragment_btn_status)
    Button settingsFragmentBtnStatus;
    private DatabaseReference databaseReference;
    private FirebaseUser mCurrentUser;
 private Unbinder unbinder;
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this, view);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String mUId = mCurrentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mUId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String image = snapshot.child("image").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                String thumb_image = snapshot.child("thumb_image").getValue().toString();

settingsFragmentTxtDisplay.setText(name);
settingsFragmentTxtStatus.setText(status);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        setUpActivity();
        mainActivity.setToolBar(view.GONE);
        mainActivity.setFrame(view.VISIBLE);


        return view;
    }

    @Override
    public void onBack() {
        startActivity(new Intent(getActivity(), mainActivity.getClass()));
    }

}
