package com.openclassrooms.realestatemanager.database;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.openclassrooms.realestatemanager.models.RealEstateMedia;

import java.util.List;

@Dao
public interface RealEstateMediaDao {

    // Obtenir tous les médias associés à un bien immobilier spécifique
    @Query("SELECT * FROM RealEstateMedia WHERE realEstateId = :realEstateID")
    LiveData<List<RealEstateMedia>> getMediaByRealEstateId(long realEstateID);

    // Obtenir tous les médias associés à un bien immobilier spécifique avec un curseur
    @Query("SELECT * FROM RealEstateMedia WHERE realEstateId = :realEstateID")
    Cursor getMediaByRealEstateIdWithCursor(long realEstateID);

    // Ajouter un média pour un bien immobilier
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addMedia(RealEstateMedia realEstateMedia);

    // Insérer plusieurs médias en une fois
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMultipleMedia(List<RealEstateMedia> realEstateMediaList);

    // Supprimer un média spécifique
    @Delete
    void deleteMedia(RealEstateMedia realEstateMedia);

    // Supprimer tous les médias associés à un bien immobilier spécifique
    @Query("DELETE FROM RealEstateMedia WHERE realEstateId = :realEstateID")
    int deleteAllMediaByRealEstateId(long realEstateID);

}
