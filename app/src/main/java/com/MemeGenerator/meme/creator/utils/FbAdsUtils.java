package com.MemeGenerator.meme.creator.utils;

import android.content.Context;
import android.widget.LinearLayout;

import com.MemeGenerator.meme.creator.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

public class FbAdsUtils {
    private static InterstitialAd interstitialAd;

    public static AdView ShowBanner(Context context, LinearLayout adContainer) {
        AdView adView = new AdView(context, context.getResources().getString(R.string.ad_banner), AdSize.BANNER_HEIGHT_50);
        adContainer.addView(adView);
        adView.loadAd();
        return adView;
    }

    public static void LoadInterstitial(Context context) {
        if (interstitialAd != null) {
            interstitialAd.loadAd(
                    interstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());
        } else {
            interstitialAd = new InterstitialAd(context, context.getResources().getString(R.string.ad_insters));
            interstitialAd.loadAd(
                    interstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());
        }

    }

    public static void ShowInterstitial(Context context) {
        if (interstitialAd != null && interstitialAd.isAdLoaded()) {
            if (!interstitialAd.isAdInvalidated()) {
                interstitialAd.show();
            }else {
                LoadInterstitial(context);
            }
        } else {
            LoadInterstitial(context);
        }
    }

    static InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
        @Override
        public void onInterstitialDisplayed(Ad ad) {

        }

        @Override
        public void onInterstitialDismissed(Ad ad) {
            if (interstitialAd != null) {
                interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(interstitialAdListener).build());
            }

        }

        @Override
        public void onError(Ad ad, AdError adError) {

        }

        @Override
        public void onAdLoaded(Ad ad) {

        }

        @Override
        public void onAdClicked(Ad ad) {
        }

        @Override
        public void onLoggingImpression(Ad ad) {
        }
    };

}
