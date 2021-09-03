package com.MemeGenerator.meme.creator.ui.activities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.MemeGenerator.meme.creator.databinding.ActivityMainBinding;
import com.MemeGenerator.meme.creator.utils.FbAdsUtils;
import com.google.firebase.messaging.FirebaseMessaging;
import com.MemeGenerator.meme.creator.R;
import com.facebook.ads.*;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private NavController navController;
    private Dialog dialog;
    public static final String TOPIC = "/topics/memes";
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AudienceNetworkAds.initialize(this);
        FbAdsUtils.LoadInterstitial(this);
        mAdView = FbAdsUtils.ShowBanner(this, findViewById(R.id.banner_container));

        subscribeTopic();

        initViews();
        binding.extendedFab.setOnClickListener(v -> {
           FbAdsUtils.ShowInterstitial(MainActivity.this);
            openCreateMemeActivity();
        });
        binding.btnShare.setOnClickListener(v -> shareApp());
        binding.btnRate.setOnClickListener(v -> rateUs());

    }




    private void initViews() {
        navController = Navigation.findNavController(this,
                R.id.nav_host_fragment);

        NavigationUI
                .setupWithNavController(binding.bottomNavigation, navController);
    }

    private void subscribeTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Log.d(TAG, "subscribed to: " + TOPIC);
                });
    }

    private void openCreateMemeActivity() {
        Intent intent = new Intent(this, EditMemeActivity.class);
        intent.putExtra("source_type", "custom");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_exit_dialog);
        dialog.setCanceledOnTouchOutside(false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Objects
                .requireNonNull(this.dialog.getWindow())
                .setLayout((int) (((double) displayMetrics.widthPixels) * 0.9d), -2);

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnExit = dialog.findViewById(R.id.btnExit);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnExit.setOnClickListener(v -> finish());

        dialog.show();
    }

    private void rateUs() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + getApplication().getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
        }
    }

    private void shareApp() {
        try {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name));
            intent.putExtra("android.intent.extra.TEXT", "\nLet me recommend you this application\nhttps://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            startActivity(Intent.createChooser(intent, "choose one"));
        } catch (Exception unused) {
            unused.printStackTrace();
            Toast.makeText(this, "Unknown error occurred. Restart app and try again!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}