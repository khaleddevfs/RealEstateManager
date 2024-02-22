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


private boolean validateRealEstateData(RealEstate realEstate) {
    if (realEstate == null) {
        Log.e("RealEstateViewModel", "L'objet RealEstate est null.");
        return false;
    }

    // Vérifiez que les champs requis ne sont pas vides ou null.
    if (realEstate.getName() == null || realEstate.getName().trim().isEmpty()) {
        Log.e("RealEstateViewModel", "Le nom du bien immobilier est requis.");
        return false;
    }

    if (realEstate.getPrice() <= 0) {
        Log.e("RealEstateViewModel", "Le prix du bien immobilier doit être supérieur à 0.");
        return false;
    }

    // Vérifiez d'autres champs selon vos règles métier...

    // Assurez-vous que la liste des médias n'est pas vide si votre logique le requiert
    if (realEstate.getMediaList() == null || realEstate.getMediaList().isEmpty()) {
        Log.e("RealEstateViewModel", "Au moins un média est requis pour le bien immobilier.");
        return false;
    }

    // Tous les champs requis sont valides
    return true;
}


    public void createOrUpdateRealEstate(RealEstate realEstate) {
        if (!validateRealEstateData(realEstate)) {
            saveOperationComplete.postValue(false);
            Log.e("RealEstateViewModel", "Échec de la validation des données RealEstate.");
            return;
        }
        LiveData<Long> realEstateIdLiveData = realEstateRepo.createOrUpdateRealEstate(realEstate);
        realEstateIdLiveData.observeForever(newId -> {
            if (newId != null && newId > 0) {
                // Si newId est non-null et > 0, cela signifie que l'opération a réussi
                realEstate.setId(newId); // Mettre à jour l'ID de l'objet RealEstate
                // Gérer la mise à jour des médias associés ici
                if (realEstate.getMediaList() != null && !realEstate.getMediaList().isEmpty()) {
                    for (RealEstateMedia media : realEstate.getMediaList()) {
                        media.setRealEstateId(newId);
                        updateMedia(media); // Mise à jour ou ajout de chaque média associé
                    }
                }
                saveOperationComplete.postValue(true);
            } else {
                // Si newId est null ou 0, l'opération a échoué
                Log.e("RealEstateViewModel", "Échec de la création ou de la mise à jour du RealEstate.");
                saveOperationComplete.postValue(false);
            }
        });
    }





   public void addNewMedia(RealEstate realEstate, long realEstateId) {
        if (realEstate.getMediaList() != null) {
            realEstate.getMediaList().forEach(media -> {
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


}
