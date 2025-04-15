package com.example.chatapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.example.chatapp.Models.Users;
import com.example.chatapp.databinding.ActivitySignUpBinding;
import com.example.chatapp.Models.Users;
import com.example.chatapp.databinding.ActivitySignUpBinding;
import com.example.chatapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;

    ProgressDialog progressDialog; // USED FOR LOADING

    // Declare variables for phone number verification
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    //instead of using find view by id we generally use binding,we create a binding function in build.gradle
  private FirebaseAuth auth;
    FirebaseDatabase database;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    int RC_SIGN_IN=30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // If ActionBar exists, hide it (Only needed if ActionBar is present)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        auth=FirebaseAuth.getInstance(); // here we take instance of firebase auth
        // we use auth or sign up
        database=FirebaseDatabase.getInstance(); // database is used to save the data in firebase

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Creating account");
        progressDialog.setMessage("We're creating your account");

        FirebaseUser currentUser = auth.getCurrentUser();

    /*    if (currentUser != null) {
            // User is already signed in, redirect to MainActivity
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish(); // Close the SignInActivity
        }*/


        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
               .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsc= GoogleSignIn.getClient(this,gso);

        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation anim = new ScaleAnimation(
                        1f, 0.9f, // Start and end values for the X axis scaling
                        1f, 0.9f, // Start and end values for the Y axis scaling
                        Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                        Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                anim.setFillAfter(true); // Needed to keep the result of the animation
                anim.setDuration(200); // Duration in milliseconds
                v.startAnimation(anim); // Start the animation

            SignIn();
            }
        });


        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.etUserName.getText().toString().trim();
                String mail = binding.etEmail.getText().toString().trim();
                String password = binding.etPassword.getText().toString();

                Animation anim = new ScaleAnimation(
                        1f, 0.9f, // Start and end values for the X axis scaling
                        1f, 0.9f, // Start and end values for the Y axis scaling
                        Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                        Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                anim.setFillAfter(true); // Needed to keep the result of the animation
                anim.setDuration(200); // Duration in milliseconds
                v.startAnimation(anim); // Start the animation


                if (isValidEmail(mail) && password.length() >= 6) {
                    // Sign up logic
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();

                                        Users user=new Users(binding.etUserName.getText().toString(),binding.etEmail.getText().toString(),
                                                binding.etPassword.getText().toString());
                                        String id=task.getResult().getUser().getUid();
                                        database.getReference().child("Users").child(id).setValue(user);
                                        // Sign up success, navigate to MainActivity
                                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        // Sign up failed
                                        Toast.makeText(SignUpActivity.this, "This email is already used by another account", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(SignUpActivity.this, "Please enter a valid email and a password with at least 6 characters.", Toast.LENGTH_SHORT).show();
                }
            }

            private boolean isValidEmail(CharSequence target) {
                return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
            }
        });


        binding.tvAlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });
    }


    // Sign in with Google method
    private void SignIn() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    // Handle the result of the Google Sign-In
   @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful,authenticate with Firebase
                GoogleSignInAccount account=task.getResult(ApiException.class);
                Log.d("Tag","firebaseAuthWithGoogle:"+account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            }
            catch (ApiException e){
                //Google sign in is failed,update UI appropriately
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Authenticate with Firebase using the Google ID token
    private void firebaseAuthWithGoogle(String idToken){

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        Log.d("TAG", "signInWithCredential:success");

                        Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                    }
                });
    }
}