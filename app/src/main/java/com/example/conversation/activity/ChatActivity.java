package com.example.conversation.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

import com.example.conversation.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DatabaseReference userRef;
    private TextView tv_personName, tv_last_seen;
    private CircleImageView person_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        String person_id = getIntent().getStringExtra("userId");
        LoadPersonData(person_id);
    }

    private void LoadPersonData(String person_id) {
        userRef.child(person_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String person_name = snapshot.child("displayName").getValue().toString();
                    tv_personName.setText(person_name);

                    String image = snapshot.child("image").getValue().toString();
                    if(!image.equals("default")){
                        Picasso.get().load(image).into(person_image);
                    }
                    if(image.equals("default")) {
                        person_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_person_24));
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.chatActivityToolbar);
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        tv_personName = findViewById(R.id.tv_person_name);
        person_image = findViewById(R.id.person_iamge);
        tv_last_seen = findViewById(R.id.tv_last_seen);
    }
}