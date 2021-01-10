package com.tejMa.mypreparation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tejMa.mypreparation.CustomArFragment;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ArScan extends AppCompatActivity {

    private ExternalTexture texture;
    private MediaPlayer mediaPlayer;
    private CustomArFragment arFragment;
    private Scene scene;
    private ModelRenderable renderable;
    private boolean isImageDetected = false;
    TextView msg;
    File file;
    String std;
    Map<String, String> umap = new HashMap<>();
    DatabaseReference database;
    LottieAnimationView animationView;
    SharedPreferences sharedPreferences;
    BroadcastReceiver onComplete;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ar_scan);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if(!checkCamera()){
                ActivityCompat.requestPermissions(ArScan.this, new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);
            }
        }

        sharedPreferences = getSharedPreferences("Language", Context.MODE_PRIVATE);
        msg = findViewById(R.id.msg);
        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_fragment);
        animationView = findViewById(R.id.scan);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            texture = new ExternalTexture();
            ModelRenderable
                    .builder()
                    .setSource(this, Uri.parse("video_screen.sfb"))
                    .build()
                    .thenAccept(modelRenderable -> {
                        modelRenderable.getMaterial().setExternalTexture("videoTexture",
                                texture);
                        modelRenderable.getMaterial().setFloat4("keyColor",
                                new Color(0.01843f, 1f, 0.098f));

                        renderable = modelRenderable;
                    });

            scene = arFragment.getArSceneView().getScene();
            scene.addOnUpdateListener(this::onUpdate);

        std = sharedPreferences.getString("Class", "English");
        database = FirebaseDatabase.getInstance().getReference("AR/" + std);
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                umap.put(snapshot.getKey(), snapshot.getValue().toString());
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

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onUpdate(FrameTime frameTime) {
        if (isImageDetected)
            return;

        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<AugmentedImage> augmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage image : augmentedImages) {
            if (image.getTrackingState() == TrackingState.TRACKING) {
                animationView.setVisibility(View.GONE);
                msg.setText("Figure detected!");
                isImageDetected = true;
                String name = image.getName().split("\\.")[0];

                if (isFilePresent(name)) {
                    msg.setText("Figure Detected");
                    playVideo(image.createAnchor(image.getCenterPose()), image.getExtentX(),
                            image.getExtentZ(), file);
                } else {
                    downloadFile(image, name);
                }

                break;
            }
        }
    }

    public boolean isFilePresent(String fileName) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/AR/" + fileName;
        file = new File(path);
        return file.exists();
    }


    @Override
    public void onBackPressed() {
        if(isImageDetected) {
            mediaPlayer.stop();
            Intent i = new Intent(getApplicationContext(), ArScan.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        else
            super.onBackPressed();
    }


    public void downloadFile(AugmentedImage image, String name){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading the effect...");
        progressDialog.setMessage("Thank you for your patience.");
        progressDialog.show();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(umap.get(name)));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle("SciTech AR File");
        request.setDescription("Downloading File");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM, "/AR/"+name);

        DownloadManager manager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/AR/" + name;
                file = new File(path);
                Toast.makeText(ctxt, "Download Completed", Toast.LENGTH_SHORT).show();
                msg.setVisibility(View.GONE);
                progressDialog.cancel();
                playVideo (image.createAnchor(image.getCenterPose()), image.getExtentX(),
                        image.getExtentZ(), file);
            }
        };

    }




    private void playVideo(Anchor anchor, float extentX, float extentZ, File path) {
        mediaPlayer = MediaPlayer.create(this, Uri.fromFile(path));
        mediaPlayer.setSurface(texture.getSurface());
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        AnchorNode anchorNode = new AnchorNode(anchor);

        texture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
            anchorNode.setRenderable(renderable);
            texture.getSurfaceTexture().setOnFrameAvailableListener(null);
        });

        anchorNode.setWorldScale(new Vector3(extentX, 1f, extentZ));

        scene.addChild(anchorNode);
    }

    private boolean checkCamera () {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

}