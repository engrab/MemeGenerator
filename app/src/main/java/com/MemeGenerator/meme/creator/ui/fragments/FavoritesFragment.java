package com.MemeGenerator.meme.creator.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.MemeGenerator.meme.creator.databinding.FragmentFavoritesBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.MemeGenerator.meme.creator.R;
import com.MemeGenerator.meme.creator.ui.activities.EditMemeActivity;
import com.MemeGenerator.meme.creator.adapters.MemesAdapter;
import com.MemeGenerator.meme.creator.models.FavoritePhoto;
import com.MemeGenerator.meme.creator.viewmodels.FavoritesViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private static final String TAG = "FavoritesFragment";
    private FragmentFavoritesBinding binding;
    private FavoritesViewModel mFavoritesViewModel;
    private MemesAdapter adapter;
    private Context mContext;
    private BottomSheetDialog bottomSheetDialog;
    private InterstitialAd mInterstitialAd;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);

        initViews();
        initEmptyRecyclerView();
        subscribeObservers();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        interstitalAdsFacebook();
    }

    private void initViews() {
        mFavoritesViewModel = ViewModelProviders.of(this)
                .get(FavoritesViewModel.class);

        mContext = getActivity();

        adapter = new MemesAdapter(mContext);
    }

    private void initEmptyRecyclerView() {
        adapter = new MemesAdapter(mContext);
        binding.recyclerView.setAdapter(adapter);

        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(getContext(), 3);

        binding.recyclerView.setLayoutManager(gridLayoutManager);

        adapter.setOnMemeItemClickListener((position, url, view) -> {
            displayBottomSheet(url);
            interstitalAdsFacebook();
        });
    }

    private void subscribeObservers() {
        mFavoritesViewModel.getAllFavoritePhotos()
                .observe(getViewLifecycleOwner(), photos -> {
                    List<String> urls = new ArrayList<>();
                    for (FavoritePhoto photo : photos) {
                        urls.add(photo.getUrl());
                    }
                    adapter.submitList(urls);

                    if (photos.isEmpty()) {
                        binding.textViewNoItems.setVisibility(View.VISIBLE);
                    } else {
                        binding.textViewNoItems.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void displayBottomSheet(String url) {
        bottomSheetDialog = new BottomSheetDialog(requireActivity());

        View bottomSheetView = LayoutInflater
                .from(getContext())
                .inflate(R.layout.layout_bottom_sheet_favorite_items, null);

        bottomSheetView.findViewById(R.id.linearLayoutEdit).setOnClickListener(v -> {
            openCreateMemeActivity(url);
            interstitalAdsFacebook();
        });

        bottomSheetView.findViewById(R.id.linearLayoutDeleteTemplate).setOnClickListener(v -> {
            removeFromFavorites(url);
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.linearLayoutShare).setOnClickListener(v -> {
            shareImage(url);
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void removeFromFavorites(String url) {
        mFavoritesViewModel.deletePhoto(new FavoritePhoto(url));
        Toast.makeText(mContext, "Removed from Favorites", Toast.LENGTH_SHORT).show();
    }

    private void openCreateMemeActivity(String url) {
        Intent intent = new Intent(getActivity(), EditMemeActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("source_type", "url");
        startActivity(intent);
    }

    private void shareImage(String url) {
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                Transition<? super Bitmap> transition) {
                        shareBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void shareBitmap(Bitmap bitmap) {
        String bitmapPath = MediaStore.Images.Media
                .insertImage(requireContext().getContentResolver(),
                        bitmap,
                        "title",
                        "");
        Uri bitmapUri = Uri.parse(bitmapPath);
        Toast.makeText(requireContext(), "Loading. Please wait..", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        startActivity(Intent.createChooser(intent, "Share"));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
    }
    private void interstitalAdsFacebook() {
        mInterstitialAd = new InterstitialAd(getContext(), getString(R.string.ad_insters));
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



    @Override
    public void onDestroy() {
        if (mInterstitialAd != null) {
            mInterstitialAd.destroy();
        }

        super.onDestroy();
    }

}