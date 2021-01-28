package com.example.conversation.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.conversation.MainActivity;
import com.example.conversation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private ImageView backIcon;
    private TextInputLayout emailTILayout, passwordTILayout;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToStartActivity();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTILayout.getEditText().getText().toString();
                String password = passwordTILayout.getEditText().getText().toString();

                if(!email.isEmpty() && !password.isEmpty()){
                    progressDialog.setTitle("Login");
                    progressDialog.setMessage("Please wait while we are checking your credential");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    login(email,password);
                }
            }
        });
    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            goToMainActivity();
                        }
                        else {
                            progressDialog.hide();
                            String errorMsg = task.getException().getMessage();
                            Toast.makeText(LoginActivity.this, "Error: "+errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void goToStartActivity() {
        Intent intent = new Intent(LoginActivity.this,StartActiviy.class);
        startActivity(intent);
        finish();
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        loginBtn = findViewById(R.id.loginBtn);
        backIcon = findViewById(R.id.backIcon);
        emailTILayout = findViewById(R.id.email_text_input_layout);
        passwordTILayout = findViewById(R.id.password_text_input_layout);
        progressDialog = new ProgressDialog(this);
    }
}