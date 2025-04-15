package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.example.chatapp.Models.Users;
import com.example.chatapp.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;

    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // update values in firebase
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status=binding.etStatus.getText().toString();
                String userName=binding.etUserName.getText().toString();

                // Create scale animation
                Animation anim = new ScaleAnimation(
                        1f, 0.9f, // Start and end values for the X axis scaling
                        1f, 0.9f, // Start and end values for the Y axis scaling
                        Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                        Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                anim.setFillAfter(true); // Needed to keep the result of the animation
                anim.setDuration(200); // Duration in milliseconds
                v.startAnimation(anim); // Start the animation


                HashMap<String , Object> obj=new HashMap<>();
                obj.put("userName",userName);
                obj.put("status",status);

                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(obj);

            }
        });


      //get data from database
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       Users users=snapshot.getValue(Users.class);
                       Picasso.get()
                                .load(users.getProfilePic())
                              // .placeholder(R.drawable.avatar)
                                .into(binding.profileImage);

                        // get values of username and status from database
                        binding.etStatus.setText(users.getStatus());
                        binding.etUserName.setText(users.getUserName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate from settingActivity to gallery
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*"); //only images can set from gallery , */* to set all types of files
                startActivityForResult(intent,33);
            }
        });
    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 33 && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                binding.profileImage.setImageURI(selectedImageUri);

                // Upload the image to Firebase Storage
                final StorageReference storageReference = storage.getReference()
                        .child("profile_pictures")
                        .child(FirebaseAuth.getInstance().getUid());

                UploadTask uploadTask = storageReference.putFile(selectedImageUri);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update the user's profile picture URL in the database
                        database.getReference().child("Users")
                                .child(FirebaseAuth.getInstance().getUid())
                                .child("profilePic")
                                .setValue(uri.toString())
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(SettingsActivity.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(SettingsActivity.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show());
                    });
                }).addOnFailureListener(e ->
                        Toast.makeText(SettingsActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(SettingsActivity.this, "Failed to get image URI", Toast.LENGTH_SHORT).show();
            }
        }
    }*/
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      super.onActivityResult(requestCode, resultCode, data);

      if (resultCode == RESULT_OK && requestCode == 33 && data != null) {
          Uri selectedImageUri = data.getData();
          if (selectedImageUri != null) {
              binding.profileImage.setImageURI(selectedImageUri);

              // Upload the image to Firebase Storage
              final StorageReference storageReference = storage.getReference()
                      .child("profile_pictures")
                      .child(FirebaseAuth.getInstance().getUid());

              UploadTask uploadTask = storageReference.putFile(selectedImageUri);
              uploadTask.addOnSuccessListener(taskSnapshot -> {
                  storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                      // Update the user's profile picture URL in the database
                      database.getReference().child("Users")
                              .child(FirebaseAuth.getInstance().getUid())
                              .child("profilePic")
                              .setValue(uri.toString())
                              .addOnSuccessListener(aVoid -> {
                                  Toast.makeText(SettingsActivity.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                                  Log.d("ProfileUpdate", "Profile picture updated successfully");
                              })
                              .addOnFailureListener(e -> {
                                  Toast.makeText(SettingsActivity.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                                  Log.e("ProfileUpdate", "Failed to update profile picture: " + e.getMessage());
                              });
                  }).addOnFailureListener(e -> {
                      Toast.makeText(SettingsActivity.this, "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                      Log.e("ProfileUpdate", "Failed to get download URL: " + e.getMessage());
                  });
              }).addOnFailureListener(e -> {
                  Toast.makeText(SettingsActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                  Log.e("ProfileUpdate", "Failed to upload image: " + e.getMessage());
              });
          } else {
              Toast.makeText(SettingsActivity.this, "Failed to get image URI", Toast.LENGTH_SHORT).show();
              Log.e("ProfileUpdate", "Failed to get image URI");
          }
      }
  }

}
