package com.MemeGenerator.meme.creator.async;

import android.os.AsyncTask;

import com.MemeGenerator.meme.creator.database.FavoritesDao;
import com.MemeGenerator.meme.creator.models.FavoritePhoto;

public class InsertFavoriteAsyncTask extends AsyncTask<FavoritePhoto, Void, Void> {

    private final FavoritesDao mAsyncTaskDao;

    public InsertFavoriteAsyncTask(FavoritesDao dao) {
        mAsyncTaskDao = dao;
    }

    @Override
    protected Void doInBackground(FavoritePhoto... favoritePhotos) {
        mAsyncTaskDao.insertFavoritePhoto(favoritePhotos[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
