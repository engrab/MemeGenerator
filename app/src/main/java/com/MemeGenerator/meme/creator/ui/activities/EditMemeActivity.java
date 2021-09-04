package com.MemeGenerator.meme.creator.ui.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.MemeGenerator.meme.creator.utils.FbAdsUtils;
import com.MemeGenerator.meme.creator.utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.MemeGenerator.meme.creator.R;
import com.MemeGenerator.meme.creator.editing.EmojiBSFragment;
import com.MemeGenerator.meme.creator.editing.FileSaveHelper;
import com.MemeGenerator.meme.creator.editing.PropertiesBSFragment;
import com.MemeGenerator.meme.creator.editing.ShapeBSFragment;
import com.MemeGenerator.meme.creator.editing.TextEditorDialogFragment;
import com.MemeGenerator.meme.creator.editing.base.BaseActivity;
import com.MemeGenerator.meme.creator.utils.KotlinHelper;

import java.io.File;
import java.io.IOException;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.ViewType;
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder;
import ja.burhanrashid52.photoeditor.shape.ShapeType;

public class EditMemeActivity extends BaseActivity implements
        OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        ShapeBSFragment.Properties,
        EmojiBSFragment.EmojiListener {

    public static final String PINCH_TEXT_SCALABLE_INTENT_KEY = "PINCH_TEXT_SCALABLE";
    private static final String TAG = EditMemeActivity.class.getSimpleName();
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    private static final String KEY_SHAPE_DIALOGE = "shape_dialoge";
    private static final String KEY_ERASE_DIALOGE = "erase_dialoge";
    PhotoEditor mPhotoEditor;

    private PhotoEditorView mPhotoEditorView;
    private PropertiesBSFragment mPropertiesBSFragment;
    private ShapeBSFragment mShapeBSFragment;
    private ShapeBuilder mShapeBuilder;
    private EmojiBSFragment mEmojiBSFragment;
    private TextView mTxtCurrentTool;
    private Typeface mWonderFont;
    private RelativeLayout mRootView;
    private boolean mIsFilterVisible;
    private FileSaveHelper mSaveFileHelper;
    private TextView textViewDescription;
    private ProgressDialog progressDialog;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_meme);

        textViewDescription = findViewById(R.id.textViewDescription);
        textViewDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
            }
        });

        AudienceNetworkAds.initialize(this);
        FbAdsUtils.LoadInterstitial(this);
        mAdView = FbAdsUtils.ShowBanner(this, findViewById(R.id.banner_container));

        initViews();
        mWonderFont = Typeface.createFromAsset(getAssets(),
                "beyond_wonderland.ttf");

        mPropertiesBSFragment = new PropertiesBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mShapeBSFragment = new ShapeBSFragment();
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);
        mShapeBSFragment.setPropertiesChangeListener(this);

        boolean pinchTextScalable = getIntent()
                .getBooleanExtra(PINCH_TEXT_SCALABLE_INTENT_KEY,
                        true);

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(pinchTextScalable)
                .build();
        mPhotoEditor.setOnPhotoEditorListener(this);

        mSaveFileHelper = new FileSaveHelper(this);

        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                switch (getIntent().getStringExtra("source_type")) {
                    case "url":
                        loadImageFromUrl();
                        break;
                    case "path":
                        loadImageFromPath();
                        break;
                    case "custom":
                        textViewDescription.setVisibility(View.VISIBLE);
                        break;
                    case "bitmap":
                        loadImageFromPhoto();
                        break;
                }
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void loadImageFromPhoto() {
        byte[] byteArray = getIntent().getByteArrayExtra("photo");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        mPhotoEditorView.getSource().setImageBitmap(bitmap);
    }

    public void shapeDialoge() {

        AlertDialog alertDialog = new AlertDialog.Builder(EditMemeActivity.this).create();
        alertDialog.setTitle("Shape Tool");
        alertDialog.setMessage("Create line, rectangle, and oval shape");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mPhotoEditor.setBrushDrawingMode(true);
                        mShapeBuilder = new ShapeBuilder();
                        mPhotoEditor.setShape(mShapeBuilder);
                        mTxtCurrentTool.setText(R.string.label_shape);
                        showBottomSheetDialogFragment(mShapeBSFragment);
                    }
                });
        alertDialog.show();

        SharedPrefs.putBoolean(this, KEY_SHAPE_DIALOGE, false);

    }

    public void eraseDialoge() {

        AlertDialog alertDialog = new AlertDialog.Builder(EditMemeActivity.this).create();
        alertDialog.setTitle("Eraser Tool");
        alertDialog.setMessage("Erase to background or transparency using a brush");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        mPhotoEditor.brushEraser();
                        mTxtCurrentTool.setText(R.string.label_eraser_mode);
                    }
                });
        alertDialog.show();

        SharedPrefs.putBoolean(this, KEY_ERASE_DIALOGE, false);

    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private void loadImageFromUrl() {
        String url = getIntent().getStringExtra("url");
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                Transition<? super Bitmap> transition) {

                        mPhotoEditorView.getSource().setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void loadImageFromPath() {
        File file = (File) getIntent().getExtras().get("file");
        Glide.with(this)
                .asBitmap()
                .load(file.getPath())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                Transition<? super Bitmap> transition) {

                        mPhotoEditorView.getSource().setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void initViews() {
        ImageView imgUndo;
        ImageView imgRedo;
        ImageView imgCamera;
        ImageView imgGallery;
        ImageView imgSave;
        ImageView imgClose;
        ImageView imgShare;

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool);
        mRootView = findViewById(R.id.rootView);

        imgUndo = findViewById(R.id.imgUndo);
        imgUndo.setOnClickListener(this);

        imgRedo = findViewById(R.id.imgRedo);
        imgRedo.setOnClickListener(this);

        imgCamera = findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(this);

        imgGallery = findViewById(R.id.imgGallery);
        imgGallery.setOnClickListener(this);

        imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(this);

        imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(this);

        imgShare = findViewById(R.id.imgShare);
        imgShare.setOnClickListener(this);


    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener((inputText, newColorCode) -> {
            final TextStyleBuilder styleBuilder = new TextStyleBuilder();
            styleBuilder.withTextColor(newColorCode);

            mPhotoEditor.editText(rootView, inputText, styleBuilder);
            mTxtCurrentTool.setText(R.string.label_text);
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imgUndo:
                mPhotoEditor.undo();
                break;

            case R.id.imgRedo:
                mPhotoEditor.redo();
                break;

            case R.id.imgSave:
                saveInPrivate();
                break;

            case R.id.imgClose:
                onBackPressed();
                break;
            case R.id.imgShare:
                shareImage();
                break;

            case R.id.imgCamera:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                break;

            case R.id.imgGallery:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
                break;
        }
    }

    private void saveInPrivate() {
        FbAdsUtils.ShowInterstitial(EditMemeActivity.this);
        showProgressDialog();
        mPhotoEditor.saveAsBitmap(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                KotlinHelper helper = new KotlinHelper(EditMemeActivity.this);
                helper.savePhotoToInternalStorage(System.currentTimeMillis() + "", saveBitmap);
                hideProgressDialog();
                mPhotoEditorView.getSource().setImageBitmap(saveBitmap);
                Toast.makeText(EditMemeActivity.this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
                FbAdsUtils.ShowInterstitial(EditMemeActivity.this);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditMemeActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shareImage() {
        showProgressDialog();
        progressDialog.setMessage("Loading..");
        mPhotoEditor.saveAsBitmap(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                mPhotoEditorView.getSource().setImageBitmap(saveBitmap);
                shareBitmap(saveBitmap);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditMemeActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shareBitmap(Bitmap bitmap) {

        String bitmapPath = MediaStore.Images.Media
                .insertImage(getContentResolver(),
                        bitmap,
                        "title",
                        "");
        Uri bitmapUri = Uri.parse(bitmapPath);
        hideProgressDialog();
        Toast.makeText(this, "Loading. Please wait..", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        startActivity(Intent.createChooser(intent, "Share"));
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving..");
        progressDialog.setTitle("Please wait..");
        progressDialog.show();
    }

    private void hideProgressDialog() {
        try {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:
                    mPhotoEditor.clearAllViews();
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    mPhotoEditorView.getSource().setImageBitmap(photo);
                    textViewDescription.setVisibility(View.GONE);
                    break;
                case PICK_REQUEST:
                    try {
                        textViewDescription.setVisibility(View.GONE);
                        mPhotoEditor.clearAllViews();
                        Uri uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        mPhotoEditorView.getSource().setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeColor(colorCode));
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeOpacity(opacity));
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onShapeSizeChanged(int shapeSize) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeSize(shapeSize));
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onShapePicked(ShapeType shapeType) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeType(shapeType));
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
        mTxtCurrentTool.setText(R.string.label_emoji);
    }

    @Override
    public void isPermissionGranted(boolean isGranted, String permission) {
        if (isGranted) {
            saveInPrivate();
        }
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.msg_save_image));
        builder.setPositiveButton("Save", (dialog, which) -> saveInPrivate());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.setNeutralButton("Discard", (dialog, which) -> finish());
        builder.create().show();

    }

    private void showBottomSheetDialogFragment(BottomSheetDialogFragment fragment) {
        if (fragment == null || fragment.isAdded()) {
            return;
        }
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    @Override
    public void onBackPressed() {
        if (!mPhotoEditor.isCacheEmpty()) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }

    public void toolSelected(View view) {
        switch (view.getId()) {
            case R.id.rootLayoutText:
                enableTextEditing();
                break;
            case R.id.rootLayoutShape:
                enableShapeEditing();
                break;
            case R.id.rootLayoutEraser:
                enableEraserEditing();
                break;
            case R.id.rootLayoutEmoji:
                enableEmojiEditing();
                break;
            default:
                break;
        }
    }

    private void enableTextEditing() {
        TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
        textEditorDialogFragment.setOnTextEditorListener((inputText, colorCode) -> {
            final TextStyleBuilder styleBuilder = new TextStyleBuilder();
            styleBuilder.withTextColor(colorCode);

            mPhotoEditor.addText(inputText, styleBuilder);
            mTxtCurrentTool.setText(R.string.label_text);
        });
    }

    private void enableShapeEditing() {

        if (SharedPrefs.getBoolean(this, KEY_SHAPE_DIALOGE, true)) {
            shapeDialoge();
        } else {

            mPhotoEditor.setBrushDrawingMode(true);
            mShapeBuilder = new ShapeBuilder();
            mPhotoEditor.setShape(mShapeBuilder);
            mTxtCurrentTool.setText(R.string.label_shape);
            showBottomSheetDialogFragment(mShapeBSFragment);
        }

    }

    private void enableEraserEditing() {
        if (SharedPrefs.getBoolean(this, KEY_ERASE_DIALOGE, true)) {
            eraseDialoge();
        } else {

            mPhotoEditor.brushEraser();
            mTxtCurrentTool.setText(R.string.label_eraser_mode);
        }
    }

    private void enableEmojiEditing() {
        showBottomSheetDialogFragment(mEmojiBSFragment);
    }


}
