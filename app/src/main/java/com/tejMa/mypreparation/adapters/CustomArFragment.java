package com.tejMa.mypreparation.adapters;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;
import com.tejMa.mypreparation.R;

import java.io.IOException;
import java.io.InputStream;

public class CustomArFragment extends ArFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout = (FrameLayout) super.onCreateView(inflater, container, savedInstanceState);

        getPlaneDiscoveryController().hide();
        getPlaneDiscoveryController().setInstructionView(null);

        return frameLayout;
    }

    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);

        config.setFocusMode(Config.FocusMode.AUTO);


        InputStream dbStream = getResources().openRawResource(R.raw.imagedb);
        AugmentedImageDatabase aid = null;
        try {
            aid = AugmentedImageDatabase.deserialize(session, dbStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        config.setAugmentedImageDatabase(aid);

        this.getArSceneView().setupSession(session);

        return config;

    }
}
