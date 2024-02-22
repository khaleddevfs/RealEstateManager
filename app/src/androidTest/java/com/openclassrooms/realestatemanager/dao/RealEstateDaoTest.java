package com.openclassrooms.realestatemanager.dao;
/*
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.openclassrooms.realestatemanager.database.RealEstateDao;
import com.openclassrooms.realestatemanager.database.SaveRealEstateDB;
import com.openclassrooms.realestatemanager.models.RealEstate;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.database.Cursor;

@RunWith(MockitoJUnitRunner.class)
public class RealEstateDaoTest {

    private RealEstateDao realEstateDao;
    private SaveRealEstateDB db;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getContext(),
                SaveRealEstateDB.class).allowMainThreadQueries().build();
        realEstateDao = db.realEstateDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void getAllRealEstate_returnsLiveDataList() {
        LiveData<List<RealEstate>> testLiveData = new MutableLiveData<>();
        when(realEstateDao.getAllRealEstate()).thenReturn(testLiveData);

        LiveData<List<RealEstate>> returnedData = realEstateDao.getAllRealEstate();
        assertNotNull(returnedData);
        verify(realEstateDao).getAllRealEstate();
    }

    @Test
    public void createOrUpdateRealEstate_createsOrUpdatesSuccessfully() {
        RealEstate realEstate = new RealEstate(); // Mock real estate object
        realEstateDao.createOrUpdateRealEstate(realEstate);
        verify(realEstateDao).createOrUpdateRealEstate(realEstate);
    }

    @Test
    public void deleteRealEstate_deletesSuccessfully() {
        RealEstate realEstate = new RealEstate(); // Mock real estate object
        realEstateDao.deleteRealEstate(realEstate);
        verify(realEstateDao).deleteRealEstate(realEstate);
    }

    @Test
    public void insertMultipleRealEstates_insertsSuccessfully() {
        List<RealEstate> realEstates = new ArrayList<>(); // Mock real estate list
        realEstateDao.insertMultipleRealEstates(realEstates);
        verify(realEstateDao).insertMultipleRealEstates(realEstates);
    }

    @Test
    public void getRealEstateWithCursor_returnsCursor() {
        long id = 1L; // Example ID
        Cursor cursor = realEstateDao.getRealEstateWithCursor(id);
        assertNotNull(cursor);
        verify(realEstateDao).getRealEstateWithCursor(id);
    }

    @Test
    public void filterRealEstates_returnsFilteredLiveDataList() {
        LiveData<List<RealEstate>> testLiveData = new MutableLiveData<>();
        String name = "Test";
        Date maxSaleDate = new Date();
        Date minListingDate = new Date();
        int maxPrice = 1000000;
        int minPrice = 500000;
        int maxSurface = 200;
        int minSurface = 100;

        when(realEstateDao.filterRealEstates(name, maxSaleDate, minListingDate, maxPrice, minPrice, maxSurface, minSurface)).thenReturn(testLiveData);

        LiveData<List<RealEstate>> returnedData = realEstateDao.filterRealEstates(name, maxSaleDate, minListingDate, maxPrice, minPrice, maxSurface, minSurface);
        assertNotNull(returnedData);
        verify(realEstateDao).filterRealEstates(name, maxSaleDate, minListingDate, maxPrice, minPrice, maxSurface, minSurface);
    }

    @Test
    public void updateFeaturedMediaUrl_updatesSuccessfully() {
        long realEstateId = 1L;
        String featuredMediaUrl = "http://example.com/image.jpg";
        realEstateDao.updateFeaturedMediaUrl(realEstateId, featuredMediaUrl);
        verify(realEstateDao).updateFeaturedMediaUrl(realEstateId, featuredMediaUrl);
    }
}

 */


import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

import android.database.Cursor;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.openclassrooms.realestatemanager.TestLifecycleOwner;
import com.openclassrooms.realestatemanager.database.RealEstateDao;
import com.openclassrooms.realestatemanager.database.SaveRealEstateDB;
import com.openclassrooms.realestatemanager.models.RealEstate;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RealEstateDaoTest extends TestCase {

    private RealEstateDao dao;
    private SaveRealEstateDB testDb;
    private final TestLifecycleOwner lifecycleOwner = new TestLifecycleOwner();

    public void testGetAllRealEstate() throws Throwable {
        runOnUiThread(() -> dao.getAllRealEstate().observe(lifecycleOwner, realEstates -> assertEquals(5,realEstates.size())));

    }

    public void testCreateOrUpdateRealEstate() throws Throwable {
        // Create a mock RealEstate object
        RealEstate mockRealEstate = new RealEstate();
        dao.createOrUpdateRealEstate(mockRealEstate);

        // Mock the behavior of the DAO method
        runOnUiThread(() -> dao.getAllRealEstate().observe(lifecycleOwner, realEstates -> assertEquals(6,realEstates.size())));

    }

    public void testDeleteRealEstate() throws Throwable {
        RealEstate mockRealEstate = new RealEstate();
        mockRealEstate.setId(1L);

        dao.deleteRealEstate(mockRealEstate);

        runOnUiThread(() -> dao.getAllRealEstate().observe(lifecycleOwner, realEstates -> assertEquals(4,realEstates.size())));
    }

    public void testInsertMultipleRealEstates() throws Throwable {
        List<RealEstate> estates = new ArrayList<>();
        estates.add(new RealEstate());
        estates.add(new RealEstate());

        // Call the method to insert multiple real estates
        dao.insertMultipleRealEstates(estates);

        runOnUiThread(() -> dao.getAllRealEstate().observe(lifecycleOwner, realEstates -> assertEquals(7,realEstates.size())));

    }

    public void testGetRealEstateWithCursor() throws Throwable {


        runOnUiThread(() -> dao.getAllRealEstate().observe(lifecycleOwner, realEstates -> {
            assertEquals(5,realEstates.size());
            Cursor res = dao.getRealEstateWithCursor(realEstates.get(0).getId());
            assertEquals(realEstates.get(0).getId(),res.getLong(res.getColumnIndex("mID")));
        }));



    }

    public void testFilterRealEstates() throws Throwable {
        // Create mock input parameters
        String name = "example";
        Date maxSaleDate = new Date();
        Date minListingDate = new Date();
        int maxPrice = 100000;
        int minPrice = 50000;

        int maxSurface = 2000;
        int minSurface = 1000;




        runOnUiThread(()-> dao.filterRealEstates(name, maxSaleDate, minListingDate, maxPrice,
                minPrice, maxSurface, minSurface).observe(lifecycleOwner, realEstates -> {
            assert realEstates != null;
            assertEquals(1, realEstates.size());
            assertEquals("example",realEstates.get(0).getName());
        }));





    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        testDb = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), SaveRealEstateDB.class).build();

        dao = testDb.realEstateDao();

        List<RealEstate> estates = new ArrayList<>();

        estates.add(new RealEstate("example",  "region",  "location",  "description",  "featuredMediaUrl", 60000, 1500, 3, 1, 2, null ));
        estates.add(new RealEstate("example2",  "region",  "location",  "description",  "featuredMediaUrl", 60000, 1500, 3, 1, 2, null ));

        dao.insertMultipleRealEstates(estates);
    }
    @Override
    protected void tearDown() throws Exception {
        testDb.close();
        super.tearDown();
    }
}