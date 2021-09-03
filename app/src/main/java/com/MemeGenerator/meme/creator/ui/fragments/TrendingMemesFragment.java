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

import com.MemeGenerator.meme.creator.databinding.FragmentTrendingMemesBinding;
import com.MemeGenerator.meme.creator.utils.FbAdsUtils;
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
import com.MemeGenerator.meme.creator.ui.activities.EditMemeActivity;
import com.MemeGenerator.meme.creator.adapters.MemesAdapter;
import com.MemeGenerator.meme.creator.models.FavoritePhoto;
import com.MemeGenerator.meme.creator.viewmodels.FavoritesViewModel;

import java.util.ArrayList;

public class TrendingMemesFragment extends Fragment {

    private static final String TAG = "TrendingMemesFragment";
    private FragmentTrendingMemesBinding binding;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private ArrayList<String> memes = new ArrayList<>();
    private MemesAdapter adapter;
    private BottomSheetDialog bottomSheetDialog;
    private FavoritesViewModel mFavoritesViewModel;

    public TrendingMemesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference()
                .child("trending");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTrendingMemesBinding
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

        });
    }

    private void displayBottomSheet(String url) {
        bottomSheetDialog = new BottomSheetDialog(requireActivity());

        View bottomSheetView = LayoutInflater
                .from(getContext())
                .inflate(R.layout.layout_bottom_sheet, null);

        bottomSheetView.findViewById(R.id.linearLayoutEdit).setOnClickListener(v -> {
            openCreateMemeActivity(url);
            FbAdsUtils.ShowInterstitial(requireActivity());
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
}