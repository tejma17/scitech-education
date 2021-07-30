package com.tejMa.mypreparation.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tejMa.mypreparation.R;
import com.tejMa.mypreparation.activities.About;
import com.tejMa.mypreparation.activities.HelpSection;
import com.tejMa.mypreparation.adapters.ListViewAdapter;
import com.tejMa.mypreparation.pojo.Chapters;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Settings extends Fragment {

    private ListViewAdapter arrayAdapter;
    View view;
    ArrayList<Chapters> demo;
    AlertDialog.Builder builder;
    SharedPreferences sharedPreferences;
    String medium, std, theme, feed, contact, rate, share;
    String[] values, settings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_settings, container, false);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle(R.string.setting_tab);
        setHasOptionsMenu(true);

        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        ListView listView = view.findViewById(R.id.settingView);

        sharedPreferences = requireActivity().getSharedPreferences("Language", Context.MODE_PRIVATE);

        demo = new ArrayList<>();
        demo.clear();
        arrayAdapter = new ListViewAdapter(getActivity(), R.layout.setting_list_layout, demo);
        listView.setAdapter(arrayAdapter);
        getStrings();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            final String[] language = {"English", "मराठी"};
            final  String[] classes = {"10", "9", "8"};
            if(position == 0)
            {
                builder = new AlertDialog.Builder(requireContext(), R.style.MyDialogStyle);
                builder.setItems(language, (dialog, which) -> {
                    medium = language[which];
                    demo.get(position).setDescription(medium);
                    arrayAdapter.notifyDataSetChanged();
                    if(language[which].equals("English")){
                        setLocale("en", "English");
                    } else{
                        setLocale("mr", "मराठी");
                    }
                    getActivity().recreate();
                    ((AppCompatActivity)getContext()).getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }).show();
            }
            if(position == 1)
            {
                builder = new AlertDialog.Builder(requireContext(), R.style.MyDialogStyle);
                builder.setItems(classes, (dialog, which) -> {
                    std = classes[which];
                    demo.get(position).setDescription(std);
                    arrayAdapter.notifyDataSetChanged();
                    sharedPreferences.edit().putString("Class", classes[which]).apply();
                }).show();
            }
//            else if(position == 2)
//            {
//                builder = new AlertDialog.Builder(requireContext(), R.style.MyDialogStyle);
//                builder.setItems(mode, (dialog, which) -> {
//                    if(which == 1)
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                    else if(which == 0)
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                    else if(which == 2)
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//                    theme = mode[which];
//                    sharedPreferences.edit().putString("Theme", mode[which]).apply();
//                    demo.get(position).setDescription(theme);
//                    arrayAdapter.notifyDataSetChanged();
//                }).show();
//
//            }
            else if(position == 2)
            {

                final Dialog builder = new Dialog(requireContext());
                builder.setContentView(R.layout.alert_dialog);
                final EditText text = builder.findViewById(R.id.dialog_text);
                final EditText name = builder.findViewById(R.id.name);
                final EditText version = builder.findViewById(R.id.version);
                Button positive = builder.findViewById(R.id.positive);
                Button negative = builder.findViewById(R.id.negative);

                positive.setOnClickListener(v -> {
                    if(isAlpha(name.getText().toString().trim())) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Bugs");
                        reference.child(name.getText().toString().trim()).setValue(version.getText().toString().trim() + "//" + text.getText().toString().trim());
                        Toast.makeText(getContext(), R.string.report_submit, Toast.LENGTH_SHORT).show();
                        builder.cancel();
                    }else{
                        Toast.makeText(getContext(), R.string.name_erro, Toast.LENGTH_SHORT).show();
                    }
                });

                negative.setOnClickListener(v -> builder.cancel());
                builder.show();
            }
            else if(position == 3)
            {
                startActivity(new Intent(getContext(), About.class));
            }
            else if (position == 4){
                Uri uri = Uri.parse("market://details?id=" + requireContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                }
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + requireContext().getPackageName())));
                }
            }
            else if (position == 5){
                shareIt();
            }
        });

        return view;
    }

    private void setLocale(String lang, String langu) {
        Locale locale = new Locale(lang, "IN");
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
        sharedPreferences.edit().putString("Language", langu).apply();
    }

    private void getStrings(){
        medium = sharedPreferences.getString("Language", "English");
        std = sharedPreferences.getString("Class", "10");
        feed = "";
        contact = "";
        rate = "";
        share = "";
        values = new String[]{medium, std, feed, contact, rate, share};
        settings = new String[]{
                getResources().getString(R.string.medium),
                getResources().getString(R.string.std),
                getResources().getString(R.string.feedback_and_suggestion),
                getResources().getString(R.string.contactus),
                getResources().getString(R.string.rate),
                getResources().getString(R.string.share)};
        for(int i=0; i<settings.length; i++){
            Chapters chapters = new Chapters(settings[i], values[i], -1);
            demo.add(chapters);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    public boolean isAlpha(String name) {
        Pattern pattern = Pattern.compile("^[a-zA-Z\\s]*$");
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_navigation, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem search = menu.findItem(R.id.search);
        search.setVisible(false);
        MenuItem qr = menu.findItem(R.id.qr);
        qr.setVisible(false);

        MenuItem help = menu.findItem(R.id.help);
        help.setOnMenuItemClickListener(item -> {
            startActivity(new Intent(getContext(), HelpSection.class));
            return true;
        });
    }

    private void shareIt() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "\nSciTech: Don't let this lockdown stop your learning. Learn online with SciTech\n\nDownload here:\nhttps://play.google.com/store/apps/details?id=com.tejMa.mypreparation\n";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}