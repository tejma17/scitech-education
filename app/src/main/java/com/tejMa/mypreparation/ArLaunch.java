package com.tejMa.mypreparation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.google.ar.core.ArCoreApk;

public class ArLaunch extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_launch);


        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isSupported() && (Build.VERSION.SDK_INT >= 24)) {
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(this, ArScan.class));
                    finish();
                },4000);
        } else {
//            new AlertDialog.Builder(ArLaunch.this)
//                    .setTitle("Devices not supported")
//                    .setMessage("This feature requires android version 7.0 (N)+ with ARCore support")
//                    .setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .show();
        }
    }
}