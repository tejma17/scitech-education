package com.tejMa.mypreparation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class Doubts extends Fragment {

    ListView doubt_list;
    LottieAnimationView coming;
    DatabaseReference reference;
    public ArrayList<Chapters> demo;
    private ListViewAdapter adapter;
    LottieAnimationView animationView;
    SharedPreferences sharedPreferences;
    String std, lang;
    FloatingActionButton ask;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_doubts, container, false);
        ActionBar actionBar = ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle(R.string.doubts);
        setHasOptionsMenu(true);

        doubt_list = view.findViewById(R.id.notes);
        coming = view.findViewById(R.id.coming);
        demo = new ArrayList<>();
        animationView = view.findViewById(R.id.anim);
        animationView.setVisibility(View.VISIBLE);
        coming.setVisibility(View.GONE);
        adapter = new ListViewAdapter(Objects.requireNonNull(getContext()), R.layout.chap_list_layout, demo);
        doubt_list.setAdapter(adapter);
        ask = view.findViewById(R.id.ask);
        TextView poor;
        poor = view.findViewById(R.id.poor);



        //check connection
        if(isConnection()) {
            animationView.setAnimation(R.raw.load);
        } else{
            new Handler().postDelayed(() -> {
                poor.setVisibility(View.VISIBLE);
            },10000);
            animationView.setAnimation(R.raw.no_connection);
            coming.setVisibility(View.GONE);
        }

        //ask doubt
        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(ask, "newDoubt");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pairs);
                startActivity(new Intent(getContext(), AskDoubt.class), options.toBundle());
            }
        });

        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("Language", Context.MODE_PRIVATE);

        int count = sharedPreferences.getInt("Count", 0);
        if(count == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogStyle);
            builder.setTitle(getResources().getString(R.string.doubt_session));
            builder.setMessage(getResources().getString(R.string.warn_doubt));
            builder.setNegativeButton(getResources().getString(R.string.gotit),new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
            count++;
            sharedPreferences.edit().putInt("Count", count).apply();
        }


        sharedPreferences = getActivity().getSharedPreferences("Language", Context.MODE_PRIVATE);
        lang = sharedPreferences.getString("Language", "English");
        std = sharedPreferences.getString("Class", "10");

        reference = FirebaseDatabase.getInstance().getReference("Doubts/"+std+"/"+lang);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 0) {
                    animationView.setVisibility(View.INVISIBLE);
                    coming.setVisibility(View.VISIBLE);
                    poor.setVisibility(View.GONE);
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
                    chap1 = new Chapters(dataSnapshot.getKey(),dataSnapshot.getChildrenCount()+" शंका");
                }else {
                    if(dataSnapshot.getChildrenCount() == 1)
                        chap1 = new Chapters(dataSnapshot.getKey(),dataSnapshot.getChildrenCount()+" Thread");
                    else
                        chap1 = new Chapters(dataSnapshot.getKey(),dataSnapshot.getChildrenCount()+" Threads");
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

        doubt_list.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getContext(), Topic.class);
            Chapters chap = (Chapters) doubt_list.getItemAtPosition(position);
            String Chapter = chap.getName();
            intent.putExtra("ChapName", "DoubtsTEJMA"+std+"TEJMA"+lang+"TEJMA"+Chapter);
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
        help.setVisible(false);
        MenuItem qr = menu.findItem(R.id.qr);
        qr.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getContext(), QRScan.class));
                return true;
            }
        });


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
                    doubt_list.clearTextFilter();
                }
                else {
                    adapter.filter(newText);
                }
                return true;
            }
        });

    }
}