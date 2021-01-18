package com.tejMa.mypreparation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tejMa.mypreparation.adapters.ListViewAdapter;
import com.tejMa.mypreparation.pojo.Chapters;

import java.util.ArrayList;
import java.util.Objects;

public class Video extends Fragment {

    ListView video_list;
    LottieAnimationView coming;
    DatabaseReference reference;
    public ArrayList<Chapters> demo;
    private ListViewAdapter adapter;
    LottieAnimationView animationView;
    SharedPreferences sharedPreferences;
    String std, lang;
    View view;
    private Button notes, videos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_notes, container, false);
        ActionBar actionBar = ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle(R.string.video_lectures);
        setHasOptionsMenu(true);

        video_list = view.findViewById(R.id.notes);
        coming = view.findViewById(R.id.coming);
        demo = new ArrayList<>();
        notes = view.findViewById(R.id.note_button);
        videos = view.findViewById(R.id.video_button);
        animationView = view.findViewById(R.id.anim);
        animationView.setVisibility(View.VISIBLE);
        coming.setVisibility(View.GONE);
        adapter = new ListViewAdapter(Objects.requireNonNull(getContext()), R.layout.chap_list_layout, demo);
        video_list.setAdapter(adapter);

        //check connection
        if(isConnection()) {
            animationView.setAnimation(R.raw.load);
        } else{
            animationView.setAnimation(R.raw.no_connection);
            coming.setVisibility(View.GONE);
        }

        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,
                                new ShowNotes()).commit();
                notes.setTextColor(getResources().getColor(R.color.select));
                videos.setTextColor(getResources().getColor(R.color.set_text));
            }
        });

        videos.setTextColor(getResources().getColor(R.color.select));
        videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,
                                new Video()).commit();
                videos.setTextColor(getResources().getColor(R.color.select));
                notes.setTextColor(getResources().getColor(R.color.set_text));
            }
        });


        sharedPreferences = getActivity().getSharedPreferences("Language", Context.MODE_PRIVATE);
        lang = sharedPreferences.getString("Language", "English");
        std = sharedPreferences.getString("Class", "10");

        reference = FirebaseDatabase.getInstance().getReference("Videos/"+std+"/"+lang);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 0) {
                    animationView.setVisibility(View.INVISIBLE);
                    coming.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        final boolean marathi = lang.equals("मराठी");

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Chapters chap1;
                if(marathi){
                    if(dataSnapshot.getChildrenCount() == 1)
                        chap1 = new Chapters(dataSnapshot.getKey(),dataSnapshot.getChildrenCount()+" तासिका");
                    else
                        chap1 = new Chapters(dataSnapshot.getKey(),dataSnapshot.getChildrenCount()+" तासिका");
                }else {
                    if(dataSnapshot.getChildrenCount() == 1)
                        chap1 = new Chapters(dataSnapshot.getKey(),dataSnapshot.getChildrenCount()+" Lecture");
                    else
                        chap1 = new Chapters(dataSnapshot.getKey(),dataSnapshot.getChildrenCount()+" Lectures");
                }
                demo.add(chap1);
                adapter.notifyDataSetChanged();
                animationView.setVisibility(View.INVISIBLE);
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

        video_list.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getContext(), Topic.class);
            Chapters chap = (Chapters) video_list.getItemAtPosition(position);
            String Chapter = chap.getName();
            intent.putExtra("ChapName", "VideosTEJMA"+std+"TEJMA"+lang+"TEJMA"+Chapter);
            startActivity(intent);
        });
        return view;
    }

    public boolean isConnection(){
        ConnectivityManager connectivityManager = ((ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_navigation, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.search);
        MenuItem help = menu.findItem(R.id.help);
        MenuItem qr = menu.findItem(R.id.qr);
        qr.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getContext(), QRScan.class));
                return true;
            }
        });
        help.setVisible(false);

        SearchView searchView = (SearchView) item.getActionView();

        ImageView searchIcon= searchView.findViewById(androidx.appcompat.R.id.search_button);
        ImageView closeIcon= searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        TextView name = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        name.setHintTextColor(getResources().getColor(R.color.text_hint));
        name.setTextColor(getResources().getColor(R.color.set_text));
        searchIcon.setColorFilter(getResources().getColor(R.color.set_text), android.graphics.PorterDuff.Mode.SRC_IN);
        closeIcon.setColorFilter(getResources().getColor(R.color.set_text), android.graphics.PorterDuff.Mode.SRC_IN);
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)) {
                    adapter.filter("");
                    video_list.clearTextFilter();
                }
                else {
                    adapter.filter(newText);
                }
                return true;
            }
        });

    }
}