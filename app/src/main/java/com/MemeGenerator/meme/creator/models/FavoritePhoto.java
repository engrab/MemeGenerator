package com.MemeGenerator.meme.creator.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "table_favorites")
public class FavoritePhoto implements Parcelable {

    public static final Creator<FavoritePhoto> CREATOR = new Creator<FavoritePhoto>() {
        @Override
        public FavoritePhoto createFromParcel(Parcel in) {
            return new FavoritePhoto(in);
        }

        @Override
        public FavoritePhoto[] newArray(int size) {
            return new FavoritePhoto[size];
        }
    };
    @PrimaryKey
    @NonNull
    private String url;

    public FavoritePhoto() {
    }

    public FavoritePhoto(@NotNull String url) {
        this.url = url;
    }

    protected FavoritePhoto(Parcel in) {
        url = in.readString();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;

        FavoritePhoto photo = (FavoritePhoto) obj;
        return Objects.requireNonNull(photo).url.equals(this.url);
    }
}
