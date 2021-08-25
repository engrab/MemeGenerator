package com.MemeGenerator.meme.creator.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.MemeGenerator.meme.creator.databinding.ActivityOnboardingBinding;
import com.MemeGenerator.meme.creator.ui.fragments.StoragePermissionsFragment;
import com.MemeGenerator.meme.creator.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

public class OnBoardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private static final String TAG = "OnBoardingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AudienceNetworkAds.initialize(this);
        bannerAdsFacebook();
        interstitalAdsFacebook();

        initFragment();
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


    private void bannerAdsFacebook() {

        mAdView = new AdView(this, getString(R.string.ad_banner), AdSize.BANNER_HEIGHT_50);

        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        adContainer.addView(mAdView);

        mAdView.loadAd();
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

    private void initFragment() {
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.frameTheme, new StoragePermissionsFragment());
        beginTransaction.commit();
    }

}