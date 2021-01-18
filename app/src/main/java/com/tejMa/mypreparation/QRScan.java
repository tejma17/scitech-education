package com.tejMa.mypreparation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.Result;

import java.util.Objects;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScan extends AppCompatActivity implements ZXingScannerView.ResultHandler  {

    Vibrator vibrator;
    ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_scan);


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if(checkCamera() != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(QRScan.this, new String[]{Manifest.permission.CAMERA}, 101);
            }
        }

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.app_name);
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Scan QR");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);


        scannerView = findViewById(R.id.surfaceView);
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        scannerView.startCamera();
        scannerView.setFocusableInTouchMode(true);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }


    @Override
    public void handleResult(Result result) {
        BottomSheetDialog builder = new BottomSheetDialog(QRScan.this);
        builder.setContentView(R.layout.search_layout);
        Button search = builder.findViewById(R.id.go);
        TextView query = builder.findViewById(R.id.info);
        query.setText(result.getText());
        builder.show();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getText()));
                startActivity(browserIntent);
                builder.dismiss();
            }
        });
        scannerView.setSoundEffectsEnabled(true);
        vibrator.vibrate(300);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                }  else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(QRScan.this);
                    builder.setTitle("Permission Required")
                            .setMessage("Camera permission is required to scan the QR. Please grant permission")
                            .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(QRScan.this, new String[]{Manifest.permission.CAMERA}, 101);
                                }
                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            QRScan.this.finish();
                        }
                    }).show();
                }
        }
    }

    private int checkCamera () {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
    }
}