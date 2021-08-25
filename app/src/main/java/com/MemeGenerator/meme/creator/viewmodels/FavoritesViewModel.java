package com.MemeGenerator.meme.creator.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.MemeGenerator.meme.creator.repository.FavoritesRepository;
import com.MemeGenerator.meme.creator.models.FavoritePhoto;

import java.util.List;

public class FavoritesViewModel extends AndroidViewModel {

    private FavoritesRepository mRepository;
    private LiveData<List<FavoritePhoto>> mAllFavoritePhotos;

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        mRepository = new FavoritesRepository(application);
        mAllFavoritePhotos = mRepository.getAllFavoritePhotos();
    }

    public LiveData<List<FavoritePhoto>> getAllFavoritePhotos() {
        return mAllFavoritePhotos;
    }

    public void insertPhoto(FavoritePhoto photo) {
        mRepository.insertWallpaper(photo);
    }

    public void deletePhoto(FavoritePhoto photo) {
        mRepository.deleteWallpaper(photo);
    }
}
