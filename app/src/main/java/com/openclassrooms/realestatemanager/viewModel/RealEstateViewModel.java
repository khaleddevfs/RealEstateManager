package com.openclassrooms.realestatemanager.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;


import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.models.RealEstateMedia;
import com.openclassrooms.realestatemanager.repositories.RealEstateMediaRepo;
import com.openclassrooms.realestatemanager.repositories.RealEstateRepo;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

public class RealEstateViewModel extends ViewModel {

    private final RealEstateRepo realEstateRepo;
    private final RealEstateMediaRepo realEstateMediaRepo;
    private final Executor executor;

    public RealEstateViewModel(RealEstateRepo realEstateRepo, RealEstateMediaRepo realEstateMediaRepo, Executor executor) {
        this.realEstateRepo = realEstateRepo;
        this.realEstateMediaRepo = realEstateMediaRepo;
        this.executor = executor;
    }



    public LiveData<List<RealEstate>> getRealEstates() {
        return realEstateRepo.getAllRealEstates();
    }

    public LiveData<List<RealEstateMedia>> getRealEstateMediasByID(long id) {
        return realEstateMediaRepo.getRealEstateMediaByRealEstateId(id);
    }

    /*public void createOrUpdateRealEstate(RealEstate estate) {
        executor.execute(() -> {
            long id = realEstateRepo.createOrUpdateRealEstate(estate);
            Log.d("TAG", "createOrUpdateRealEstate: ID: " + estate.getID() + ", New ID: " + id);

            deleteAssociatedMedia(id);
            addNewMedia(estate, id);
        });
    }

     */

    public void deleteAssociatedMedia(long realEstateId) {
        executor.execute(() -> realEstateMediaRepo.deleteAllMediaByRealEstateID(realEstateId));
    }

   public void addNewMedia(RealEstate estate, long realEstateId) {
        if (estate.getMediaList() != null) {
            estate.getMediaList().forEach(media -> {
                media.setRealEstateId(realEstateId);
                executor.execute(() -> realEstateMediaRepo.addRealEstateMedia(media));
            });
        }
    }

    public void updateMedia(RealEstateMedia media) {
        executor.execute(() -> realEstateMediaRepo.addRealEstateMedia(media));
    }

    public LiveData<List<RealEstate>> filterEstates(String name, Date maxSaleDate, Date minListingDate, int maxPrice, int minPrice, int maxSurface, int minSurface) {
        return realEstateRepo.filterRealEstates(name, maxSaleDate, minListingDate, maxPrice, minPrice, maxSurface, minSurface);
    }

    public void updateEstateFeaturedMediaUrl(String oldUrl, String newUrl) {
        executor.execute(() -> realEstateRepo.updateFeaturedMediaUrl(oldUrl, newUrl));
    }
}
