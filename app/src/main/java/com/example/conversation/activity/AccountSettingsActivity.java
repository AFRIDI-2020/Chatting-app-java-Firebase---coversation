package com.example.conversation.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.conversation.MainActivity;
import com.example.conversation.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingsActivity extends AppCompatActivity {

    private TextView displayNameTV;
    private TextView statusTV;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private CircleImageView profile_image;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private ProgressDialog uploadDialog, loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        init();
        //show displayName & status from db
        getDataFromDB();
    }

    private void getDataFromDB() {
        loadingDialog.setTitle("Loading data");
        loadingDialog.setMessage("Please wait while your profile data is loading.");
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
        String currentUser = mAuth.getCurrentUser().getUid();
        databaseReference.child("users").child(currentUser)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String displayName = snapshot.child("displayName").getValue().toString();
                            String status = snapshot.child("status").getValue().toString();
                            String image = snapshot.child("image").getValue().toString();
                            displayNameTV.setText(displayName);
                            statusTV.setText(status);
                            if (!image.equals("default")) {
                                Picasso.get().load(image).placeholder(R.drawable.ic_baseline_person_150).into(profile_image);
                            }
                            loadingDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        displayNameTV = findViewById(R.id.tv_display_name);
        statusTV = findViewById(R.id.tv_status);
        profile_image = findViewById(R.id.circular_profile_image);
        uploadDialog = new ProgressDialog(this);
        loadingDialog = new ProgressDialog(this);
    }

    public void changeStatus(View view) {
        Intent intent = new Intent(this,StatusActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void changeImage(View view) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uploadDialog.setTitle("Uploading image");
                uploadDialog.setMessage("Please wait while your profile image is getting uploaded.");
                uploadDialog.setCancelable(false);
                uploadDialog.setCanceledOnTouchOutside(false);
                uploadDialog.show();
                Uri resultUri = result.getUri();
                String currentUserId = mAuth.getCurrentUser().getUid();
                storageReference = firebaseStorage.getReference();
                StorageReference profile_image_ref = storageReference.child("profile_images").child(currentUserId + ".jpg");
                UploadTask uploadTask = profile_image_ref.putFile(resultUri);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return profile_image_ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            String imageUrl = downloadUri.toString();
                            updateImageLinkToDB(imageUrl);
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error: "+error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateImageLinkToDB(String imageUrl) {
        String currentUserId = mAuth.getCurrentUser().getUid();
        databaseReference.child("users").child(currentUserId).child("image").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Picasso.get().load(imageUrl).into(profile_image);
                    uploadDialog.dismiss();
                }
                else {
                    String errorMsg = task.getException().getMessage();
                    uploadDialog.hide();
                    Toast.makeText(AccountSettingsActivity.this, "Error: "+errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}