package com.openclassrooms.realestatemanager.adapters;


import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.realestatemanager.databinding.MediaListItemBinding;

public class MediaGalleryViewHolder  extends RecyclerView.ViewHolder {

    private final MediaListItemBinding binding;

    public MediaGalleryViewHolder(@NonNull MediaListItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;

        binding.getRoot().setOnClickListener(v -> {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                MediaGalleryAdapter adapter = (MediaGalleryAdapter) getBindingAdapter();
                MediaGalleryAdapter.MediaGalleryClickListener clickListener = adapter.getClickListener();
                if (clickListener != null) {
                    clickListener.onMediaClick(position);
                }
            }
        });
    }

    public ImageView getImage() {
        return binding.image;
    }

    public TextView getCaption() {
        return binding.caption;
    }
}
