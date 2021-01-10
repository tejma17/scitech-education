package com.tejMa.mypreparation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.util.Locale;
import java.util.Objects;

public class Navigation extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SpaceNavigationView spaceNavigationView;
    String lang;
    SpaceOnClickListener spaceOnClickListener;
    boolean homeActive = true;
    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        sharedPreferences = getSharedPreferences("Language", MODE_PRIVATE);
        lang = sharedPreferences.getString("Language", "English");

        if(Objects.requireNonNull(lang).equals("मराठी")){
            setLocale("mr", "मराठी");
        } else
            setLocale("en", "English");

        spaceNavigationView = findViewById(R.id.spaceView);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("HOME", R.drawable.ic_round_home_24));
        spaceNavigationView.addSpaceItem(new SpaceItem("NOTES", R.drawable.ic_notes));
        spaceNavigationView.addSpaceItem(new SpaceItem("DOUBTS", R.drawable.ic_chat));
        spaceNavigationView.addSpaceItem(new SpaceItem("SETTINGS", R.drawable.ic_round_settings_24));
        spaceNavigationView.showIconOnly();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Quizzes()).commit();
        }


        spaceOnClickListener = new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                startActivity(new Intent(getApplicationContext(), ArLaunch.class));
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex){
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container,
                                        new Quizzes()).commit();
                        homeActive = true;
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container,
                                        new ShowNotes()).commit();
                        homeActive = false;
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container,
                                        new Doubts()).commit();
                        homeActive = false;
                        break;
                    case 3:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container,
                                        new Settings()).commit();
                        homeActive = false;
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

            }
        };

        spaceNavigationView.setSpaceOnClickListener(spaceOnClickListener);

    }



    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void setLocale(String lang, String langu) {
        // optional - Helper method to save the selected language to SharedPreferences in case you might need to attach to activity context (you will need to code this)
        Locale locale = new Locale(lang, "IN");
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
        setContentView(R.layout.activity_navigation);
        sharedPreferences.edit().putString("Language", langu).apply();
    }

}