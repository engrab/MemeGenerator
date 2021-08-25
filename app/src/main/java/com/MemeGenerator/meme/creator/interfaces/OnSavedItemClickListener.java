package com.MemeGenerator.meme.creator.interfaces;

import com.MemeGenerator.meme.creator.models.InternalStoragePhoto;

public interface OnSavedItemClickListener {

    void onItemClick(int position, InternalStoragePhoto photo);
}
