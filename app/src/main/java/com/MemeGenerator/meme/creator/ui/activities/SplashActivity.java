package com.MemeGenerator.meme.creator.ui.activities;

import static com.MemeGenerator.meme.creator.utils.Constants.KEY_IS_FIRST_TIME;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.MemeGenerator.meme.creator.R;
import com.MemeGenerator.meme.creator.utils.SharedPrefs;
import com.facebook.ads.AudienceNetworkAds;

public class SplashActivity extends AppCompatActivity {

    public static final int SPLASH_DURATION = 3000;
    private static final String TAG = "SplashActivity";
    private boolean isFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_splash);

        TextView tv = (TextView) findViewById(R.id.tv_app_name);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "KaushanScript-Regular.otf");
        tv.setTypeface(face);

        AudienceNetworkAds.initialize(getApplicationContext());
        isFirstTime = SharedPrefs.getBoolean(this, KEY_IS_FIRST_TIME, true);

        if (isFirstTime)
            openOnBoardingActivity();
        else
            openMainActivity();
    }



    private void openOnBoardingActivity() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, SlideActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }

    private void openMainActivity() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}