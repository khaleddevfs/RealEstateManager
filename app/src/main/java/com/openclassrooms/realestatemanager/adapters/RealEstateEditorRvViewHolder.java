package com.openclassrooms.realestatemanager.adapters;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.realestatemanager.databinding.AddMediaListItemBinding;

public class RealEstateEditorRvViewHolder extends RecyclerView.ViewHolder {
    private final AddMediaListItemBinding mBinding;

    public RealEstateEditorRvViewHolder(@NonNull AddMediaListItemBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    public ImageView getImage(){
        return mBinding.selectedImage;
    }

    public EditText getCaption() {
        return mBinding.etCaption;
    }

    public ImageButton getButton() {
        return mBinding.btDelete;
    }
}
