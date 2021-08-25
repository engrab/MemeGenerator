package com.MemeGenerator.meme.creator.ui.activities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.MemeGenerator.meme.creator.databinding.ActivityMainBinding;
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
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AudienceNetworkAds.initialize(this);

        subscribeTopic();

        initViews();
        binding.extendedFab.setOnClickListener(v -> openCreateMemeActivity());
        binding.btnShare.setOnClickListener(v -> shareApp());
        binding.btnRate.setOnClickListener(v -> rateUs());
        bannerAdsFacebook();
        interstitalAdsFacebook();
    }

    private void interstitalAdsFacebook() {
        mInterstitialAd = new InterstitialAd(this, getString(R.string.ad_insters));
// Create listeners for the Interstitial Ad
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                showAdWithDelay();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        mInterstitialAd.loadAd(
                mInterstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

    private void showAdWithDelay() {
        /**
         * Here is an example for displaying the ad with delay;
         * Please do not copy the Handler into your project
         */
        // Handler handler = new Handler();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                // Check if interstitialAd has been loaded successfully
                if (mInterstitialAd == null || !mInterstitialAd.isAdLoaded()) {
                    return;
                }
                // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
                if (mInterstitialAd.isAdInvalidated()) {
                    return;
                }
                // Show the ad
                mInterstitialAd.show();
            }
        }, 1000 * 3); // Show the ad after 3 second
    }

//    private void bannerDialogeAdsFacebook(Dialog dialog) {
//
//        AdView adView = new AdView(this, "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID", AdSize.BANNER_HEIGHT_50);
//
//// Find the Ad Container
//        LinearLayout adContainer = (LinearLayout) dialog.findViewById(R.id.banner_container);
//
//// Add the ad view to your activity layout
//        adContainer.addView(adView);
//
//// Request an ad
//        adView.loadAd();
//    }

    private void bannerAdsFacebook() {

        mAdView = new AdView(this, "570681927643832_570682054310486", AdSize.BANNER_HEIGHT_50);

        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        adContainer.addView(mAdView);

        mAdView.loadAd();
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
        ((Window) Objects
                .requireNonNull(this.dialog.getWindow()))
                .setLayout((int) (((double) displayMetrics.widthPixels) * 0.9d), -2);

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnExit = dialog.findViewById(R.id.btnExit);
//        bannerDialogeAdsFacebook(dialog);

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
        if (mInterstitialAd != null) {
            mInterstitialAd.destroy();
        }
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}