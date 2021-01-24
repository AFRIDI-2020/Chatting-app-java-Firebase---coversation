package com.example.conversation.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.conversation.R;

public class StartActiviy extends AppCompatActivity {

    private Button goToRegBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_activiy);

        init();

        goToRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(StartActiviy.this, RegisterActivity.class);
                startActivity(regIntent);
            }
        });
    }

    private void init() {
        goToRegBtn = findViewById(R.id.goToRegBtn);
    }
}