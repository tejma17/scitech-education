package com.tejMa.mypreparation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.Result;

import java.util.Objects;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScan extends AppCompatActivity implements ZXingScannerView.ResultHandler  {

    Vibrator vibrator;
    ZXingScannerView scannerView;
    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_scan);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.app_name);
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Scan QR");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);


        scannerView = findViewById(R.id.surfaceView);
        textView = findViewById(R.id.info);
        textView.setText("");
        button = findViewById(R.id.go);
        button.setVisibility(View.GONE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(textView.getText().toString()));
                startActivity(browserIntent);
            }
        });

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
        textView.setText("");
        button.setVisibility(View.GONE);
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }


    @Override
    public void handleResult(Result result) {
        scannerView.setSoundEffectsEnabled(true);
        vibrator.vibrate(300);
        textView.setText(result.getText());
        button.setVisibility(View.VISIBLE);
    }
}