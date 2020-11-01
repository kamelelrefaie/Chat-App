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
import com.example.lapitchat.view.activity.MainActivity;
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

    @BindView(R.id.start_toolbar)
    Toolbar startToolbar;
    @BindView(R.id.start_toolbar_back)
    ImageButton startToolbarBack;
    @BindView(R.id.start_toolbar_title)
    TextView startToolbarTitle;
    @BindView(R.id.all_user_fragment_rv)
    RecyclerView allUserFragmentRv;
    private ProfileFragment profileFragment;
    private Bundle bundle;
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

        //set title
        startToolbarTitle.setText(R.string.all_users);

        //use bundle to send information
        profileFragment = new ProfileFragment();
        bundle = new Bundle();

        myRef = FirebaseDatabase.getInstance().getReference().child("Users");

        //set recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        allUserFragmentRv.setLayoutManager(layoutManager);
        allUserFragmentRv.setHasFixedSize(true);

        // get data to fill page
        fetch();

        // setup activity
        setUpActivity();
        mainActivity.setToolBar(view.GONE);
        mainActivity.setFrame(view.VISIBLE);

        return view;
    }

    // using firebase recycler
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
                holder.setImage(model.getImage(), getActivity());
                holder.setTxtStatus(model.getStatus());
                String Uid = getRef(position).getKey();

                // when clicking in whole item
                holder.usersAdapterRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //send info through bundle
                        bundle.putString("USER_ID", Uid);
                        profileFragment.setArguments(bundle);

                        // move to profile fragment
                        replaceFragment(getActivity().getSupportFragmentManager(), R.id.main_activity_of, profileFragment);
                    }
                });

            }


        };

        //setting recyclerview adapter
        allUserFragmentRv.setAdapter(adapter);
    }

    // adding back feature to toolbar
    @OnClick(R.id.start_toolbar_back)
    public void onViewClicked() {
        //go to main activity
        startActivity(new Intent(getActivity(), mainActivity.getClass()));
    }

    // send to main activity

    @Override
    public void onBack() {
        startActivity(new Intent(getActivity(), mainActivity.getClass()));
    }



    // running adapter
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    // stop loading it
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
