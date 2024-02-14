package com.openclassrooms.realestatemanager.dao;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

import android.database.Cursor;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.openclassrooms.realestatemanager.TestLifecycleOwner;
import com.openclassrooms.realestatemanager.database.RealEstateMediaDao;
import com.openclassrooms.realestatemanager.database.SaveRealEstateDB;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.models.RealEstateMedia;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class RealEstateMediaDaoTest extends TestCase {

    private RealEstateMediaDao dao;
    private SaveRealEstateDB testDb;
    private final TestLifecycleOwner lifecycleOwner = new TestLifecycleOwner();

    @Override
    public void setUp() throws Exception {

        testDb = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), SaveRealEstateDB.class).build();

        dao = testDb.realEstateMediaDao();
        super.setUp();
    }
    @Override
    protected void tearDown() throws Exception {
        testDb.close();
        super.tearDown();
    }


    public void testGetMediaByRealEstateId() throws Throwable {
        runOnUiThread(() -> dao.getMediaByRealEstateId(1).observe(lifecycleOwner, medias -> assertEquals(4,medias.size())));
    }

    public void testGetMediaByRealEstateIdWithCursor() throws Throwable {
        runOnUiThread(() -> dao.getMediaByRealEstateId(1).observe(lifecycleOwner, realEstateMedias -> {
            Cursor res = dao.getMediaByRealEstateIdWithCursor(1);
            assertEquals(realEstateMedias.get(0).getID(),res.getLong(res.getColumnIndex("mID")));
        }));
    }

    public void testAddMedia() throws Throwable {
        RealEstate estate = new RealEstate();
        estate.setID(1000);

        testDb.realEstateDao().createOrUpdateRealEstate(estate);

        dao.addMedia(new RealEstateMedia(1000,"test","test"));
        runOnUiThread(() -> dao.getMediaByRealEstateId(1000).observe(lifecycleOwner, medias -> assertEquals(1,medias.size())));
    }

    public void testInsertMultipleMedia() throws Throwable {
        List<RealEstateMedia> medias = new ArrayList<>();
        RealEstate estate = new RealEstate();
        estate.setID(1000);

        testDb.realEstateDao().createOrUpdateRealEstate(estate);

        medias.add(new RealEstateMedia(1000,"test","test"));
        medias.add(new RealEstateMedia(1000,"test2","test2"))  ;

        dao.insertMultipleMedia(medias);

        runOnUiThread(() -> dao.getMediaByRealEstateId(1000).observe(lifecycleOwner, actualMedias -> assertEquals(2,actualMedias.size())));



    }

    public void testDeleteMedia() throws Throwable {
        List<RealEstateMedia> medias = new ArrayList<>();
        RealEstate estate = new RealEstate();
        estate.setID(1000);

        testDb.realEstateDao().createOrUpdateRealEstate(estate);

        medias.add(new RealEstateMedia(1000,"test","test"));
        medias.add(new RealEstateMedia(1000,"test2","test2"))  ;

        dao.insertMultipleMedia(medias);

        runOnUiThread(() -> dao.getMediaByRealEstateId(1000).observe(lifecycleOwner, actualMedias -> {
            assertEquals(2,actualMedias.size());
            dao.deleteMedia(actualMedias.get(0));
            assertEquals(1,actualMedias.size());
        }));

    }

    public void testDeleteAllMediaByRealEstateId() throws Throwable {
        List<RealEstateMedia> medias = new ArrayList<>();
        RealEstate estate = new RealEstate();
        estate.setID(1000);

        testDb.realEstateDao().createOrUpdateRealEstate(estate);

        medias.add(new RealEstateMedia(1000,"test","test"));
        medias.add(new RealEstateMedia(1000,"test2","test2"))  ;

        dao.insertMultipleMedia(medias);

        runOnUiThread(() -> dao.getMediaByRealEstateId(1000).observe(lifecycleOwner, actualMedias -> {
            assertEquals(2,actualMedias.size());
            dao.deleteAllMediaByRealEstateId(1000);
            assertTrue(actualMedias.isEmpty());
        }));
    }
}