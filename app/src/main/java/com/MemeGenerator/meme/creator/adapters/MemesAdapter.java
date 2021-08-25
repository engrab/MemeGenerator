package com.MemeGenerator.meme.creator.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.MemeGenerator.meme.creator.databinding.LayoutMemeItemBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.MemeGenerator.meme.creator.interfaces.OnMemeItemClickListener;

public class MemesAdapter extends ListAdapter<String, MemesAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<String> DIFF_CALLBACK = new DiffUtil.ItemCallback<String>() {
        @Override
        public boolean areItemsTheSame(String oldItem, String newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(String oldItem, String newItem) {
            return oldItem.equals(newItem);
        }
    };
    private final Context mContext;
    private OnMemeItemClickListener listener;

    public MemesAdapter(Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutMemeItemBinding binding = LayoutMemeItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = getItem(position);

        Glide.with(mContext)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.binding.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.binding.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.binding.imageViewThumbnail);
    }

    public void setOnMemeItemClickListener(OnMemeItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LayoutMemeItemBinding binding;

        public ViewHolder(LayoutMemeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.cardView.setOnClickListener(v -> {
                listener.onItemClick(getAdapterPosition(), getItem(getAdapterPosition()), v);
            });
        }
    }
}
