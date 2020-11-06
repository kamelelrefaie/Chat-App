package com.example.lapitchat.view.fragment.mainCycle;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapitchat.R;
import com.example.lapitchat.adapter.FriendsViewholder;
import com.example.lapitchat.adapter.UsersViewHolder;
import com.example.lapitchat.data.model.Friends;
import com.example.lapitchat.data.model.Users;
import com.example.lapitchat.view.activity.ChatActivity;
import com.example.lapitchat.view.activity.MenuContainerActivity;
import com.example.lapitchat.view.fragment.BaseFragment;
import com.example.lapitchat.view.fragment.mainCycle.menuPackage.ConvFrgament;
import com.example.lapitchat.view.fragment.mainCycle.menuPackage.allUsers.ProfileFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.lapitchat.helper.HelperMethods.replaceFragment;

public class FriendsMainFragment extends BaseFragment {

    @BindView(R.id.friends_fragment_rv)
    RecyclerView friendsFragmentRv;
    private DatabaseReference friendDatabaseRef;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private Unbinder unbinder;
    private DatabaseReference usersDatabaseReference;
    private FirebaseRecyclerAdapter adapter;
    private Bundle bundle;
    private ConvFrgament convFrgament;
    private Intent menuIntent;

    public FriendsMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        unbinder = ButterKnife.bind(this, view);

        // get user id
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        // ref
        friendDatabaseRef = FirebaseDatabase.getInstance().getReference().child("friends").child(currentUserId);
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        // off cap
        friendDatabaseRef.keepSynced(true);
        usersDatabaseReference.keepSynced(true);

        //set recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        friendsFragmentRv.setLayoutManager(layoutManager);
        friendsFragmentRv.setHasFixedSize(true);

        bundle = new Bundle();
        convFrgament = new ConvFrgament();

        fetch();
        return view;
    }


    // using firebase recycler
    private void fetch() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("friends")
                .child(currentUserId);

        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query, new SnapshotParser<Friends>() {
                            @NonNull
                            @Override
                            public Friends parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Friends(snapshot.getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Friends, FriendsViewholder>(options) {

            @Override
            public FriendsViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_users, parent, false);

                return new FriendsViewholder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FriendsViewholder holder, int position, @NonNull Friends model) {
                holder.setDate(model.getDate());

                String listUserId = getRef(position).getKey();
                usersDatabaseReference.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("name").getValue().toString();
                        String thumbImage = snapshot.child("thumb_image").getValue().toString();
                        if (snapshot.hasChild("online")) {
                            String onlineF = snapshot.child("online").getValue().toString();
                            holder.setImageOnline(onlineF);
                        }

                        holder.setImage(thumbImage, getActivity());
                        holder.setTxtDisplay(name);
                        holder.usersAdapterRoot.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] = new CharSequence[]{"open profile", "send message"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        switch (i) {
                                            case 0:
                                                menuIntent = new Intent(getActivity(), MenuContainerActivity.class);
                                                menuIntent.setAction("NOT");
                                                menuIntent.putExtra("USER_ID", listUserId);
                                                startActivity(menuIntent);
                                                break;
                                            case 1:
                                                Intent intent = new Intent(getActivity(), ChatActivity.class);
                                                intent.putExtra("USER_ID", listUserId);
                                                startActivity(intent);
                                                break;
                                        }

                                    }
                                });
                                builder.show();
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }


        };

        //setting recyclerview adapter
        friendsFragmentRv.setAdapter(adapter);
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
