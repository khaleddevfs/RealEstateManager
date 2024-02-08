package com.openclassrooms.realestatemanager.database;
import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.openclassrooms.realestatemanager.models.RealEstate;

import java.util.Date;
import java.util.List;

@Dao
public interface RealEstateDao {

    // Récupérer tous les biens immobiliers
    @Query("SELECT * FROM RealEstate")
    LiveData<List<RealEstate>> getAllRealEstate();

    // Créer ou mettre à jour un bien immobilier
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createOrUpdateRealEstate(RealEstate realEstate);

    // Supprimer un bien immobilier
    @Delete
    void deleteRealEstate(RealEstate realEstate);

    // Insérer plusieurs biens immobiliers
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMultipleRealEstates(List<RealEstate> realEstates);

    // Obtenir un bien immobilier par son ID avec un curseur
    @Query("SELECT * FROM RealEstate WHERE id = :id")
    Cursor getRealEstateWithCursor(long id);

    // Filtrer les biens immobiliers selon divers critères
    @Query("SELECT * FROM RealEstate WHERE (:name IS NULL OR name LIKE '%' || :name || '%') " +
            "AND (:maxPrice IS NULL OR price <= :maxPrice) " +
            "AND (:minPrice IS NULL OR price >= :minPrice) " +
            "AND (:maxSurface IS NULL OR surface <= :maxSurface) " +
            "AND (:minSurface IS NULL OR surface >= :minSurface) " +
            "AND (:maxSaleDate IS NULL OR sale_date > :maxSaleDate) " +
            "AND (:minListingDate IS NULL OR listing_date > :minListingDate)")
    LiveData<List<RealEstate>> filterRealEstates(String name, Date maxSaleDate, Date minListingDate, int maxPrice, int minPrice, int maxSurface, int minSurface);

    // Mettre à jour l'URL des médias en vedette pour un bien immobilier
  /*  @Query("UPDATE RealEstate SET featuredMediaUrl = :mediaUrl WHERE featuredMediaUrl = :oldUrl")
    void updateFeaturedMediaUrl(String oldUrl, String mediaUrl);

   */


    // Mettre à jour l'URL des médias en vedette pour un bien immobilier spécifié par son ID
    @Query("UPDATE RealEstate SET featuredMediaUrl = :featuredMediaUrl WHERE id = :realEstateId")
    void updateFeaturedMediaUrl(long realEstateId, String featuredMediaUrl);

}
