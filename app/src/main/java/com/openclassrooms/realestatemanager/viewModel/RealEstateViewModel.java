package com.openclassrooms.realestatemanager.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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

    private MutableLiveData<Boolean> saveOperationComplete = new MutableLiveData<>();

    public LiveData<Boolean> isSaveOperationComplete() {
        return saveOperationComplete;
    }

   /* public void createOrUpdateRealEstate(RealEstate estate) {
        executor.execute(() -> {
            long id = realEstateRepo.createOrUpdateRealEstate(estate);
            Log.d("TAG", "createOrUpdateRealEstate: ID: " + estate.getID() + ", New ID: " + id);

            deleteAssociatedMedia(id);
            addNewMedia(estate, id);
        });
    }

    */


    public void createOrUpdateRealEstate(RealEstate estate) {
        executor.execute(() -> {
            long id = realEstateRepo.createOrUpdateRealEstate(estate);
            Log.d("RealEstateViewModel", "createOrUpdateRealEstate: ID: " + estate.getID() + ", New ID: " + id);

            if (id > 0) {
                estate.setID(id); // Mettez à jour l'ID de l'entité estate avec le nouvel ID
                deleteAssociatedMedia(id);

                if (estate.getMediaList() != null && !estate.getMediaList().isEmpty()) {
                    for (RealEstateMedia media : estate.getMediaList()) {
                        media.setRealEstateId(id); // Assurez-vous que l'ID de l'immobilier est correctement défini
                        executor.execute(() -> realEstateMediaRepo.addRealEstateMedia(media));
                    }
                    // Notifiez ici que l'opération de sauvegarde est complète
                    // Note: Cette notification est déclenchée de manière asynchrone
                    saveOperationComplete.postValue(true);
                } else {
                    // Si il n'y a pas de médias à ajouter, notifiez immédiatement que l'opération est terminée
                    saveOperationComplete.postValue(true);
                }
            } else {
                Log.e("RealEstateViewModel", "Failed to create or update RealEstate with ID: " + estate.getID());
                // Vous pouvez choisir de notifier un échec ici si nécessaire
                // saveOperationComplete.postValue(false);
            }
        });    }



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

    public void updateEstateFeaturedMediaUrl(long realEstateId, String newUrl) {
        executor.execute(() -> realEstateRepo.updateFeaturedMediaUrl(realEstateId, newUrl));
    }

    public void deleteRealEstate(RealEstate estate) {
        executor.execute(() -> {
            // Commencez par supprimer tous les médias associés à l'immobilier
            realEstateMediaRepo.deleteAllMediaByRealEstateID(estate.getID());

            // Ensuite, supprimez l'immobilier lui-même
            realEstateRepo.deleteRealEstate(estate);

            Log.d("RealEstateViewModel", "deleteRealEstate: Deleted RealEstate with ID: " + estate.getID());

            // Ici, vous pouvez utiliser LiveData pour notifier l'UI que la suppression est terminée
            // Par exemple, en mettant à jour une MutableLiveData<Boolean> similaire à `saveOperationComplete`
            // Cette étape est optionnelle et dépend de la manière dont vous souhaitez gérer les retours dans l'UI
        });
    }

    public void setRealEstateSoldStatus(long realEstateId, boolean isSold) {
        executor.execute(() -> realEstateRepo.setRealEstateSoldStatus(realEstateId, isSold));
    }


}
