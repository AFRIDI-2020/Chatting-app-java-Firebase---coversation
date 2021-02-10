package com.example.conversation.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.conversation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView displayNameTV, statusTV, friendCount, mutualFriendCount;
    private CircleImageView profileImage;
    private DatabaseReference databaseReference;
    private ProgressDialog loadingDialog;
    private FirebaseAuth firebaseAuth;
    private Button sendFriendReqBtn;
    private String relation_state;
    private String friend_request_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();

        String user_id = getIntent().getStringExtra("userId");
        if (user_id.equals(firebaseAuth.getCurrentUser().getUid())) {
            sendFriendReqBtn.setVisibility(View.INVISIBLE);
        }

        relation_state = "not_friend";


        loadingDialog.setTitle("Profile Loading");
        loadingDialog.setMessage("Please wait while user's profile is being loaded.");
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        displayUserInfo(user_id);

        sendFriendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (relation_state.equals("not_friend")) {
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("friend_requests").child(firebaseAuth.getCurrentUser().getUid()).child(user_id)
                            .child("request_type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        databaseReference.child("friend_requests").child(user_id).child(firebaseAuth.getCurrentUser().getUid())
                                                .child("request_type").setValue("received")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        relation_state = "request_sent";
                                                        sendFriendReqBtn.setText("Cancel Friend Request");
                                                    }
                                                });
                                    }
                                }
                            });
                }

                if (relation_state.equals("request_sent")) {
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("friend_requests").child(firebaseAuth.getCurrentUser().getUid())
                            .child(user_id).child("request_type").removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        databaseReference.child("friend_requests").child(user_id).child(firebaseAuth.getCurrentUser().getUid())
                                                .child("request_type").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                relation_state = "not_friend";
                                                sendFriendReqBtn.setText("Send Friend Request");
                                            }
                                        });
                                    }
                                }
                            });
                }

                if (relation_state.equals("request_received")) {
                    final String currentDate = DateFormat.getDateInstance().format(new Date());

                    databaseReference.child("friends").child(firebaseAuth.getCurrentUser().getUid())
                            .child(user_id).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            databaseReference.child("friends").child(user_id).child(firebaseAuth.getCurrentUser().getUid()).child("date")
                                    .setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    databaseReference.child("friend_requests").child(firebaseAuth.getCurrentUser().getUid())
                                            .child(user_id).child("request_type").removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        databaseReference.child("friend_requests").child(user_id).child(firebaseAuth.getCurrentUser().getUid())
                                                                .child("request_type").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                relation_state = "friends";
                                                                sendFriendReqBtn.setText("Unfriend");
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    });
                }

                if(relation_state.equals("friends")){
                    databaseReference.child("friends").child(firebaseAuth.getCurrentUser().getUid())
                            .child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            databaseReference.child("friends").child(user_id).child(firebaseAuth.getCurrentUser().getUid())
                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    relation_state = "not_friend";
                                    sendFriendReqBtn.setText("Send Friend Request");
                                }
                            });
                        }
                    });
                }
            }
        });

    }

    private void displayUserInfo(String user_id) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("displayName").getValue().toString();
                    String status = snapshot.child("status").getValue().toString();
                    String image = snapshot.child("image").getValue().toString();

                    displayNameTV.setText(name);
                    statusTV.setText(status);

                    if (!image.equals("default")) {
                        Picasso.get().load(image).into(profileImage);
                    }
                    if (image.equals("default")) {
                        profileImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_person_150));
                    }

                    databaseReference.child("friend_requests").child(firebaseAuth.getCurrentUser().getUid()).child(user_id)
                            .child("request_type").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String request_type = snapshot.getValue().toString();
                                if (request_type.equals("received")) {
                                    relation_state = "request_received";
                                    sendFriendReqBtn.setText("Accept Friend Request");
                                } else if (request_type.equals("sent")) {
                                    relation_state = "request_sent";
                                    sendFriendReqBtn.setText("Cancel Friend Request");
                                }
                            }
                            else {
                                databaseReference.child("friends").child(firebaseAuth.getCurrentUser().getUid())
                                        .child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            relation_state = "friends";
                                            sendFriendReqBtn.setText("Unfriend");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        loadingDialog.dismiss();
    }

    private void init() {
        displayNameTV = findViewById(R.id.displayName);
        statusTV = findViewById(R.id.status);
        friendCount = findViewById(R.id.number_of_total_friends);
        mutualFriendCount = findViewById(R.id.number_of_mutual_friends);
        profileImage = findViewById(R.id.profileImage);
        loadingDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        sendFriendReqBtn = findViewById(R.id.sendFriendReqBtn);
    }


}