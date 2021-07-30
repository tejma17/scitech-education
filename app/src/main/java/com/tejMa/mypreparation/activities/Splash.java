package com.tejMa.mypreparation.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.tejMa.mypreparation.R;

import java.util.Objects;


public class Splash extends AppCompatActivity {

    ImageView imageView, red, green, red2, green2;
    TextView textView;
    String lang;
    Animation top, bottom;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Log.d("AASSDDSSDD", "AT START");


        textView = findViewById(R.id.appname);
        imageView = findViewById(R.id.logo);
        red = findViewById(R.id.redline);
        green = findViewById(R.id.greenline);
        green2 = findViewById(R.id.greenbottom);
        red2 = findViewById(R.id.redbottom);

        sharedPreferences = getApplicationContext().getSharedPreferences("Language", MODE_PRIVATE);
        lang = sharedPreferences.getString("Language", "none");


        Intent intent;
        if(Objects.requireNonNull(lang).equals("none")){
            intent = new Intent(Splash.this, WelcomeActivity.class);
        }else{
            intent = new Intent(Splash.this, Navigation.class);
            if(lang.equals("मराठी")){
                textView.setText("सायटेक");
            }
        }

//        String theme = sharedPreferences.getString("Theme", "Light");
//        if(theme==null){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//        }
//        if(Objects.requireNonNull(theme).equals("Dark"))
//        {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        }
//        else if(theme.equals("Light"))
//        {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }

        Log.d("AASSDDSSDD", "CONF CHANGE");


        top = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottom = AnimationUtils.loadAnimation(this, R.anim.bot_animation);

        imageView.animate().alpha(1).setDuration(1000);
        red.setAnimation(top);
        green.setAnimation(top);
        red2.setAnimation(bottom);
        green2.setAnimation(bottom);

        ProgressBar progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);

        MobileAds.initialize(this, initializationStatus -> {
            startActivity(intent);
            progressBar.setVisibility(View.GONE);
            Log.d("AASSDDSSDD", "BEFORE FINISH");
            finish();
        });
    }

}