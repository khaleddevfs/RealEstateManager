package com.openclassrooms.realestatemanager.utils;

import android.content.Context;

import java.io.File;

public class ImageDownloadService {
    private final Context context;

    public ImageDownloadService(Context context) {
        this.context = context;
    }

    public void downloadAndSaveMapImage(String imageUrl, File destinationFile, Runnable onSuccess, Runnable onError) {
        new SaveImageTask(context, mapImageFile -> {
            if (mapImageFile.exists()) {
                onSuccess.run();
            } else {
                onError.run();
            }
        }).execute(imageUrl, destinationFile.getAbsolutePath());
    }

    // Ajoutez d'autres méthodes de téléchargement d'image si nécessaire
}
