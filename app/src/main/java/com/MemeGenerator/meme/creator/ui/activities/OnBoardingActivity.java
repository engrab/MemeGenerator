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
    private static final String TAG = "OnBoardingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        initFragment();
    }



    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    private void initFragment() {
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.frameTheme, new StoragePermissionsFragment());
        beginTransaction.commit();
    }

}