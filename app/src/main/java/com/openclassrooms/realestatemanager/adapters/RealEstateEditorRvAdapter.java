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

import java.util.List;

public class RealEstateEditorRvAdapter extends RecyclerView.Adapter<RealEstateEditorRvViewHolder> {
    List<RealEstateMedia> mRealEstateMediaList;
    private Context mContext;

    public RealEstateEditorRvAdapter(List<RealEstateMedia> realEstateMediaList) {
        mRealEstateMediaList = realEstateMediaList;
    }

    @NonNull
    @Override
    public RealEstateEditorRvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AddMediaListItemBinding mediaListItemBinding = AddMediaListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        mContext = parent.getContext();
        return new RealEstateEditorRvViewHolder(mediaListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RealEstateEditorRvViewHolder holder, int position) {
        Glide.with(mContext)
                .load(mRealEstateMediaList.get(position).getMediaUrl())
                .into(holder.getImage());


        Log.d("TAG", "onBindViewHolder: " + mRealEstateMediaList.get(position).getMediaUrl());

        holder.getCaption().setText(mRealEstateMediaList.get(position).getMediaCaption());

        holder.getButton().setOnClickListener(view -> {
            if (position < mRealEstateMediaList.size()) {
                mRealEstateMediaList.remove(position);
                notifyItemRemoved(position);
            }
        });
    }



    @Override
    public int getItemCount() {
        return mRealEstateMediaList.size();
    }
}
