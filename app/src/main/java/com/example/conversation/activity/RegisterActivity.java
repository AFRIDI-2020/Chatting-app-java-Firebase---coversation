package com.example.conversation.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.conversation.MainActivity;
import com.example.conversation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout displayNameTILayout, emailTILayout, passwordTILayout;
    private Button regBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayName = displayNameTILayout.getEditText().getText().toString();
                String email = emailTILayout.getEditText().getText().toString();
                String password = passwordTILayout.getEditText().getText().toString();

                register_user(displayName, email, password);
            }
        });
    }

    private void register_user(String displayName, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent mainActivityIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(mainActivityIntent);
                            finish();
                        }
                        else {
                            String errorMsg = task.getException().getMessage();
                            Toast.makeText(RegisterActivity.this, "Error "+errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        displayNameTILayout = findViewById(R.id.display_name_text_input_layout);
        emailTILayout = findViewById(R.id.email_text_input_layout);
        passwordTILayout = findViewById(R.id.password_text_input_layout);
        regBtn = findViewById(R.id.regBtn);
    }
}