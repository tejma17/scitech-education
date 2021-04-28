package com.tejMa.mypreparation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tejMa.mypreparation.adapters.QuestionsAdapter;
import com.tejMa.mypreparation.pojo.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomQuizActivity extends AppCompatActivity implements QuestionsAdapter.OnClickListener {

    private RecyclerView questionRv;
    private QuestionsAdapter adapter;
    private Button submit;
    private float score, maxMarks;
    private List<Question> list;
    private String path;
    private String[] info;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_quiz);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);

        questionRv = findViewById(R.id.question_rv);
        submit = findViewById(R.id.submit);
        animationView = findViewById(R.id.anim);
        list = new ArrayList<>();

        animationView.setVisibility(View.VISIBLE);
        adapter = new QuestionsAdapter(this, list, this);
        questionRv.setAdapter(adapter);

        path = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).getString("PATH"));
        info = path.split("TEJMA");
        path = path.replaceAll("TEJMA", "/");

        actionBar.setTitle(info[4].replace("@@", ""));

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                animationView.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String ques = snapshot.child("ques").getValue().toString();
                int ans = Integer.parseInt(snapshot.child("ans").getValue().toString());
                float marks = Float.parseFloat(snapshot.child("marks").getValue().toString());
                String[] options = snapshot.child("options").getValue().toString().split("@#@");
                String img = snapshot.child("img").getValue().toString();
                Question question = new Question(ques, options, ans, marks, img);

                list.add(question);
                adapter.updateAdapter(list);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        submit.setOnClickListener(view->{
            score = 0;
            maxMarks = 0;
            for(int i=0; i<list.size(); i++) {
                Question question = list.get(i);
                if(question.getTickedAns()==question.getCorrectAns()) {
                    score += question.getMaxMarks();
                    question.setBackground(R.color.right);
                } else {
                    question.setBackground(R.color.wrong);
                }
                maxMarks += question.getMaxMarks();
            }
            adapter.updateAdapter(list);
        });


    }

    //menu actions
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return true;
    }

    @Override
    public void onClick(int position, View v) {

    }

    @Override
    public void onLongClick(int position) {

    }
}