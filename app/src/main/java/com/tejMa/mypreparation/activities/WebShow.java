package com.tejMa.mypreparation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.animation.Animator;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.tejMa.mypreparation.R;

import java.util.Locale;
import java.util.Objects;

public class WebShow extends AppCompatActivity {

    String[] topicDetail;
    String url;
    WebView webView;
    TextView timer;
    int back = 0;
    boolean doubleBackToExitPressedOnce = false;
    LottieAnimationView animationView, load;
    Animation anim;
    CountDownTimer countDownTimer;
    SharedPreferences sharedPreferences;
    String lang;
    String remaining;

    private String TAG = "Ads";
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        webView = findViewById(R.id.webView);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        timer = findViewById(R.id.timer);
        animationView = findViewById(R.id.anim);
        load = findViewById(R.id.loading);


        adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(WebShow.this,"ca-app-pub-3015278233978080/8759945120", adRequest, new InterstitialAdLoadCallback() {
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
                        finish();
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

        sharedPreferences = getSharedPreferences("Language", MODE_PRIVATE);
        lang = sharedPreferences.getString("Language", "English");

        if(Objects.requireNonNull(lang).equals("मराठी")){
            setLocale("mr", "मराठी");
        } else
            setLocale("en", "English");

        Intent intent = getIntent();
        topicDetail = Objects.requireNonNull(Objects.requireNonNull(intent.getExtras()).getString("TestID")).split("TEJMA");
        url = topicDetail[1];
        actionBar.setTitle(topicDetail[0]);
        anim = blinkAnim();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new AppWebViewClients(load));
        webView.loadUrl(url);

        if(topicDetail[0].equals("Exam") || topicDetail[0].equals("परीक्षा")){
            actionBar.setDisplayHomeAsUpEnabled(false);
            timer.setVisibility(View.VISIBLE);
            int time = Integer.parseInt(topicDetail[2]);
            countDownTimer = new CountDownTimer(time*1000, 1000) {

                public void onTick(long millisUntilFinished) {
                    updateTimer((int) millisUntilFinished/1000);
                }

                public void onFinish() {
                   animationView.setVisibility(View.VISIBLE);
                   timer.setText(R.string.test_finish);
                   remaining = "0";
                   timer.setTextColor(getResources().getColor(R.color.online));
                }

            }.start();
        }

        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(remaining.equals("0"))
                    onBackPressed();
                else
                    animationView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }


    public void updateTimer(int sec){
        int minutes = sec/60;
        int seconds = sec - minutes*60;
        String seco = Integer.toString(seconds);
        String minu = Integer.toString(minutes);
        if(seconds < 10)
            seco = "0"+seconds;
        if(minutes < 10)
            minu = "0"+minutes;
        remaining = minu+":"+seco;
        if(sec <= 30) {
            timer.setTextColor(Color.RED);
        }
        if (sec <= 15) {
            timer.startAnimation(anim);
            String rem = getResources().getString(R.string.submit_within);
            timer.setText(String.format(rem, remaining));
        }
         else{
            String tim = getResources().getString(R.string.time_left);
            timer.setText(String.format(tim, remaining));
        }
    }

    public static Animation blinkAnim() {
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(250);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(1  );
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(WebShow.this);
        } else {
            if(back==1)
                finish();
            else
                Toast.makeText(this, "Press back again", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
            back++;
        }

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

    public class AppWebViewClients extends WebViewClient {
        private LottieAnimationView animationView;

        public AppWebViewClients(LottieAnimationView animationView) {
            this.animationView=animationView;
            animationView.setVisibility(View.VISIBLE);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            if(topicDetail[0].equals("Exam") || topicDetail[0].equals("परीक्षा")) {
                animationView.setVisibility(View.VISIBLE);
                countDownTimer.cancel();
                timer.setText(R.string.test_finish);
                timer.setTextColor(getResources().getColor(R.color.online));
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            animationView.setVisibility(View.GONE);
        }
    }
}