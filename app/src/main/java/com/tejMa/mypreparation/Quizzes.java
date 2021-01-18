package com.tejMa.mypreparation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;
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

import static android.app.Activity.RESULT_OK;

public class Quizzes extends Fragment {

    ListView chap_listView;
    int REQUEST_CODE = 0;
    LottieAnimationView coming;
    DatabaseReference db;
    public ArrayList<Chapters> demo;
    LottieAnimationView animationView;
    private String language, std;
    private ListViewAdapter adapter;
    private ReviewManager reviewManager;
    SharedPreferences sharedPreferences;
    View view;

    ReviewInfo reviewInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_quizzes, container, false);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        ActionBar actionBar = ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle(R.string.quiz);
        setHasOptionsMenu(true);

        //set up views
        chap_listView = view.findViewById(R.id.listChap);
        coming = view.findViewById(R.id.coming);
        animationView = view.findViewById(R.id.anim);
        animationView.setVisibility(View.VISIBLE);
        coming.setVisibility(View.GONE);
        demo = new ArrayList<>();
        demo.clear();


        //review manager
        reviewManager = ReviewManagerFactory.create(getActivity());
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull Task<ReviewInfo> task) {
                if (task.isSuccessful()) {
                    // We can get the ReviewInfo object
                    reviewInfo = task.getResult();
                    getRating();
                }
            }
        });

        adapter = new ListViewAdapter(Objects.requireNonNull(getContext()), R.layout.chap_list_layout, demo);
        chap_listView.setAdapter(adapter);
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

        //get settings
        sharedPreferences = getActivity().getSharedPreferences("Language", Context.MODE_PRIVATE);
        language = sharedPreferences.getString("Language", "English");
        std = sharedPreferences.getString("Class", "10");

        //get data
        db = FirebaseDatabase.getInstance().getReference("Quiz/"+std+"/"+language);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
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

        final boolean marathi = language.equals("मराठी");

        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Chapters chap1;
                if(marathi){
                    if(dataSnapshot.getChildrenCount() == 1)
                        chap1 = new Chapters(dataSnapshot.getKey(),dataSnapshot.getChildrenCount()+" चाचणी");
                    else
                        chap1 = new Chapters(dataSnapshot.getKey(),dataSnapshot.getChildrenCount()+" चाचण्या");
                }else {
                    if(dataSnapshot.getChildrenCount() == 1)
                        chap1 = new Chapters(dataSnapshot.getKey(),dataSnapshot.getChildrenCount()+" Test");
                    else
                        chap1 = new Chapters(dataSnapshot.getKey(),dataSnapshot.getChildrenCount()+" Tests");
                }
                demo.add(chap1);
                adapter.notifyDataSetChanged();
                coming.setVisibility(View.GONE);
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

        //list actions
        chap_listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getContext(), Topic.class);
            Chapters chap = (Chapters) chap_listView.getItemAtPosition(position);
            String Chapter = chap.getName();
            intent.putExtra("ChapName", "QuizTEJMA"+std+"TEJMA"+language+"TEJMA"+Chapter);
            startActivity(intent);
        });

        //In-app rating


        //check updates
        final AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(getActivity());
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(result -> {
            if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                try {
                    appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, getActivity(), REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            }
        });

        return view;

    }

    private void getRating() {
        Task<Void> flow = reviewManager.launchReviewFlow(getActivity(), reviewInfo);
        flow.addOnCompleteListener(task -> {
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (requestCode != RESULT_OK) {
                Log.d("TAG", "Update failed" + requestCode);
            }
        }
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
                    chap_listView.clearTextFilter();
                }
                else {
                    adapter.filter(newText);
                }
                return true;
            }
        });

    }

    public boolean isConnection(){
        ConnectivityManager connectivityManager = ((ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


}