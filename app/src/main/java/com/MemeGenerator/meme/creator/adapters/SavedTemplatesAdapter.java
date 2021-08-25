package com.MemeGenerator.meme.creator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.MemeGenerator.meme.creator.databinding.LayoutGalleryItemBinding;
import com.MemeGenerator.meme.creator.interfaces.OnSavedItemClickListener;
import com.MemeGenerator.meme.creator.models.InternalStoragePhoto;

public class SavedTemplatesAdapter extends ListAdapter<InternalStoragePhoto, SavedTemplatesAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<InternalStoragePhoto> DIFF_CALLBACK = new DiffUtil.ItemCallback<InternalStoragePhoto>() {
        @Override
        public boolean areItemsTheSame(InternalStoragePhoto oldItem, InternalStoragePhoto newItem) {
            return oldItem.getName().equals(newItem.getName());
        }

        @Override
        public boolean areContentsTheSame(InternalStoragePhoto oldItem, InternalStoragePhoto newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    };
    private final Context mContext;
    private OnSavedItemClickListener listener;

    public SavedTemplatesAdapter(Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutGalleryItemBinding binding = LayoutGalleryItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        InternalStoragePhoto photo = getItem(position);
        holder.binding.imageViewThumbnail.setImageBitmap(photo.getBitmap());

    }

    public void setOnSavedItemClickListener(OnSavedItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LayoutGalleryItemBinding binding;

        public ViewHolder(LayoutGalleryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.cardView.setOnClickListener(v -> {
                listener.onItemClick(getAdapterPosition(), getItem(getAdapterPosition()));
            });
        }
    }
}
