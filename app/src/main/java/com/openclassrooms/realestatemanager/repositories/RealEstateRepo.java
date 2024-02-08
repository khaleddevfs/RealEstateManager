package com.openclassrooms.realestatemanager.repositories;

import androidx.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.database.RealEstateDao;
import com.openclassrooms.realestatemanager.models.RealEstate;

import java.util.Date;
import java.util.List;

/**
 * Repository for Real Estate operations.
 */
public class RealEstateRepo {
    private final RealEstateDao realEstateDao;

    public RealEstateRepo(RealEstateDao realEstateDao) {
        this.realEstateDao = realEstateDao;
    }

    /**
     * Get all real estates.
     */
    public LiveData<List<RealEstate>> getAllRealEstates() {
        return realEstateDao.getAllRealEstate();
    }

    /**
     * Create or update a real estate.
     */
    public long createOrUpdateRealEstate(RealEstate estate) {
        return realEstateDao.createOrUpdateRealEstate(estate);
    }

    /**
     * Filter real estates based on various criteria.
     */
    public LiveData<List<RealEstate>> filterRealEstates(String name, Date maxSaleDate, Date minListingDate, int maxPrice, int minPrice, int maxSurface, int minSurface) {
        return realEstateDao.filterRealEstates(name, maxSaleDate, minListingDate, maxPrice, minPrice, maxSurface, minSurface);
    }

    /**
     * Update the featured media URL of a real estate.
     */
    public void updateFeaturedMediaUrl(long realEstateId, String featuredMediaUrl) {
        realEstateDao.updateFeaturedMediaUrl(realEstateId, featuredMediaUrl);
    }
}
