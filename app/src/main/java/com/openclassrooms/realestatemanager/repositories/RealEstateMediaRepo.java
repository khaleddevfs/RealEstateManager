package com.openclassrooms.realestatemanager.repositories;

import androidx.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.database.RealEstateMediaDao;
import com.openclassrooms.realestatemanager.models.RealEstateMedia;

import java.util.List;

/**
 * Repository for managing Real Estate Media operations.
 */
public class RealEstateMediaRepo {
    private final RealEstateMediaDao realEstateMediaDao;

    public RealEstateMediaRepo(RealEstateMediaDao realEstateDao) {
        this.realEstateMediaDao = realEstateDao;
    }

    /**
     * Retrieve media items associated with a specific real estate ID.
     */
    public LiveData<List<RealEstateMedia>> getRealEstateMediaByRealEstateId(long realEstateId) {
        return realEstateMediaDao.getMediaByRealEstateId(realEstateId);
    }

    /**
     * Add a media item for a real estate property.
     */
    public void addRealEstateMedia(RealEstateMedia media) {
        realEstateMediaDao.addMedia(media);
    }

    /**
     * Delete all media items associated with a specific real estate ID.
     */
    public void deleteAllMediaByRealEstateID(long realEstateId) {
        realEstateMediaDao.deleteAllMediaByRealEstateId(realEstateId);
    }

    // Additional methods for real estate media management could be added here.
}
