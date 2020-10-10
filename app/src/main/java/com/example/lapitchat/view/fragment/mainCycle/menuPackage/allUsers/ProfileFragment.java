package com.example.lapitchat.view.fragment.mainCycle.menuPackage.allUsers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.lapitchat.R;
import com.example.lapitchat.helper.LoadingDialog;
import com.example.lapitchat.view.fragment.BaseFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.lapitchat.helper.HelperMethods.onLoadImageFromUrl;
import static com.example.lapitchat.helper.HelperMethods.replaceFragment;

public class ProfileFragment extends BaseFragment {

    @BindView(R.id.profile_fragment_txt_name)
    TextView profileFragmentTxtName;
    @BindView(R.id.profile_fragment_txt_status)
    TextView profileFragmentTxtStatus;
    @BindView(R.id.profile_fragment_txt_total)
    TextView profileFragmentTxtTotal;
    @BindView(R.id.profile_fragment_btn_send)
    Button profileFragmentBtnSend;
    @BindView(R.id.profile_fragment_img)
    ImageView profileFragmentImg;

    private Unbinder unbinder;
    private Bundle bundle;

    private DatabaseReference databaseReference;
    private DatabaseReference friendDatabaseReference;
    private FirebaseUser currentUser;
    private String thisUserId;
    private String Uid;
    private LoadingDialog loadingDialog;
    private int currentState;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);

        currentState = 0;
        //creating loading dialog and start it
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        //get user id from bundle
        bundle = this.getArguments();
         Uid = bundle.getString("USER_ID");

        //get user id
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
         thisUserId = currentUser.getUid();

        //get database ref
        friendDatabaseReference= FirebaseDatabase.getInstance().getReference().child("friend_req");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
        setValues(databaseReference);


        //set up main activity
        setUpActivity();
        mainActivity.setFrame(view.VISIBLE);
        return view;
    }


    private void setValues(DatabaseReference d) {

        d.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //setting display name
                String name = snapshot.child("name").getValue().toString();
                profileFragmentTxtName.setText(name);

                //setting status
                String status = snapshot.child("status").getValue().toString();
                profileFragmentTxtStatus.setText(status);

                //setting image
                String img_url = snapshot.child("image").getValue().toString();
                onLoadImageFromUrl(profileFragmentImg, img_url, getContext());

                //dismiss dialog
                loadingDialog.dismissDialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @OnClick(R.id.profile_fragment_btn_send)
    public void onViewClicked() {
     sendRequest();
    }

    private void sendRequest(){
        if (currentState == 0) {
            friendDatabaseReference.child(thisUserId).child(Uid).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        friendDatabaseReference.child(Uid).child(thisUserId).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "done sending request", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }else{
                        Toast.makeText(getActivity(), "erroe sending req", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onBack() {
        replaceFragment(getActivity().getSupportFragmentManager(), R.id.main_activity_of, new UsersFragment());
    }

}
