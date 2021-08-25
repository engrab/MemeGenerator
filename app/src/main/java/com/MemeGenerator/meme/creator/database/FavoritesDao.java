package com.MemeGenerator.meme.creator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.MemeGenerator.meme.creator.models.FavoritePhoto;

import java.util.List;

@Dao
public interface FavoritesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFavoritePhoto(FavoritePhoto photo);

    @Query("SELECT * FROM table_favorites")
    LiveData<List<FavoritePhoto>> getAllFavoriteFavoritePhotos();

    @Delete
    void deleteFavoritePhoto(FavoritePhoto photo);
}
