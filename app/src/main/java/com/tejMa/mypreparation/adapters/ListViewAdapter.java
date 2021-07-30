package com.tejMa.mypreparation.adapters;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.tejMa.mypreparation.R;
import com.tejMa.mypreparation.pojo.Chapters;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    Context context;
    ArrayList<Chapters> objects;
    int res;
    ArrayList<Chapters> chapterlist;
    LayoutInflater inflater;

    public ListViewAdapter(@NonNull Context context, int resource, ArrayList<Chapters> objects) {
        this.context = context;
        this.objects = objects;
        res = resource;
        inflater = LayoutInflater.from(context);
        this.chapterlist = new ArrayList<>();
    }

    public static class ViewHolder{
        TextView title, description;
        ImageView adView;
        LottieAnimationView newLabel;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Nullable
    @Override
    public Chapters getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(res, null);

            holder.title = convertView.findViewById(R.id.title);
            holder.description = convertView.findViewById(R.id.description);
            holder.newLabel = convertView.findViewById(R.id.new_label);
            holder.adView = convertView.findViewById(R.id.qureka_ad);

            chapterlist.clear();
            chapterlist.addAll(objects);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(objects.get(position).getName());
        holder.description.setText(Html.fromHtml(objects.get(position).getDescription()));

        if(objects.get(position).getName().equals("This is an ad")) {
            holder.adView.setVisibility(View.VISIBLE);
            holder.adView.setImageResource(objects.get(position).getImage());
        } else {
            holder.adView.setVisibility(View.INVISIBLE);
        }


        if(objects.get(position).isNew()!=-1) {
            if (objects.get(position).isNew() == 1)
                holder.newLabel.setVisibility(View.VISIBLE);
            else
                holder.newLabel.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void filter(String query){
        query = query.toLowerCase().trim();
        objects.clear();
        if(query.length() == 0)
        {
            objects.addAll(chapterlist);
        }
        else
        {
            for(Chapters chapter : chapterlist){
                if(chapter.getName().toLowerCase().contains(query)){
                    objects.add(chapter);
                }
            }
        }
        notifyDataSetChanged();
    }
}
