package com.tejMa.mypreparation.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.tejMa.mypreparation.R;
import com.tejMa.mypreparation.pojo.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder>{

    List<Question> questions;
    Context context;
    private final OnClickListener onClickListener;
    LayoutInflater inflater;

    public QuestionsAdapter(@NonNull Context context, List<Question> questions, OnClickListener onClickListener) {
        this.context = context;
        this.questions = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_layout, parent, false);
        return new ViewHolder(view, onClickListener);
    }

    public void updateAdapter(List<Question> newList){
        this.questions = newList;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.questionTitle.setText(questions.get(position).getQuestion());
        holder.marks.setText(String.valueOf(questions.get(position).getMaxMarks()));


        holder.options.removeAllViews();
        final MaterialRadioButton[] rb = new MaterialRadioButton[questions.get(position).getOptions().length];
        createOptions(rb, holder.options, questions.get(position));


        holder.options.setOnCheckedChangeListener((group, checkedId) -> {
            if (group.getCheckedRadioButtonId() == -1)
            {
                questions.get(position).setTickedAns(-1);
                holder.clear.setVisibility(View.GONE);
            }
            else
            {
                questions.get(position).setTickedAns(checkedId);
                holder.clear.setVisibility(View.VISIBLE);
            }
        });

        if(questions.get(position).getTickedAns()!=-1) {
            holder.options.check(questions.get(position).getTickedAns());
            if(questions.get(position).getTickedAns()==questions.get(position).getCorrectAns()) {
                holder.card.setBackgroundColor(context.getResources().getColor(R.color.right));
            } else {
                holder.card.setBackgroundColor(context.getResources().getColor(R.color.wrong));
            }
        }

        holder.clear.setOnClickListener(view ->{
            holder.options.clearCheck();
        });

        if(questions.get(position).getImg().equals("no"))
            holder.img.setVisibility(View.GONE);
        else {
            Glide.with(context).load(questions.get(position).getImg())
                    .placeholder(R.drawable.loading)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                    .into(holder.img);
            holder.img.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return questions.size();
    }


    private void createOptions(MaterialRadioButton[] rb, RadioGroup rg, Question question) {
        rg.setOrientation(RadioGroup.VERTICAL);
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled}, //disabled
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[] {Color.BLACK, Color.DKGRAY}
        );

        for(int i=0; i<question.getOptions().length; i++){
            rb[i]  = new MaterialRadioButton(context);
            rb[i].setText(question.getOptions()[i]);
            rb[i].setMinimumHeight(0);
            rb[i].setButtonTintList(colorStateList);
            rb[i].setId(i+1);
            if(question.getTickedAns()==rb[i].getId())
                rb[i].setChecked(true);
            rg.addView(rb[i]);
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView questionTitle, marks, clear;
        RadioGroup options;
        ImageView img;
        OnClickListener clickListener;
        CardView card;

         public ViewHolder(View itemView, OnClickListener OnClickListener){
             super(itemView);
             this.questionTitle = itemView.findViewById(R.id.question);
             this.marks = itemView.findViewById(R.id.marks);
             this.clear = itemView.findViewById(R.id.clear);
             this.options = itemView.findViewById(R.id.options);
             this.card = itemView.findViewById(R.id.background);
             this.img = itemView.findViewById(R.id.img);
             this.clickListener = OnClickListener;
             itemView.setOnClickListener(this);
             itemView.setOnLongClickListener(this);
         }




        @Override
        public void onClick(View v) {
            clickListener.onClick(getLayoutPosition(), v);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onLongClick(getLayoutPosition());
            return true;
        }
    }

    public interface OnClickListener{
        void onClick(int position, View v);
        void onLongClick(int position);
    }

}
