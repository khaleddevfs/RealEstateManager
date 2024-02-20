package com.openclassrooms.realestatemanager.utils;

import java.io.File;

public interface ImageDownloadCallback {

    void onImageSaved(File imageFile);
    void onError(Exception e);
}
