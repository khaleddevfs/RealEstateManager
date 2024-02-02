package com.openclassrooms.realestatemanager.adapters;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.realestatemanager.databinding.RealEstateListItemBinding;

public class RealEstateViewHolder extends RecyclerView.ViewHolder{
    private final RealEstateListItemBinding binding;
    public RealEstateViewHolder(@NonNull RealEstateListItemBinding realEstateListItemBinding) {
        super(realEstateListItemBinding.getRoot());
       binding = realEstateListItemBinding;
    }

    public ImageView getRealEstateImageView()
    {
        return  binding.realEstateImage;
    }

    public TextView getRealEstateName()
    {
        return binding.realEstateName;
    }

    public TextView getRealEstateRegion() {
        return  binding.realEstateRegion;
    }

    public TextView getRealEstatePrice() {
        return  binding.realEstatePrice;
    }
}
