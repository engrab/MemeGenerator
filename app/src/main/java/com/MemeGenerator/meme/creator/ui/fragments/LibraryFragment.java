package com.MemeGenerator.meme.creator.ui.fragments;

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

import com.MemeGenerator.meme.creator.databinding.FragmentMemesBinding;
import com.MemeGenerator.meme.creator.ui.activities.EditMemeActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.MemeGenerator.meme.creator.R;
import com.MemeGenerator.meme.creator.adapters.MemesAdapter;
import com.MemeGenerator.meme.creator.models.FavoritePhoto;
import com.MemeGenerator.meme.creator.viewmodels.FavoritesViewModel;

import java.util.ArrayList;

public class LibraryFragment extends Fragment {

    private static final String TAG = "MemesFragment";
    private FragmentMemesBinding binding;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private ArrayList<String> memes = new ArrayList<>();
    private MemesAdapter adapter;
    private BottomSheetDialog bottomSheetDialog;
    private InterstitialAd mInterstitialAd;
    private FavoritesViewModel mFavoritesViewModel;

    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference()
                .child("library");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMemesBinding
                .inflate(inflater, container, false);

        mFavoritesViewModel = ViewModelProviders.of(this)
                .get(FavoritesViewModel.class);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getMemes(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        interstitalAdsFacebook();
    }

    private void getMemes(DataSnapshot snapshot) {

        for (DataSnapshot dataSnapshot : snapshot
                .getChildren()) {

            String url = (String) dataSnapshot.getValue();
            memes.add(url);
            binding.progressBar.setVisibility(View.GONE);
        }

        adapter = new MemesAdapter(getActivity());
        adapter.submitList(memes);

        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(getContext(), 3);

        binding.recyclerView.setLayoutManager(gridLayoutManager);
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnMemeItemClickListener((position, url, view) -> {
            displayBottomSheet(url);
            interstitalAdsFacebook();
        });
    }

    private void displayBottomSheet(String url) {
        bottomSheetDialog = new BottomSheetDialog(requireActivity());

        View bottomSheetView = LayoutInflater
                .from(getContext())
                .inflate(R.layout.layout_bottom_sheet, null);

        bottomSheetView.findViewById(R.id.linearLayoutEdit).setOnClickListener(v -> {
            openCreateMemeActivity(url);
            interstitalAdsFacebook();
        });

        bottomSheetView.findViewById(R.id.linearLayoutSaveTemplate).setOnClickListener(v -> {
            mFavoritesViewModel.insertPhoto(new FavoritePhoto(url));
            bottomSheetDialog.dismiss();
            Toast.makeText(requireContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
        });

        bottomSheetView.findViewById(R.id.linearLayoutShare).setOnClickListener(v -> {
            shareImage(url);
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
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