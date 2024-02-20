package com.openclassrooms.realestatemanager.repositories;

import androidx.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.database.RealEstateDao;
import com.openclassrooms.realestatemanager.models.RealEstate;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Repository for Real Estate operations.
 */
public class RealEstateRepo {
    private final RealEstateDao realEstateDao;
    private final Executor executor;


    public RealEstateRepo(RealEstateDao realEstateDao) {
        this.realEstateDao = realEstateDao;
        this.executor = Executors.newSingleThreadExecutor(); // Créez un Executor pour les opérations asynchrones
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

    public void deleteRealEstate(RealEstate estate) {
        realEstateDao.deleteRealEstate(estate);
    }

    /**
     * Filter real estates based on various criteria.
     */
    public LiveData<List<RealEstate>> filterRealEstates(String name, Date maxSaleDate, Date minListingDate, int maxPrice, int minPrice, int maxSurface, int minSurface) {
        return realEstateDao.filterRealEstates(name, maxSaleDate, minListingDate, maxPrice, minPrice, maxSurface, minSurface);
    }

    public LiveData<RealEstate> getRealEstateById(long id) {
        return realEstateDao.getRealEstateById(id);
    }

    /**
     * Update the featured media URL of a real estate.
     */

    public void setRealEstateSoldStatus(long realEstateId, boolean isSold) {
        executor.execute(() -> realEstateDao.setRealEstateSoldStatus(realEstateId, isSold));
    }
    public void updateFeaturedMediaUrl(long realEstateId, String featuredMediaUrl) {
        realEstateDao.updateFeaturedMediaUrl(realEstateId, featuredMediaUrl);
    }
}
