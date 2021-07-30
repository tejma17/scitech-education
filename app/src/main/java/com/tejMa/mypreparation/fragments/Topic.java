package com.tejMa.mypreparation.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tejMa.mypreparation.R;
import com.tejMa.mypreparation.activities.CustomQuizActivity;
import com.tejMa.mypreparation.activities.DoubtInfo;
import com.tejMa.mypreparation.activities.WebShow;
import com.tejMa.mypreparation.adapters.ListViewAdapter;
import com.tejMa.mypreparation.pojo.Chapters;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class Topic extends AppCompatActivity {

    private ListView listView;
    ArrayList<Chapters> names_test = new ArrayList<>();
    DatabaseReference databaseReference;
    ListViewAdapter adapter;
    SharedPreferences sharedPreferences;
    String lang, path;
    File file;
    boolean doubts = false;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);

        ActivityCompat.requestPermissions(Topic.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        names_test.clear();
        listView = findViewById(R.id.list);

        sharedPreferences = getSharedPreferences("Language", MODE_PRIVATE);
        lang = sharedPreferences.getString("Language", "English");

        if(Objects.requireNonNull(lang).equals("मराठी")){
            setLocale("mr", "मराठी");
        } else
            setLocale("en", "English");

        Intent intent = getIntent();
        String[] info;
        path = Objects.requireNonNull(Objects.requireNonNull(intent.getExtras()).getString("ChapName"));
        info = path.split("TEJMA");

        actionBar.setTitle(info[3]);


        //adapter setup
        if(info[0].equals("Doubts")) {
            adapter = new ListViewAdapter(this, R.layout.doubt_list_layout, names_test);
            doubts = true;
        }
        else
            adapter = new ListViewAdapter(this, R.layout.topic_list_layout, names_test);
        listView.setAdapter(adapter);

        //database access
        databaseReference = FirebaseDatabase.getInstance().getReference(info[0]).child(info[1]).child(info[2]).child(info[3]);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                String value = dataSnapshot.getValue().toString();
                int isNew = 0;
                if(value.startsWith("(NEW)")) {
                    value = value.replace("(NEW)", "");
                    isNew = 1;
                }

                Chapters chap1;
                if(key.startsWith("@@")){
                    String tempKey = key.replace("@@", "");
                    chap1 = new Chapters(tempKey, 1, value);
                } else
                    chap1 = new Chapters(key, 0, value);

                chap1.setNew(isNew);

                if(doubts) {
                    String answ = Objects.requireNonNull(dataSnapshot.child("ans").getValue()).toString();
                    answ = answ.replaceAll("\n", "\n");
                    answ = answ.replaceAll("_dot_", "\u25CF");
                    chap1 = new Chapters(dataSnapshot.getKey(), answ, 0);
                }

                if(!key.equals("new"))
                    names_test.add(chap1);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //listView actions
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Chapters chap = (Chapters) listView.getItemAtPosition(position);
            String link = chap.getDescription();
            String name = chap.getName();
            if(info[0].equals("Notes")){
                if(isFilePresent(info[3]+"_"+name)){
                    Intent i = new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(Topic.this, "com.tejMa.mypreparation", file));
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(i);
                }else{
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(browserIntent);
                }
            }else if(info[0].equals("Videos")){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserIntent);
            }
            else if(doubts){
                Intent d = new Intent(getApplicationContext(), DoubtInfo.class);
                d.putExtra("DoubtID", info[3]+"TEJMA"+name);
                startActivity(d);
            }
            else if(chap.getType()==1){
                Intent intent1 = new Intent(getApplicationContext(), CustomQuizActivity.class);
                intent1.putExtra("PATH", path+"TEJMA@@"+name);
                startActivity(intent1);
            } else {
                Intent intent1 = new Intent(getApplicationContext(), WebShow.class);
                intent1.putExtra("TestID", name+"TEJMA"+link);
                startActivity(intent1);
            }
        });

    }

    public boolean isFilePresent(String fileName) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName + ".pdf";
        file = new File(path);
        return file.exists();
    }

    //menu actions
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return true;
    }

    //action bar activities
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_navigation, menu);

        //searchView
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        MenuItem help = menu.findItem(R.id.help);
        MenuItem qr = menu.findItem(R.id.qr);
        qr.setVisible(false);
        help.setVisible(false);

        //customize search
        ImageView searchIcon= searchView.findViewById(androidx.appcompat.R.id.search_button);
        ImageView closeIcon= searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        TextView name = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        name.setHintTextColor(getResources().getColor(R.color.text_hint));
        name.setTextColor(getResources().getColor(R.color.set_text));
        searchIcon.setColorFilter(getResources().getColor(R.color.set_text), android.graphics.PorterDuff.Mode.SRC_IN);
        closeIcon.setColorFilter(getResources().getColor(R.color.set_text), android.graphics.PorterDuff.Mode.SRC_IN);
        searchView.setMaxWidth(Integer.MAX_VALUE);

        //filter searches
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)) {
                    adapter.filter("");
                    listView.clearTextFilter();
                }
                else {
                    adapter.filter(newText);
                }
                return true;
            }
        });
        return true;
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
