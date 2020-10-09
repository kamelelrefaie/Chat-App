package com.example.lapitchat.view.fragment.mainCycle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.lapitchat.R;
import com.example.lapitchat.view.fragment.BaseFragment;
import com.example.lapitchat.view.fragment.mainCycle.menuPackage.UsersFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.lapitchat.helper.HelperMethods.replaceFragment;

public class ProfileFragment extends BaseFragment {
    @BindView(R.id.profile_fragment_diplay)
    TextView profileFragmentDiplay;
    Unbinder unbinder;
 private  Bundle bundle;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);

        bundle = this.getArguments();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(bundle.getString("USER_ID")).child("name");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
        profileFragmentDiplay.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }

    @Override
    public void onBack() {
        replaceFragment(getActivity().getSupportFragmentManager(),R.id.main_activity_of,new UsersFragment());
    }
}
