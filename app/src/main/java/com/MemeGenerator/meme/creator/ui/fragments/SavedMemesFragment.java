package com.MemeGenerator.meme.creator.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
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

import com.MemeGenerator.meme.creator.databinding.FragmentSavedBinding;
import com.MemeGenerator.meme.creator.utils.FbAdsUtils;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.MemeGenerator.meme.creator.R;
import com.MemeGenerator.meme.creator.ui.activities.EditMemeActivity;
import com.MemeGenerator.meme.creator.adapters.SavedTemplatesAdapter;
import com.MemeGenerator.meme.creator.models.InternalStoragePhoto;
import com.MemeGenerator.meme.creator.utils.KotlinHelper;

import java.io.ByteArrayOutputStream;
import java.util.List;

import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineStart;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.GlobalScope;

public class SavedMemesFragment extends Fragment {

    private static final String TAG = "SavedTemplatesFragment";
    private FragmentSavedBinding binding;
    private BottomSheetDialog bottomSheetDialog;

    private SavedTemplatesAdapter adapter;

    public SavedMemesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSavedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadImages();
//        interstitalAdsFacebook();
    }

    private void initViews() {

        adapter = new SavedTemplatesAdapter(getContext());
        binding.recyclerView.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(() -> {
            loadImages();
            binding.swipeRefresh.setRefreshing(false);
        });

        adapter.setOnSavedItemClickListener((position, photo) -> {
            displayBottomSheet(photo);
        });
    }

    private void loadImages() {
        KotlinHelper kotlinHelper = new KotlinHelper(requireContext());

        BuildersKt.launch(GlobalScope.INSTANCE,
                Dispatchers.getMain(),  //context to be ran on
                CoroutineStart.DEFAULT,
                (coroutineScope, continuation) -> KotlinHelper
                        .Companion
                        .loadPhotosFromInternalStorage(requireContext().getFilesDir(),
                                kotlinHelper.getContinuation((internalStoragePhotos, throwable) -> {
                                    if (internalStoragePhotos != null) {
                                        requireActivity().runOnUiThread(() -> {

                                            if (internalStoragePhotos.isEmpty()) {
                                                binding.textViewNoItems.setVisibility(View.VISIBLE);
                                            } else {
                                                binding.textViewNoItems.setVisibility(View.GONE);
                                            }

                                            adapter.submitList((List<InternalStoragePhoto>) internalStoragePhotos);
                                            binding.swipeRefresh.setRefreshing(false);
                                        });
                                    }
                                }))
        );
    }

    private void displayBottomSheet(InternalStoragePhoto photo) {
        bottomSheetDialog = new BottomSheetDialog(requireContext());

        View bottomSheetView = LayoutInflater
                .from(getContext())
                .inflate(R.layout.layout_bottom_sheet_saved_items, null);

        bottomSheetView.findViewById(R.id.linearLayoutEdit).setOnClickListener(v -> {
            FbAdsUtils.ShowInterstitial(requireActivity());
            openCreateMemeActivity(photo);
        });

        bottomSheetView.findViewById(R.id.linearLayoutDeleteTemplate).setOnClickListener(v -> {
            if (requireContext().deleteFile(photo.getName())) {
                Toast.makeText(requireContext(), "Deleted Successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Error while deleting..", Toast.LENGTH_SHORT).show();
            }
            binding.swipeRefresh.setRefreshing(true);
            bottomSheetDialog.dismiss();
            initViews();

        });

        bottomSheetView.findViewById(R.id.linearLayoutShare).setOnClickListener(v -> {
            shareBitmap(photo.getBitmap());
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void openCreateMemeActivity(InternalStoragePhoto photo) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Bitmap bitmap = photo.getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        byte[] byteArray = os.toByteArray();

        Intent intent = new Intent(getActivity(), EditMemeActivity.class);
        intent.putExtra("photo", byteArray);
        intent.putExtra("source_type", "bitmap");
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
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
}