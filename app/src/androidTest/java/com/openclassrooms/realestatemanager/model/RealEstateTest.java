package com.openclassrooms.realestatemanager.model;

import android.content.ContentValues;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.models.RealEstateMedia;

import junit.framework.TestCase;

import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RealEstateTest extends TestCase {

    public void testGetJsonPoint() {
        RealEstate estate = new RealEstate();
        estate.setJsonPoint("test");
        assertEquals("test",estate.getJsonPoint());
    }

    public void testSetJsonPoint() {
        RealEstate estate = new RealEstate();
        estate.setJsonPoint("test");
        assertEquals("test",estate.getJsonPoint());
    }

    public void testGetSync() {
        RealEstate estate = new RealEstate();
        estate.setSync(false);
        assertFalse(estate.getSync());
    }

    public void testSetSync() {
        RealEstate estate = new RealEstate();
        estate.setSync(false);
        assertFalse(estate.getSync());
    }

    public void testSetListingDate() {
        RealEstate estate = new RealEstate();
        Date date = new Date();
        estate.setListingDate(date);
        assertEquals(date,estate.getListingDate());

    }

    public void testGetAgentName() {
        RealEstate realEstate = new RealEstate();
        realEstate.setAgentName("agent name");
        assertEquals("agent name", realEstate.getAgentName());
    }

    public void testSetAgentName() {
        RealEstate realEstate = new RealEstate();
        realEstate.setAgentName("agent name");
        assertEquals("agent name", realEstate.getAgentName());
    }


    public void testGetListingDate() {
        RealEstate estate = new RealEstate();
        Date date = new Date();
        estate.setListingDate(date);
        assertEquals(date,estate.getListingDate());
    }

    public void testGetSaleDate() {
        RealEstate estate = new RealEstate();
        Date date = new Date();
        estate.setSaleDate(date);
        assertEquals(date,estate.getSaleDate());
    }

    public void testSetSaleDate() {
        RealEstate estate = new RealEstate();
        Date date = new Date();
        estate.setSaleDate(date);
        assertEquals(date,estate.getSaleDate());
    }

    public void testGetDescription() {
        RealEstate estate = new RealEstate();
        estate.setDescription("estate description");
        assertEquals("estate description", estate.getDescription());
    }

    public void testSetDescription() {
        RealEstate estate = new RealEstate();
        estate.setDescription("estate description");
        assertEquals("estate description", estate.getDescription());
    }

    public void testGetID() {
        RealEstate estate = new RealEstate();
        estate.setID(10000L);
        assertEquals(10000L,estate.getID());
    }

    public void testSetID() {
        RealEstate estate = new RealEstate();
        estate.setID(10000L);
        assertEquals(10000L,estate.getID());
    }

    public void testGetLocation() {
        RealEstate estate = new RealEstate();
        estate.setLocation("My location");
        assertEquals("My location",estate.getLocation());
    }

    public void testSetLocation() {
        RealEstate estate = new RealEstate();
        estate.setLocation("My location");
        assertEquals("My location",estate.getLocation());
    }

    public void testGetSurface() {
        RealEstate estate = new RealEstate();
        estate.setSurface(1000);
        assertEquals(1000,estate.getSurface());
    }

    public void testSetSurface() {
        RealEstate estate = new RealEstate();
        estate.setSurface(1000);
        assertEquals(1000,estate.getSurface());
    }

    public void testGetRooms() {
        RealEstate estate = new RealEstate();
        estate.setRooms(10);
        assertEquals(10,estate.getRooms());
    }

    public void testSetRooms() {
        RealEstate estate = new RealEstate();
        estate.setRooms(10);
        assertEquals(10,estate.getRooms());
    }

    public void testGetBathrooms() {
        RealEstate estate = new RealEstate();
        estate.setBathrooms(10);
        assertEquals(10,estate.getBathrooms());
    }

    public void testSetBathrooms() {
        RealEstate estate = new RealEstate();
        estate.setBathrooms(10);
        assertEquals(10,estate.getBathrooms());
    }

    public void testGetBedrooms() {
        RealEstate estate = new RealEstate();
        estate.setBedrooms(10);
        assertEquals(10,estate.getBedrooms());
    }

    public void testSetBedrooms() {
        RealEstate estate = new RealEstate();
        estate.setBedrooms(10);
        assertEquals(10,estate.getBedrooms());
    }

    public void testGetFeaturedMediaUrl() {
        RealEstate estate = new RealEstate();
        estate.setFeaturedMediaUrl("https://thisistheurl.com/media.jpg");
        assertEquals("https://thisistheurl.com/media.jpg",estate.getFeaturedMediaUrl());
    }

    public void testSetFeaturedMediaUrl() {
        RealEstate estate = new RealEstate();
        estate.setFeaturedMediaUrl("https://thisistheurl.com/media.jpg");
        assertEquals("https://thisistheurl.com/media.jpg",estate.getFeaturedMediaUrl());
    }

    public void testGetMediaList() {
        RealEstate estate = new RealEstate();
        List<RealEstateMedia> mediaList = new ArrayList<>();
        mediaList.add(new RealEstateMedia("url1","caption 1"));
        mediaList.add(new RealEstateMedia("url2","caption 2"));
        mediaList.add(new RealEstateMedia("url3","caption 3"));
        estate.setMediaList(mediaList);
        assertSame(mediaList,estate.getMediaList());

    }

    public void testSetMediaList() {
        RealEstate estate = new RealEstate();
        List<RealEstateMedia> mediaList = new ArrayList<>();
        mediaList.add(new RealEstateMedia("url1","caption 1"));
        mediaList.add(new RealEstateMedia("url2","caption 2"));
        mediaList.add(new RealEstateMedia("url3","caption 3"));
        estate.setMediaList(mediaList);
        assertSame(mediaList,estate.getMediaList());
    }

    public void testTestGetName() {
        RealEstate estate = new RealEstate();
        estate.setName("name");
        assertEquals("name",estate.getName());
    }

    public void testTestSetName() {
        RealEstate estate = new RealEstate();
        estate.setName("name");
        assertEquals("name",estate.getName());
    }

    public void testGetRegion() {
        RealEstate estate = new RealEstate();
        estate.setRegion("region");
        assertEquals("region",estate.getRegion());
    }

    public void testSetRegion() {
        RealEstate estate = new RealEstate();
        estate.setRegion("region");
        assertEquals("region",estate.getRegion());
    }

    public void testGetPrice() {
        RealEstate estate = new RealEstate();
        estate.setPrice(1000000);
        assertEquals(1000000,estate.getPrice());
    }

    public void testSetPrice() {
        RealEstate estate = new RealEstate();
        estate.setPrice(1000000);
        assertEquals(1000000,estate.getPrice());
    }

    public void testToHashMap() {
        RealEstate estate = new RealEstate("name","region","location","description","url",1000,1,1,1,1,null);
        HashMap<String, Object> map = new HashMap<>();

        Date date= new Date();
        estate.setListingDate(date);
        estate.setSaleDate(date);
        estate.setJsonPoint("json");
        estate.setAgentName("agent");

        map.put("listingDate", date);
        map.put("saleDate",date);
        map.put("name", "name");
        map.put("jsonPoint", "json");
        map.put("region", "region");
        map.put("location", "location");
        map.put("description", "description");
        map.put("featuredMediaUrl", "url");
        map.put("agentName", "agent");
        map.put("price", 1000);
        map.put("Surface", 1);
        map.put("rooms", 1);
        map.put("bathrooms", 1);
        map.put("bedrooms", 1);

        HashMap<String,Object> real = estate.toHashMap();
        assertEquals(map,real);
    }

    public void testTestEquals() {
        RealEstate estate1 = new RealEstate();
        RealEstate estate2 = new RealEstate();
        estate1.setID(1);
        estate2.setID(1);
        assertEquals(estate1,estate2);
    }


    public void testFromQueryDocumentSnapshot() {
        QueryDocumentSnapshot document = Mockito.mock(QueryDocumentSnapshot.class);
        Mockito.when(document.getString("agentName")).thenReturn("Agent Smith");
        Mockito.when(document.getId()).thenReturn("Agent Smith12345");
        Mockito.when(document.getString("name")).thenReturn("Beautiful House");
        Mockito.when(document.getString("region")).thenReturn("City");
        Mockito.when(document.getString("location")).thenReturn("123 Street");
        Mockito.when(document.getString("description")).thenReturn("Spacious and modern house");
        Mockito.when(document.getLong("price")).thenReturn(200000L);
        Mockito.when(document.getLong("Surface")).thenReturn(150L);
        Mockito.when(document.getLong("rooms")).thenReturn(5L);
        Mockito.when(document.getLong("bathrooms")).thenReturn(2L);
        Mockito.when(document.getLong("bedrooms")).thenReturn(3L);
        Mockito.when(document.getString("featuredMediaUrl")).thenReturn("http://example.com/image.jpg");
        Mockito.when(document.getDate("listingDate")).thenReturn(new Date());

        RealEstate expectedRealEstate = new RealEstate(12345L, "Beautiful House", "City", "123 Street",
                "Spacious and modern house", 200000, 150, 5, 2, 3, "http://example.com/image.jpg", "Agent Smith");
        expectedRealEstate.setSync(true);
        expectedRealEstate.setListingDate(new Date());

        RealEstate actualRealEstate = RealEstate.fromQueryDocumentSnapshot(document);

        assertEquals(expectedRealEstate, actualRealEstate);

    }

    public void testFromContentValues() {
        ContentValues values = new ContentValues();
        values.put("iD", 12345L);
        values.put("name", "Beautiful House");
        values.put("jsonPoint", "{latitude: 123, longitude: 456}");
        values.put("region", "City");
        values.put("location", "123 Street");
        values.put("description", "Spacious and modern house");
        values.put("featuredMediaUrl", "http://example.com/image.jpg");
        values.put("agentName", "Agent Smith");
        values.put("price", 200000);
        values.put("surface", 150);
        values.put("rooms", 5);
        values.put("bathrooms", 2);
        values.put("bedrooms", 3);
        values.put("listing_date", new Date().getTime());
        values.put("sale_date", new Date().getTime());

        RealEstate expectedRealEstate = new RealEstate();
        expectedRealEstate.setID(12345L);
        expectedRealEstate.setName("Beautiful House");
        expectedRealEstate.setJsonPoint("{latitude: 123, longitude: 456}");
        expectedRealEstate.setRegion("City");
        expectedRealEstate.setLocation("123 Street");
        expectedRealEstate.setDescription("Spacious and modern house");
        expectedRealEstate.setFeaturedMediaUrl("http://example.com/image.jpg");
        expectedRealEstate.setAgentName("Agent Smith");
        expectedRealEstate.setPrice(200000);
        expectedRealEstate.setSurface(150);
        expectedRealEstate.setRooms(5);
        expectedRealEstate.setBathrooms(2);
        expectedRealEstate.setBedrooms(3);
        expectedRealEstate.setListingDate(new Date(values.getAsLong("listing_date")));
        expectedRealEstate.setSaleDate(new Date(values.getAsLong("sale_date")));

        RealEstate actualRealEstate = RealEstate.fromContentValues(values);

        assertEquals(expectedRealEstate, actualRealEstate);
    }
}
