package com.example.conversation.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.conversation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private TextInputLayout statusTILayout;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        init();
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("");

        getPreviousStatus();
    }

    private void getPreviousStatus() {
        String currentUserId = mAuth.getCurrentUser().getUid();
        databaseReference.child("users").child(currentUserId).child("status")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String status = snapshot.getValue().toString();
                            statusTILayout.getEditText().setText(status);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mToolbar = findViewById(R.id.statusActivityToolbar);
        statusTILayout = findViewById(R.id.status_text_input_layout);
    }

    public void updateStaus(View view) {
        progressDialog.setTitle("Update Status");
        progressDialog.setMessage("Please wait while your status is being updated!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String currentUserId = mAuth.getCurrentUser().getUid();
        String status = statusTILayout.getEditText().getText().toString();

        if (!status.isEmpty()) {
            databaseReference.child("users").child(currentUserId).child("status")
                    .setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        goToAccoutSettingsActivity();
                    }
                    else {
                        progressDialog.hide();
                        String errorMsg = task.getException().getMessage();
                        Toast.makeText(StatusActivity.this, "Error: "+errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void goToAccoutSettingsActivity() {
        Intent intent = new Intent(StatusActivity.this, AccountSettingsActivity.class);
        startActivity(intent);
        finish();
    }
}