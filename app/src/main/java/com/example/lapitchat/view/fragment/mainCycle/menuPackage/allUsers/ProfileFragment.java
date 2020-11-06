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
import androidx.annotation.Nullable;

import com.example.lapitchat.R;
import com.example.lapitchat.data.model.Data;
import com.example.lapitchat.data.model.MyResponse;
import com.example.lapitchat.helper.LoadingDialog;
import com.example.lapitchat.helper.notification.NotificationSender;
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

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.lapitchat.data.api.RetrofitClient.getClient;
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
    @BindView(R.id.profile_fragment_btn_decline)
    Button profileFragmentBtnDecline;

    private Unbinder unbinder;
    private Bundle bundle;
    private String thisUserName;
    private DatabaseReference databaseReference;
    private DatabaseReference nameReference;
    private DatabaseReference friendReqDatabaseReference;
    private DatabaseReference friendDatabaseReference;
    private DatabaseReference rootRef;
    private FirebaseUser currentUser;

    private String thisUserId;

    private String Uid;

    private LoadingDialog loadingDialog;

    private String currentState;
    private String requestType;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);



        //creating loading dialog and start it
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();
        profileFragmentBtnDecline.setVisibility(View.INVISIBLE);
        profileFragmentBtnDecline.setEnabled(false);

        //get user id from bundle
        bundle = this.getArguments();
        Uid = bundle.getString("USER_ID");

        //get user id
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        thisUserId = currentUser.getUid();

        //get database ref
        friendDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friends");
        rootRef = FirebaseDatabase.getInstance().getReference();
        friendReqDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friend_req");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
        nameReference = FirebaseDatabase.getInstance().getReference().child("Users").child(thisUserId);
        getUserName();

        setValues(databaseReference);


        //set up main activity
        return view;
    }

    private void getUserName() {
        nameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                thisUserName = snapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void setValues(DatabaseReference d) {

        d.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                currentState = "not_friends";
                //setting display name
                String name = snapshot.child("name").getValue().toString();
                profileFragmentTxtName.setText(name);

                //setting status
                String status = snapshot.child("status").getValue().toString();
                profileFragmentTxtStatus.setText(status);

                //setting image
                String img_url = snapshot.child("image").getValue().toString();
                onLoadImageFromUrl(profileFragmentImg, img_url, getContext());

                //changing btn according to app state
                changeBtn();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

// change the state of button

    private void changeBtn() {
        // is current user have the sec one
        friendReqDatabaseReference.child(thisUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // if he have it
                if (snapshot.hasChild(Uid)) {
                    // getting request type
                    requestType = snapshot.child(Uid).child("request_type").getValue().toString();

                    if (requestType.equals("received")) {

                        currentState = "req_received";
                        profileFragmentBtnSend.setText(R.string.accept_friend_request);

                        profileFragmentBtnDecline.setVisibility(View.VISIBLE);
                        profileFragmentBtnDecline.setEnabled(true);


                    } else if (requestType.equals("sent")) {
                        currentState = "req_sent";
                        profileFragmentBtnSend.setText(R.string.cancel_friend_request);


                    }
                    // if he haven't it
                } else {

                    friendDatabaseReference.child(thisUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(Uid)) {
                                currentState = "friends";
                                profileFragmentBtnSend.setText(R.string.remove_this_person);


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                //dismiss dialog
                loadingDialog.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @OnClick({R.id.profile_fragment_btn_send, R.id.profile_fragment_btn_decline})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.profile_fragment_btn_send:
                sendRequest();

                break;
            case R.id.profile_fragment_btn_decline:
                if (requestType.equals("received")) {
                    friendReqDatabaseReference.child(thisUserId).child(Uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendReqDatabaseReference.child(Uid).child(thisUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    profileFragmentBtnSend.setEnabled(true);
                                    currentState = "not_friends";
                                    profileFragmentBtnSend.setText(R.string.send_friend_request);
                                    profileFragmentBtnDecline.setVisibility(View.INVISIBLE);
                                    profileFragmentBtnDecline.setEnabled(false);


                                }
                            });
                        }
                    });
                }
                break;
        }
    }

    private void sendRequest() {

        profileFragmentBtnSend.setEnabled(false);
//not friend and send req
        if (currentState.equals("not_friends")) {
            Map requestMap = new HashMap();
            requestMap.put(thisUserId + "/" + Uid + "/" + "request_type", "sent");
            requestMap.put(Uid + "/" + thisUserId + "/" + "request_type", "received");
            friendReqDatabaseReference.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    profileFragmentBtnSend.setEnabled(true);
                    currentState = "req_sent";
                    profileFragmentBtnSend.setText(R.string.cancel_friend_request);

                    Toast.makeText(getActivity(), "done sending request", Toast.LENGTH_SHORT).show();

                    FirebaseDatabase.getInstance().getReference().child("Tokens").child(Uid).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String usertoken = dataSnapshot.getValue(String.class);
                            sendNotifications(usertoken, getString(R.string.friend_request), thisUserName + " sent you a friend request");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
        }

        if (currentState.equals("friends")) {

            Map unFriend = new HashMap();
            unFriend.put("friends" + "/" + thisUserId + "/" + Uid, null);
            unFriend.put("friends" + "/" + Uid + "/" + thisUserId, null);
            rootRef.updateChildren(unFriend, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    try {
                        profileFragmentBtnSend.setEnabled(true);
                        currentState = "not_friends";
                        profileFragmentBtnSend.setText(R.string.send_friend_request);
                    } catch (Exception e) {

                    }
                }
            });
        }

        // request sent and cancel it
        if (currentState.equals("req_sent")) {
            friendReqDatabaseReference.child(thisUserId).child(Uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    friendReqDatabaseReference.child(Uid).child(thisUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            profileFragmentBtnSend.setEnabled(true);
                            currentState = "not_friends";
                            profileFragmentBtnSend.setText(R.string.send_friend_request);


                        }
                    });
                }
            });
        }

        // req received and accept it
        if (currentState.equals("req_received")) {
            String currentDate = DateFormat.getDateTimeInstance().format(new Date());
            Map friendsMap = new HashMap();
            //adding friends
            friendsMap.put("friends" + "/" + thisUserId + "/" + Uid + "/" + "date", currentDate);
            friendsMap.put("friends" + "/" + Uid + "/" + thisUserId + "/" + "date", currentDate);
            //removing old query
            friendsMap.put("friend_req" + "/" + thisUserId + "/" + Uid, null);
            friendsMap.put("friend_req" + "/" + Uid + "/" + thisUserId, null);

            rootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    try {

                        profileFragmentBtnSend.setEnabled(true);
                        currentState = "friends";
                        profileFragmentBtnSend.setText(R.string.remove_this_person);

                        FirebaseDatabase.getInstance().getReference().child("Tokens").child(Uid).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String usertoken = dataSnapshot.getValue(String.class);
                                sendNotifications(usertoken, getString(R.string.friend_request), thisUserName + " accepted your friend request");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } catch (Exception e) {

                    }
                }
            });

        }

    }


    @Override
    public void onBack() {
        replaceFragment(getActivity().getSupportFragmentManager(), R.id.menu_container_activity_frame, new UsersFragment());
    }


    public void sendNotifications(String usertoken, String title, String message) {

        Data data = new Data(title, message, thisUserId);
        NotificationSender sender = new NotificationSender(data, usertoken);
        getClient().sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                try {
                    if (response.code() == 200) {
                        Toast.makeText(getActivity(), "Notification sending", Toast.LENGTH_SHORT).show();
                        if (response.body().success != 1) {
                            Toast.makeText(getActivity(), "Failed ", Toast.LENGTH_LONG);
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(getActivity(), "error" + e, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

}

