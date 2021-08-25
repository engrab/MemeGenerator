package com.MemeGenerator.meme.creator.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.MemeGenerator.meme.creator.models.FavoritePhoto;

@Database(entities = {FavoritePhoto.class}, version = 1, exportSchema = false)
public abstract class FavoritesRoomDatabase extends RoomDatabase {

    public static FavoritesRoomDatabase INSTANCE;

    public static FavoritesRoomDatabase getDatabase(Context context) {

        if (INSTANCE == null) {
            synchronized (FavoritesRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FavoritesRoomDatabase.class, "favorites_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    public abstract FavoritesDao favoritesDao();
}