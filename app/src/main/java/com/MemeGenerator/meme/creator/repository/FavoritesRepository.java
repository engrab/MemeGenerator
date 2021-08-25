package com.MemeGenerator.meme.creator.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.MemeGenerator.meme.creator.database.FavoritesDao;
import com.MemeGenerator.meme.creator.database.FavoritesRoomDatabase;
import com.MemeGenerator.meme.creator.async.DeleteFavoriteAsyncTask;
import com.MemeGenerator.meme.creator.async.InsertFavoriteAsyncTask;
import com.MemeGenerator.meme.creator.models.FavoritePhoto;

import java.util.List;

public class FavoritesRepository {

    private FavoritesDao favoritesDao;
    private LiveData<List<FavoritePhoto>> mAllFavoritePhotos;

    public FavoritesRepository(Application application) {
        FavoritesRoomDatabase db = FavoritesRoomDatabase.getDatabase(application);
        favoritesDao = db.favoritesDao();
        mAllFavoritePhotos = favoritesDao.getAllFavoriteFavoritePhotos();
    }

    public LiveData<List<FavoritePhoto>> getAllFavoritePhotos() {
        return mAllFavoritePhotos;
    }

    public void insertWallpaper(FavoritePhoto photo) {
        new InsertFavoriteAsyncTask(favoritesDao)
                .execute(photo);
    }

    public void deleteWallpaper(FavoritePhoto photo) {
        new DeleteFavoriteAsyncTask(favoritesDao)
                .execute(photo);
    }
}
