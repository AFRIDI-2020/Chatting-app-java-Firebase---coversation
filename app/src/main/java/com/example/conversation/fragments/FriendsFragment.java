package com.example.conversation.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.conversation.R;
import com.example.conversation.activity.ChatActivity;
import com.example.conversation.activity.ProfileActivity;
import com.example.conversation.activity.UsersActivity;
import com.example.conversation.model.Friend;
import com.example.conversation.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsFragment extends Fragment {

    private RecyclerView friendRV;
    private FirebaseAuth firebaseAuth;
    private FirebaseRecyclerAdapter<Friend, FriendViewHolder> firebaseRecyclerAdapter;
    private DatabaseReference databaseReference, friendsRef, userRef;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        friendsRef = databaseReference.child("friends");
        userRef = databaseReference.child("users");
        friendsRef.keepSynced(true);
        userRef.keepSynced(true);
        friendRV = view.findViewById(R.id.friendsRV);
        friendRV.setLayoutManager(new LinearLayoutManager(getActivity()));




        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("friends").child(firebaseAuth.getCurrentUser().getUid());


        FirebaseRecyclerOptions<Friend> options =
                new FirebaseRecyclerOptions.Builder<Friend>()
                        .setQuery(query, Friend.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friend, FriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendViewHolder holder, int position, @NonNull Friend model) {
                String key = getRef(position).getKey();
                databaseReference.child("users").child(key)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String displayName = snapshot.child("displayName").getValue().toString();
                                    holder.userDisplayName.setText(displayName);

                                    String image = snapshot.child("image").getValue().toString();
                                    if(!image.equals("default")){
                                        Picasso.get().load(image).into(holder.userProfileImage);
                                    }
                                    if(image.equals("default")) {
                                        holder.userProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_60dp_black));
                                    }


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence[] options = new CharSequence[]{"Profile", "message"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Select Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which == 0){
                                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                                    intent.putExtra("userId",key);
                                    startActivity(intent);
                                }
                                if(which == 1){
                                    Intent intent = new Intent(getContext(), ChatActivity.class);
                                    intent.putExtra("userId",key);
                                    startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_preview, parent, false);
                return new FriendViewHolder(view);
            }
        };

        friendRV.setAdapter(firebaseRecyclerAdapter);

        return view;
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView userProfileImage;
        private TextView userDisplayName;
        private ImageView online_icon;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);

            userDisplayName = itemView.findViewById(R.id.userDisplayName);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            online_icon = itemView.findViewById(R.id.online_icon);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
}