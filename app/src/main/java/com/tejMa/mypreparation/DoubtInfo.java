package com.tejMa.mypreparation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tejMa.mypreparation.adapters.CustomVolleyRequest;

import java.util.Locale;
import java.util.Objects;

public class DoubtInfo extends AppCompatActivity {

    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    String lang, std, ques, answ, image_url;
    private TextView ans, que;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doubt_info);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);

        sharedPreferences = getSharedPreferences("Language", MODE_PRIVATE);
        lang = sharedPreferences.getString("Language", "English");
        std = sharedPreferences.getString("Class", "10");
        que = findViewById(R.id.que);
        ans = findViewById(R.id.ans);

        if(lang.equals("मराठी")){
            setLocale("mr", "मराठी");
        } else
            setLocale("en", "English");

        String[] chap = Objects.requireNonNull(getIntent().getStringExtra("DoubtID")).split("TEJMA");
        actionBar.setTitle(chap[0]);

        databaseReference = FirebaseDatabase.getInstance().getReference("Doubts").child(std).child(lang).child(chap[0]).child(chap[1]);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ques = snapshot.getKey();
                answ = Objects.requireNonNull(snapshot.child("ans").getValue()).toString();
                image_url = Objects.requireNonNull(snapshot.child("img").getValue()).toString();
                answ = answ.replaceAll("\n", "<br>");
                answ = answ.replaceAll("_dot_", "\u25CF");
                que.setText(ques);
                ans.setText(Html.fromHtml(answ));
                if(!image_url.equals("no")) {
                    setImage(image_url);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //menu actions
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return true;
    }

    private void setImage(String image_url)
    {
        NetworkImageView imageView = findViewById(R.id.ans_image);
        imageView.setBackgroundResource(R.drawable.outline);
        ImageLoader imageLoader = CustomVolleyRequest.getInstance(getApplicationContext())
                .getImageLoader();
        imageLoader.get(image_url, ImageLoader.getImageListener(imageView,
                R.drawable.loading, R.color.background));
        imageView.setImageUrl(image_url, imageLoader);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    private void setLocale(String lang, String langu) {
        // optional - Helper method to save the selected language to SharedPreferences in case you might need to attach to activity context (you will need to code this)
        Locale locale = new Locale(lang, "IN");
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
        sharedPreferences.edit().putString("Language", langu).apply();
    }
}