package com.MemeGenerator.meme.creator.async;

import android.os.AsyncTask;

import com.MemeGenerator.meme.creator.database.FavoritesDao;
import com.MemeGenerator.meme.creator.models.FavoritePhoto;

public class DeleteFavoriteAsyncTask extends AsyncTask<FavoritePhoto, Void, Void> {

    private final FavoritesDao myAsyncTaskDao;

    public DeleteFavoriteAsyncTask(FavoritesDao wallpapersDao) {
        myAsyncTaskDao = wallpapersDao;
    }

    @Override
    protected Void doInBackground(final FavoritePhoto... photos) {
        myAsyncTaskDao.deleteFavoritePhoto(photos[0]);
        return null;
    }
}