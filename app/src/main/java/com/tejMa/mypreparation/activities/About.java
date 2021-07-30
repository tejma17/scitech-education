package com.tejMa.mypreparation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.tejMa.mypreparation.R;

import java.util.Objects;

public class About extends AppCompatActivity implements View.OnClickListener {

    ImageView fb, tw, in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle(R.string.contact);
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);

        fb = findViewById(R.id.fb);
        tw = findViewById(R.id.tw);
        in = findViewById(R.id.ins);

        fb.setOnClickListener(this);
        tw.setOnClickListener(this);
        in.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String url = (String)v.getTag();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);

        //pass the url to intent data
        intent.setData(Uri.parse(url));

        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }
}