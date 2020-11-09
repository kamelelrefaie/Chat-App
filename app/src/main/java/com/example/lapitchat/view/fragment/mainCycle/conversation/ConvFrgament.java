package com.example.lapitchat.view.fragment.mainCycle.conversation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapitchat.R;
import com.example.lapitchat.adapter.MessageAdapter;
import com.example.lapitchat.data.model.Messages;
import com.example.lapitchat.helper.TimeAgo;
import com.example.lapitchat.view.fragment.BaseFragment;
import com.firebase.ui.database.FirebaseArray;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.lapitchat.helper.HelperMethods.onLoadImageFromUrl;

public class ConvFrgament extends BaseFragment {
    @BindView(R.id.chat_toolbar_back)
    ImageButton chatToolbarBack;
    @BindView(R.id.chat_toolbar_username)
    TextView chatToolbarUsername;
    @BindView(R.id.chat_toolbar_img)
    CircleImageView chatToolbarImg;
    @BindView(R.id.chat_toolbar_last_seen)
    TextView chatToolbarLastSeen;
    @BindView(R.id.conv_fragment_til)
    TextInputLayout convFragmentTil;
    @BindView(R.id.conv_fragment_img_send)
    ImageView convFragmentImgSend;
    @BindView(R.id.conv_fragment_ll_send)
    LinearLayout convFragmentLlSend;
    @BindView(R.id.chat_toolbar)
    Toolbar chatToolbar;
    @BindView(R.id.conv_fragment_rv_message)
    RecyclerView convFragmentRvMessage;
    private Unbinder unbinder;
    private Bundle bundle;
    private String userId;
    private DatabaseReference userRoot;
    private DatabaseReference rootRef;
    private DatabaseReference messageDatabaseR;
    private FirebaseAuth mFirebaseAuth;
    private String currentUser;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    public ConvFrgament() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conv, container, false);
        unbinder = ButterKnife.bind(this, view);

        // get user id from friends in main activity
        bundle = this.getArguments();
        userId = bundle.getString("USER_ID");

        // set ref to user id and values
        userRoot = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        rootRef = FirebaseDatabase.getInstance().getReference();
        messageDatabaseR = FirebaseDatabase.getInstance().getReference().child("messages");
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser().getUid();
        linearLayoutManager = new LinearLayoutManager(getActivity());
        convFragmentRvMessage.setLayoutManager(linearLayoutManager);
        messageAdapter = new MessageAdapter((ArrayList<Messages>) messagesList, currentUser, getContext());
        convFragmentRvMessage.setAdapter(messageAdapter);
        loadMessages();
        setValues();

        return view;
    }

    private void loadMessages() {
        messageDatabaseR.child(currentUser).child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages messages = snapshot.getValue(Messages.class);

                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setValues() {
        userRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {

                    //setting display name
                    String name = snapshot.child("name").getValue().toString();
                    chatToolbarUsername.setText(name);

                    //setting image
                    String img_url = snapshot.child("thumb_image").getValue().toString();
                    onLoadImageFromUrl(chatToolbarImg, img_url, getContext());

                    //set last seen
                    String lastSeen = snapshot.child("online").getValue().toString();
                    if (lastSeen.equals("Online")) {
                        chatToolbarLastSeen.setText("online");

                    } else {
                        TimeAgo timeAgo = new TimeAgo();
                        long timeLong = Long.parseLong(lastSeen);
                        String getTimeAgo = timeAgo.getTimeAgo(timeLong, getActivity());

                        chatToolbarLastSeen.setText(getTimeAgo);
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        rootRef.child("chat").child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (!snapshot.hasChild(userId)) {
                        Map chatAddMap = new HashMap();
                        chatAddMap.put("seen", false);
                        chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                        Map chatUserMap = new HashMap();
                        chatUserMap.put("chat" + "/" + currentUser + "/" + userId, chatAddMap);
                        chatUserMap.put("chat" + "/" + userId + "/" + currentUser, chatAddMap);

                        rootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                try {

                                } catch (Exception e) {

                                }
                            }
                        });

                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @OnClick(R.id.conv_fragment_img_send)
    public void onSendViewClicked() {
        sendMessage();
    }

    private void sendMessage() {
        String message = convFragmentTil.getEditText().getText().toString();


        if (!TextUtils.isEmpty(message)) {
            String currentUserRef = "messages" + "/" + currentUser + "/" + userId;
            String chatUserRef = "messages" + "/" + userId + "/" + currentUser;
            DatabaseReference userMessagePush = rootRef.child("messages").child(currentUser).child(userId).push();
            String pushId = userMessagePush.getKey();

            Map messageMap = new HashMap();

            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", currentUser);

            Map messageSetMap = new HashMap();
            messageSetMap.put(currentUserRef + "/" + pushId, messageMap);
            messageSetMap.put(chatUserRef + "/" + pushId, messageMap);

            rootRef.updateChildren(messageSetMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    try {

                    } catch (Exception e) {

                    }
                }
            });

            convFragmentTil.getEditText().setText("");
        }
    }

    @OnClick(R.id.chat_toolbar_back)
    public void onViewClicked() {
        onBack();
    }

    @Override
    public void onBack() {
        super.onBack();

    }
}
