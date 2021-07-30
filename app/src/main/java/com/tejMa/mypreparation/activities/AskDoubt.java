package com.tejMa.mypreparation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tejMa.mypreparation.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AskDoubt extends AppCompatActivity {

    private Button chapter;
    private EditText question;
    private String selected_chap = "NOPE";
    String[] chapter_list;
    SharedPreferences sharedPreferences;
    private String TAG = "Ads";
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_doubt);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Ask Doubt");
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);

        Integer[] ad_images = new Integer[]{R.drawable.quiz1, R.drawable.quiz2, R.drawable.quiz3, R.drawable.quiz4, R.drawable.quiz5};
        List<Integer> intList = Arrays.asList(ad_images);
        Collections.shuffle(intList);
        intList.toArray(ad_images);
        //ads initialise
        AdView mAdView = findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        adRequest = new AdRequest.Builder().build();

        findViewById(R.id.ad).setBackgroundResource(ad_images[0]);
        findViewById(R.id.ad).setOnClickListener(v -> {
            String urlString = "https://312.win.qureka.com";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.chrome");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                // Chrome browser presumably not installed so allow user to choose instead
                intent.setPackage(null);
                startActivity(intent);
            }
        });

        InterstitialAd.load(AskDoubt.this,"ca-app-pub-3015278233978080/8759945120", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        finish();
                        Log.d("TAG", "The ad was dismissed.");
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {

                        mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
                Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });




        Button submit = findViewById(R.id.submit_doubt);
        chapter = findViewById(R.id.chapter);
        question = findViewById(R.id.question);


        sharedPreferences = getSharedPreferences("Language", MODE_PRIVATE);
        String lang = sharedPreferences.getString("Language", "English");
        String std = sharedPreferences.getString("Class", "10");

        chapter.setEnabled(false);

        if(Objects.requireNonNull(lang).equals("मराठी")){
            setLocale("mr", "मराठी");
        } else
            setLocale("en", "English");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chapters").child(Objects.requireNonNull(std)).child(lang);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    chapter_list = Objects.requireNonNull(snapshot.getValue()).toString().split("000");
                    chapter.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        chapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AskDoubt.this, R.style.MyDialogStyle);
                builder.setItems(chapter_list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected_chap = chapter_list[which];
                        chapter.setText(selected_chap);
                    }
                }).show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected_chap.equals("NOPE")) {
                    Toast.makeText(AskDoubt.this, getResources().getString(R.string.choose), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(question.getText().toString().trim().equals("")) {
                    Toast.makeText(AskDoubt.this, getResources().getString(R.string.empty), Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(AskDoubt.this, R.style.MyDialogStyle);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Pending");
                String value = question.getText().toString().trim()+"TEJMA"+std+"TEJMA"+lang;
                reference.child(selected_chap).push().setValue(value);
                builder.setTitle(R.string.suc_sumitted)
                        .setMessage(R.string.recorded)
                        .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                if (mInterstitialAd != null) {
                                    mInterstitialAd.show(AskDoubt.this);
                                } else {
                                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                                }
                            }
                        }).show();
            }
        });

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

    //menu actions
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return true;
    }
}