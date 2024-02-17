package com.openclassrooms.realestatemanager.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.models.RealEstateMedia;

/*
@Database(entities = {RealEstate.class, RealEstateMedia.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class SaveRealEstateDB extends RoomDatabase {

    private static volatile SaveRealEstateDB INSTANCE;

    public abstract RealEstateDao realEstateDao();

    public abstract RealEstateMediaDao realEstateMediaDao();

    public static SaveRealEstateDB getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SaveRealEstateDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), SaveRealEstateDB.class, "MyDatabase.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}

 */





import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.models.RealEstateMedia;

@Database(entities = {RealEstate.class, RealEstateMedia.class}, version = 2, exportSchema = false) // Attention à bien mettre la version actuelle
@TypeConverters({Converters.class})
public abstract class SaveRealEstateDB extends RoomDatabase {

    private static volatile SaveRealEstateDB INSTANCE;

    public abstract RealEstateDao realEstateDao();
    public abstract RealEstateMediaDao realEstateMediaDao();

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE RealEstate ADD COLUMN isSold INTEGER NOT NULL DEFAULT 0");
        }
    };


    public static SaveRealEstateDB getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SaveRealEstateDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    SaveRealEstateDB.class, "MyDatabase.db")
                            .addMigrations(MIGRATION_3_4) // Ajoutez cette ligne pour appliquer la migration
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}







