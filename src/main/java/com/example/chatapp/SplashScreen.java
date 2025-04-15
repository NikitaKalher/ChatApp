package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class SplashScreen extends AppCompatActivity {

    TextView ChatApp,Messaging;
    LottieAnimationView lottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        ChatApp =findViewById(R.id.ChatApp);
        Messaging=findViewById(R.id.Messaging);
        lottie=findViewById(R.id.lottie);

        AlphaAnimation fadeIn=new AlphaAnimation(0.0f,1.0f);
        fadeIn.setDuration(3000);
        ChatApp.startAnimation(fadeIn);
        Messaging.startAnimation(fadeIn);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
            }
        },3000);
    }
}