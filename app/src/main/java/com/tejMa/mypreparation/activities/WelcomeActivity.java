package com.tejMa.mypreparation.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tejMa.mypreparation.R;

public class WelcomeActivity extends AppCompatActivity {

    CardView medium_layout, std_layout;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        medium_layout = findViewById(R.id.medium_layout);
        std_layout = findViewById(R.id.std_layout);

        sharedPreferences = getSharedPreferences("Language", MODE_PRIVATE);

        medium_layout.setX(10000);
    }

    public void mediumSelect(View view){
        Button button = (Button) view;
        String lang = button.getText().toString();
        if(button.getText().equals("ENGLISH")) lang = "English";
        sharedPreferences.edit().putString("Language", lang).apply();
        Toast toast = Toast.makeText(this, "You can change these settings in\nSettings->Medium & Class", Toast.LENGTH_SHORT);
        toast.show();
        startActivity(new Intent(this, Navigation.class));
        finish();
    }

    public void stdSelect(View view){
        Button button = (Button) view;
        String std = button.getText().toString();
        sharedPreferences.edit().putString("Class", std).apply();
        Toast.makeText(this, "Standard selected : " + std, Toast.LENGTH_SHORT).show();
        std_layout.animate().translationXBy(-10000).setDuration(500);
        medium_layout.animate().translationXBy(-10000).setDuration(500);
    }

    @Override
    public void onBackPressed() {
        if(std_layout.getTranslationX() == -10000){
            std_layout.animate().translationXBy(10000).setDuration(500);
            medium_layout.animate().translationXBy(10000).setDuration(500);
        } else {
            super.onBackPressed();
        }
    }
}