package com.example.lapitchat.view.fragment.mainCycle.menuPackage.allUsers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapitchat.R;
import com.example.lapitchat.adapter.UsersViewHolder;
import com.example.lapitchat.data.model.Users;
import com.example.lapitchat.view.fragment.BaseFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.lapitchat.helper.HelperMethods.replaceFragment;

public class UsersFragment extends BaseFragment {
       private  ProfileFragment profileFragment;
       private  Bundle bundle;
    @BindView(R.id.start_toolbar)
    Toolbar startToolbar;
    @BindView(R.id.start_toolbar_back)
    ImageButton startToolbarBack;
    @BindView(R.id.start_toolbar_title)
    TextView startToolbarTitle;

    @BindView(R.id.all_user_fragment_rv)
    RecyclerView allUserFragmentRv;
    private Unbinder unbinder;
    private DatabaseReference myRef;
    private FirebaseRecyclerAdapter adapter;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_users, container, false);
        unbinder = ButterKnife.bind(this, view);
        startToolbarTitle.setText(R.string.all_users);
        profileFragment = new ProfileFragment();
        bundle=new Bundle();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        myRef = FirebaseDatabase.getInstance().getReference().child("Users");
        allUserFragmentRv.setLayoutManager(layoutManager);
        allUserFragmentRv.setHasFixedSize(true);
        fetch();
        setUpActivity();
        mainActivity.setToolBar(view.GONE);
        mainActivity.setFrame(view.VISIBLE);

        return view;
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, new SnapshotParser<Users>() {
                            @NonNull
                            @Override
                            public Users parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Users(snapshot.child("thumb_image").getValue().toString(),
                                        snapshot.child("name").getValue().toString(),
                                        snapshot.child("status").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_users, parent, false);

                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
              holder.setTxtDisplay(model.getName());
              holder.setImage(model.getImage(),getActivity());
              holder.setTxtStatus(model.getStatus());
              String Uid = getRef(position).getKey();
       holder.usersAdapterRoot.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               bundle.putString("USER_ID", Uid);
               profileFragment.setArguments(bundle);
       replaceFragment(getActivity().getSupportFragmentManager(),R.id.main_activity_of, profileFragment);
           }
       });

            }


        };
        allUserFragmentRv.setAdapter(adapter);
    }


    @OnClick(R.id.start_toolbar_back)
    public void onViewClicked() {
        startActivity(new Intent(getActivity(), mainActivity.getClass()));
    }

    @Override
    public void onBack() {
        startActivity(new Intent(getActivity(), mainActivity.getClass()));
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
