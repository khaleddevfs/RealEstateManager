package com.openclassrooms.realestatemanager.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.databinding.PopupImageBinding;
import com.openclassrooms.realestatemanager.models.RealEstateMedia;
public class ImagePopupWindow {

    public void showPopup(View anchorView, RealEstateMedia media) {
        // Création de l'instance du binding pour le layout popup_image.
        PopupImageBinding binding = PopupImageBinding.inflate(LayoutInflater.from(anchorView.getContext()), (ViewGroup) anchorView.getParent(), false);

        // Création du PopupWindow avec le root du binding comme vue.
        PopupWindow popupWindow = new PopupWindow(binding.getRoot(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        // Utilisation du binding pour accéder à l'ImageView et au TextView.
        Glide.with(anchorView.getRootView()).load(media.getMediaUrl()).into(binding.imageView);
        binding.caption.setText(media.getMediaCaption());
    }
}

