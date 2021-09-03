package com.MemeGenerator.meme.creator.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.MemeGenerator.meme.creator.R;
import com.MemeGenerator.meme.creator.ui.adapter.SlideViewPagerAdapter;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

public class SlideActivity extends AppCompatActivity {

    public static ViewPager viewPager;
    SlideViewPagerAdapter adapter;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        bannerAdsFacebook();
        viewPager = findViewById(R.id.viewpager);
        adapter = new SlideViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        if (isOpenAlread()) {
            Intent intent = new Intent(SlideActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            SharedPreferences.Editor editor = getSharedPreferences("slide", MODE_PRIVATE).edit();
            editor.putBoolean("slide", true);
            editor.commit();
        }

    }

    private void bannerAdsFacebook() {

        mAdView = new AdView(this, getString(R.string.ad_banner), AdSize.BANNER_HEIGHT_50);

        LinearLayout adContainer = findViewById(R.id.banner_container);

        adContainer.addView(mAdView);

        mAdView.loadAd();
    }

    private boolean isOpenAlread() {

        SharedPreferences sharedPreferences = getSharedPreferences("slide", MODE_PRIVATE);
        boolean result = sharedPreferences.getBoolean("slide", false);
        return result;

    }
}