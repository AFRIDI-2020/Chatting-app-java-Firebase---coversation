package com.example.conversation.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.conversation.R;
import com.example.conversation.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView usersListRV;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        init();


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("users");

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, userViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull userViewHolder holder, int position, @NonNull User model) {
                holder.userDisplayName.setText(model.getDisplayName());
                holder.userStatus.setText(model.getStatus());
                String image = model.getImage();
                if(!image.equals("default")){
                    Picasso.get().load(model.getImage()).into(holder.userProfileImage);
                }
                if(image.equals("default")) {
                    holder.userProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_60dp_black));
                }


                String user_id = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UsersActivity.this, ProfileActivity.class);
                        intent.putExtra("userId",user_id);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list_preview, parent, false);
                return new userViewHolder(view);
            }
        };

        usersListRV.setAdapter(firebaseRecyclerAdapter);


    }

    private void init() {
        usersListRV = findViewById(R.id.usersListRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        usersListRV.setLayoutManager(layoutManager);
    }


    public class userViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private CircleImageView userProfileImage;
        private TextView userDisplayName, userStatus;

        public userViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            userDisplayName = itemView.findViewById(R.id.userDisplayName);
            userStatus = itemView.findViewById(R.id.userStatus);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        firebaseRecyclerAdapter.stopListening();
    }
}