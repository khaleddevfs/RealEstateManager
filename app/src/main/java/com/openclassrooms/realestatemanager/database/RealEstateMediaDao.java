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

    @Query("SELECT * FROM RealEstateMedia WHERE realEstateId = :realEstateID")
    LiveData<List<RealEstateMedia>> getMediaByRealEstateId(long realEstateID);

    @Query("SELECT * FROM RealEstateMedia WHERE realEstateId = :realEstateID")
    Cursor getMediaByRealEstateIdWithCursor(long realEstateID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addMedia(RealEstateMedia realEstateMedia);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMultipleMedia(List<RealEstateMedia> realEstateMediaList);

    @Delete
    void deleteMedia(RealEstateMedia realEstateMedia);

    @Query("DELETE FROM RealEstateMedia WHERE realEstateId = :realEstateID")
    int deleteAllMediaByRealEstateId(long realEstateID);

}
