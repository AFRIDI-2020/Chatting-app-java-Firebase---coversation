package com.example.conversation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.conversation.activity.AccountSettingsActivity;
import com.example.conversation.activity.StartActiviy;
import com.example.conversation.activity.UsersActivity;
import com.example.conversation.model.MainVIewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ViewPager tabPager;
    private TabLayout tabLayout;
    private MainVIewPagerAdapter mainVIewPagerAdapter;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        toolbar.setTitle("Conversation");
        setSupportActionBar(toolbar);

        tabPager.setAdapter(mainVIewPagerAdapter);
        tabLayout.setupWithViewPager(tabPager);
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        //userRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        toolbar = findViewById(R.id.mainActivityToolbar);
        tabLayout = findViewById(R.id.mainActivityTabLayout);
        tabPager = findViewById(R.id.tabPager);
        mainVIewPagerAdapter = new MainVIewPagerAdapter(getSupportFragmentManager());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.toolbar_menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.logOut) {
            mAuth.signOut();
            goToStartActivity();
        } else if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, AccountSettingsActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.allUsers) {
            Intent intent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(intent);
        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            goToStartActivity();
        }

    }


    private void goToStartActivity() {
        Intent startIntent = new Intent(this, StartActiviy.class);
        startActivity(startIntent);
        finish();
    }


}