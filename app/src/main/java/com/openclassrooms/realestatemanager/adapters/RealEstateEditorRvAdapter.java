package com.openclassrooms.realestatemanager.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.databinding.AddMediaListItemBinding;
import com.openclassrooms.realestatemanager.models.RealEstateMedia;

import java.io.File;
import java.util.List;

public class RealEstateEditorRvAdapter extends RecyclerView.Adapter<RealEstateEditorRvViewHolder> {
    List<RealEstateMedia> realEstateMediaList;
    private Context context;

    public RealEstateEditorRvAdapter(List<RealEstateMedia> realEstateMediaList) {
        this.realEstateMediaList = realEstateMediaList;
    }

    @NonNull
    @Override
    public RealEstateEditorRvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AddMediaListItemBinding mediaListItemBinding = AddMediaListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RealEstateEditorRvViewHolder(mediaListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RealEstateEditorRvViewHolder holder, int position) {
        RealEstateMedia media = realEstateMediaList.get(position);
        if (media != null) {
            Glide.with(holder.itemView.getContext())
                    .load(new File(media.getMediaUrl()))
                    .into(holder.getImage());
            holder.getCaption().setText(media.getMediaCaption());

            holder.getButton().setOnClickListener(view -> {

                realEstateMediaList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, realEstateMediaList.size());
            });
        }
    }



    @Override
    public int getItemCount() {
        return realEstateMediaList != null ? realEstateMediaList.size() : 0;

    }

    public void setRealEstateMediaList(List<RealEstateMedia> realEstateMediaList) {
        this.realEstateMediaList = realEstateMediaList;
        notifyDataSetChanged();
    }
}

