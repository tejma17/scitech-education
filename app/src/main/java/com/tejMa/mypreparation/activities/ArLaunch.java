package com.tejMa.mypreparation.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.ar.core.ArCoreApk;
import com.tejMa.mypreparation.R;

public class ArLaunch extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ar_launch);


        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isSupported() && (Build.VERSION.SDK_INT >= 24)) {
                new Handler().postDelayed(() -> {
                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<ImageView, String>(findViewById(R.id.arName), "arName");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ArLaunch.this, pairs);
                    startActivity(new Intent(ArLaunch.this, ArScan.class), options.toBundle());
                    finish();
                },4000);
        } else {
            new AlertDialog.Builder(ArLaunch.this)
                    .setTitle("Devices not supported")
                    .setMessage("This feature requires android version 7.0 (N)+ with ARCore support.")
                    .setPositiveButton("GET ARCore", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.ar.core");
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(goToMarket);
                            finish();
                            ArLaunch.this.finish();
                        }
                    })
                    .setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}