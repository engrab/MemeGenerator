package com.MemeGenerator.meme.creator.interfaces;

import android.view.View;

public interface OnMemeItemClickListener {

    void onItemClick(int position,
                     String url,
                     View view);

}
