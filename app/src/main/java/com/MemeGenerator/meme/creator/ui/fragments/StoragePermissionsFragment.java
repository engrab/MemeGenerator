package com.MemeGenerator.meme.creator.ui.fragments;

import static com.MemeGenerator.meme.creator.utils.Constants.KEY_IS_FIRST_TIME;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.MemeGenerator.meme.creator.databinding.FragmentStoragePermissionsBinding;
import com.MemeGenerator.meme.creator.ui.activities.MainActivity;
import com.MemeGenerator.meme.creator.utils.SharedPrefs;

public class StoragePermissionsFragment extends Fragment {

    private static final int REQUEST_WRITE_STORAGE_PERMISSION_CODE = 1;
    private FragmentStoragePermissionsBinding binding;

    public StoragePermissionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentStoragePermissionsBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        init();

        return rootView;
    }

    private void init() {
        binding.cardViewAllow.setOnClickListener(v -> {

            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions( //Method of Fragment
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE_PERMISSION_CODE
                );
            } else {
                SharedPrefs.putBoolean(getContext(), KEY_IS_FIRST_TIME, false);
                Toast.makeText(getContext(), "Permission already granted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        binding.textViewHyperlink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_STORAGE_PERMISSION_CODE) {

            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                SharedPrefs.putBoolean(getContext(), KEY_IS_FIRST_TIME, false);

                Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();

            } else {
                new AlertDialog.Builder(getContext())
                        .setMessage("Kindly Allow Permissions to enjoy all features of this app.")
                        .setCancelable(false)
                        .setPositiveButton("Allow", (dialog, id) -> checkPermissions())
                        .show();
            }
        }
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( //Method of Fragment
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE_PERMISSION_CODE
            );
        }
    }
}