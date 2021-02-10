package com.example.conversation.model;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.conversation.activity.LoginActivity;
import com.example.conversation.activity.StartActiviy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class ConversationApp extends Application {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mAuth = FirebaseAuth.getInstance();


    }
}
