package com.openclassrooms.realestatemanager.adapters;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.realestatemanager.databinding.RealEstateListItemBinding;
import com.openclassrooms.realestatemanager.models.RealEstate;

public class RealEstateViewHolder extends RecyclerView.ViewHolder{
    private RealEstateListItemBinding binding;



    public RealEstateViewHolder(RealEstateListItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;}


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



    // Méthode pour configurer la visibilité du badge "Vendu"
    public void setSold(boolean isSold) {
        binding.soldBadgeView.setVisibility(isSold ? View.VISIBLE : View.GONE);
    }

}
