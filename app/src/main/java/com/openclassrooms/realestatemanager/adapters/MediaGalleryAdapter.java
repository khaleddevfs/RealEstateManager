package com.openclassrooms.realestatemanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.databinding.MediaListItemBinding;
import com.openclassrooms.realestatemanager.models.RealEstateMedia;

import java.util.List;

public class MediaGalleryAdapter extends RecyclerView.Adapter<MediaGalleryViewHolder> {


    Context context;
    List<RealEstateMedia> mediaList;
    private MediaListItemBinding binding;

    public MediaGalleryAdapter(List<RealEstateMedia> mediaList, MediaListItemBinding binding) {
        this.binding = binding;
        mediaList = mediaList;
    }
    @NonNull
    @Override
    public MediaGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         MediaListItemBinding binding = MediaListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        context = parent.getContext();
        return new MediaGalleryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaGalleryViewHolder holder, int position) {

        Glide.with(context)
                .load(mediaList.get(position).getMediaUrl())
                .into(holder.getImage());

        holder.getCaption().setText(mediaList.get(position).getMediaCaption());
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public RealEstateMedia getMedia(int position) {
        return mediaList.get(position);
    }

    public interface MediaGalleryClickListener {
        void onMediaClick(int position);
    }

    private MediaGalleryClickListener clickListener = new MediaGalleryClickListener() {
        @Override
        public void onMediaClick(int position) {

        }
    };

    public MediaGalleryClickListener getClickListener() {
        return clickListener;
    }
}
