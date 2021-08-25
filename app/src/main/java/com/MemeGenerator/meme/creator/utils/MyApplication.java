package com.MemeGenerator.meme.creator.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;


public class MyApplication extends Application{

    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();

    }

    /**
     * It's recommended to call this method from Application.onCreate().
     * Otherwise you can call it from all Activity.onCreate()
     * methods for Activities that contain ads.
     *
     * @param context Application or Activity.
     */
}
