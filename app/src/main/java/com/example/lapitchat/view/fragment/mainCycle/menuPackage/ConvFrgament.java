package com.example.lapitchat.view.fragment.mainCycle.menuPackage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.lapitchat.R;
import com.example.lapitchat.helper.TimeAgo;
import com.example.lapitchat.view.fragment.BaseFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private Unbinder unbinder;
    private Bundle bundle;
    private String userId;
    private DatabaseReference userRoot;

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
        setValues();

        return view;
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
                    String lastSeen= snapshot.child("online").getValue().toString();
                    if(lastSeen.equals("Online")){
                        chatToolbarLastSeen.setText("online");
                    }else{
                        TimeAgo timeAgo = new TimeAgo();
                        long timeLong = Long.parseLong(lastSeen);
                        String getTimeAgo = timeAgo.getTimeAgo(timeLong,getActivity());

                        chatToolbarLastSeen.setText(getTimeAgo);
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
