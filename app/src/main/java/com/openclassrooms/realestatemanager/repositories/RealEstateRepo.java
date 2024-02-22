package com.openclassrooms.realestatemanager.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
        this.executor = Executors.newSingleThreadExecutor();
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
    /*
    public void createOrUpdateRealEstate(RealEstate realEstate) {
        if (realEstate == null) {
            Log.e("RealEstateRepo", "Tentative de sauvegarde d'un RealEstate null.");
            return; // Arrêtez l'exécution si realEstate est null.
        }
        executor.execute(() -> {
            realEstateDao.createOrUpdateRealEstate(realEstate);
            Log.d("RealEstateRepo", "RealEstate créé ou mis à jour avec succès.");
        });
    }

     */
    public LiveData<Long> createOrUpdateRealEstate(RealEstate realEstate) {
        MutableLiveData<Long> resultId = new MutableLiveData<>();
        if (realEstate == null) {
            resultId.postValue(null); // Post null if realEstate is null
            return resultId;
        }
        executor.execute(() -> {
            long id = realEstateDao.createOrUpdateRealEstate(realEstate);
            resultId.postValue(id); // Post the result ID on the main thread
        });
        return resultId;
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
