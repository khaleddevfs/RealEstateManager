package com.openclassrooms.realestatemanager.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.List;
/*
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class RealEstate implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    public String name;
    private String jsonPoint;
    public String region;
    private String location;
    private String description;
    private String featuredMediaUrl;
    private String agentName;

    private int price, surface, rooms, bathrooms, bedrooms;

    @ColumnInfo(name = "listing_date")
    private Date listingDate;

    @ColumnInfo(name = "sale_date")
    private Date saleDate;

    @Ignore
    private Boolean isSync = false;

    @Ignore
    private List<RealEstateMedia> mediaList;

    protected RealEstate(Parcel in) {
        id = in.readLong();
        name = in.readString();
        jsonPoint = in.readString();
        region = in.readString();
        location = in.readString();
        description = in.readString();
        featuredMediaUrl = in.readString();
        agentName = in.readString();
        price = in.readInt();
        surface = in.readInt();
        rooms = in.readInt();
        bathrooms = in.readInt();
        bedrooms = in.readInt();
        listingDate = new Date(in.readLong());
        saleDate = new Date(in.readLong());
        isSync = in.readByte() != 0;
        mediaList = in.createTypedArrayList(RealEstateMedia.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(jsonPoint);
        dest.writeString(region);
        dest.writeString(location);
        dest.writeString(description);
        dest.writeString(featuredMediaUrl);
        dest.writeString(agentName);
        dest.writeInt(price);
        dest.writeInt(surface);
        dest.writeInt(rooms);
        dest.writeInt(bathrooms);
        dest.writeInt(bedrooms);
        dest.writeLong(listingDate != null ? listingDate.getTime() : 0);
        dest.writeLong(saleDate != null ? saleDate.getTime() : 0);
        dest.writeByte((byte) (isSync ? 1 : 0));
        dest.writeTypedList(mediaList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<RealEstate> CREATOR = new Parcelable.Creator<RealEstate>() {
        @Override
        public RealEstate createFromParcel(Parcel in) {
            return new RealEstate(in);
        }

        @Override
        public RealEstate[] newArray(int size) {
            return new RealEstate[size];
        }
    };


    public List<RealEstateMedia> getMediaList() {
        return mediaList;
    }


    public long getId() {
        return id;
    }
}

 */

import android.content.ContentValues;
import android.util.Log;


import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

@Entity
public class RealEstate implements Parcelable {

  //  private  double latitude = Double.MIN_VALUE; // Utiliser transient pour exclure de Room
  //  private double longitude = Double.MIN_VALUE;

    @ColumnInfo(name = "listing_date")
    Date listingDate;
    @ColumnInfo(name = "sale_date")
    Date saleDate;
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "is_sold")
    private boolean isSold;
    private String name;

    private String jsonPoint;

    private String region;
    private String location;
    private String description;
    private String featuredMediaUrl;
    @Ignore
    private Boolean isSync = false;

    private String agentName;
    private int price,
            surface,
            rooms,
            bathrooms,
            bedrooms;
    @Ignore
    private List<RealEstateMedia> mediaList;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "address")
    private String address;


    protected RealEstate(Parcel in) {
        id = in.readLong();
        name = in.readString();
        jsonPoint = in.readString();
        region = in.readString();
        location = in.readString();
        description = in.readString();
        featuredMediaUrl = in.readString();
        byte tmpIsSync = in.readByte();
        isSync = tmpIsSync == 1;
        agentName = in.readString();
        price = in.readInt();
        surface = in.readInt();
        rooms = in.readInt();
        bathrooms = in.readInt();
        bedrooms = in.readInt();
        mediaList = in.createTypedArrayList(RealEstateMedia.CREATOR);
        isSold = in.readByte() != 0; // isSold


        long l = in.readLong();
        if(l != 0) {
            saleDate = new Date(l);
            Log.d("TAG", "read: " + saleDate);
        }
        else {
            saleDate = null;
        }
        l = in.readLong();
        if(l != 0) {
            listingDate = new Date(l);
        }
        else {
            listingDate = null;
        }

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(jsonPoint);
        dest.writeString(region);
        dest.writeString(location);
        dest.writeString(description);
        dest.writeString(featuredMediaUrl);
        dest.writeByte((byte) (isSync != null && isSync ? 1 : 0));
        dest.writeString(agentName);
        dest.writeInt(price);
        dest.writeInt(surface);
        dest.writeInt(rooms);
        dest.writeInt(bathrooms);
        dest.writeInt(bedrooms);
        dest.writeTypedList(mediaList);

        dest.writeByte((byte) (isSold ? 1 : 0)); // isSold

        if(saleDate == null)
        {
            dest.writeLong(0);

        }
        else {
            Log.d("TAG", "writeToParcel: " + saleDate);
            dest.writeLong(saleDate.getTime());
        }

        if (listingDate != null)
        {
            dest.writeLong(listingDate.getTime());
        }
        else {
            dest.writeLong(0);
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RealEstate> CREATOR = new Creator<RealEstate>() {
        @Override
        public RealEstate createFromParcel(Parcel in) {
            return new RealEstate(in);
        }

        @Override
        public RealEstate[] newArray(int size) {
            return new RealEstate[size];
        }
    };


    public String getJsonPoint() {
        return jsonPoint;
    }

    public void setJsonPoint(String jsonPoint) {
        this.jsonPoint = jsonPoint;
        parseJsonPoint();
    }
    // Méthode privée pour analyser jsonPoint et stocker latitude et longitude
    private void parseJsonPoint() {
        if (jsonPoint == null || jsonPoint.isEmpty()) {
            latitude = 0.0;
            longitude = 0.0;
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonPoint);
            latitude = jsonObject.optDouble("latitude", 0.0);
            longitude = jsonObject.optDouble("longitude", 0.0);
        } catch (JSONException e) {
            Log.e("RealEstate", "Erreur lors de l'analyse de jsonPoint", e);
            latitude = 0.0;
            longitude = 0.0;
        }
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    // Utiliser les valeurs stockées pour la latitude et la longitude
    public double getLatitude() {
        if (latitude == Double.MIN_VALUE) {
            parseJsonPoint();
        }
        return latitude;
    }

    public double getLongitude() {
        if (longitude == Double.MIN_VALUE) {
            parseJsonPoint();
        }
        return longitude;
    }


    public Boolean getSync() {
        return isSync;
    }

    public void setSync(Boolean sync) {
       this.isSync = sync;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }






    public RealEstate() {
        // require empty constructor
    }
    @Ignore
    public RealEstate(long id, String mName, String mRegion, String mLocation, String mDescription, int mPrice, int mSurface, int mRooms, int mBathrooms, int mBedrooms, String mFeaturedMediaUrl, String mAgentName) {
        this.id = id;
        name = mName;
        region = mRegion;
        location = mLocation;
        description = mDescription;
        price = mPrice;
        surface = mSurface;
        rooms = mRooms;
        bathrooms = mBathrooms;
        bedrooms = mBedrooms;
        featuredMediaUrl = mFeaturedMediaUrl;
        listingDate = new Date();
        agentName = mAgentName;
    }

    @Ignore
    public RealEstate(long id, String mName, String mRegion, int mPrice, String mFeaturedMediaUrl) {
        this.id = id;
        name = mName;
        region = mRegion;
        price = mPrice;
        featuredMediaUrl = mFeaturedMediaUrl;
    }

    public RealEstate(String mName, String mRegion, String mLocation, String mDescription, String mFeaturedMediaUrl, int mPrice, int mSurface, int mRooms, int mBathrooms, int mBedrooms, List<RealEstateMedia> mMediaList) {
        name = mName;
        region = mRegion;
        location = mLocation;
        description = mDescription;
        featuredMediaUrl = mFeaturedMediaUrl;
        price = mPrice;
        surface = mSurface;
        rooms = mRooms;
        bathrooms = mBathrooms;
        bedrooms = mBedrooms;
        mediaList = mMediaList;
    }


    public Date getListingDate() {
        return listingDate;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSurface() {
        return surface;
    }

    public void setSurface(int surface) {
        this.surface = surface;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public String getFeaturedMediaUrl() {
        return featuredMediaUrl;
    }

    public void setFeaturedMediaUrl(String featuredMediaUrl) {
        this.featuredMediaUrl = featuredMediaUrl;
    }

    public List<RealEstateMedia> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<RealEstateMedia> mediaList) {
        this.mediaList = mediaList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
       this.region = region;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int priceInDollar) {
        this.price = priceInDollar;
    }


    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("listingDate", listingDate);
        map.put("saleDate",saleDate);
        map.put("name", name);
        map.put("jsonPoint", jsonPoint);
        map.put("region", region);
        map.put("location", location);
        map.put("description", description);
        map.put("featuredMediaUrl", featuredMediaUrl);
        map.put("agentName", agentName);
        map.put("price", price);
        map.put("Surface", surface);
        map.put("rooms", rooms);
        map.put("bathrooms", bathrooms);
        map.put("bedrooms", bedrooms);


        return map;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RealEstate estate = (RealEstate) o;
        return Objects.equals(getName(), estate.getName());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public static RealEstate fromQueryDocumentSnapshot(QueryDocumentSnapshot document) {

        String agentName = document.getString("agentName");
        assert agentName != null;

        RealEstate realEstate = new RealEstate(
                document.getString("name"),
                document.getString("region"), document.getString("location"), document.getString("description"),
                document.getString("featuredMediaUrl"),
                Objects.requireNonNull(document.getLong("price")).intValue(),
                Objects.requireNonNull(document.getLong("Surface")).intValue(),
                Objects.requireNonNull(document.getLong("rooms")).intValue(),
                Objects.requireNonNull(document.getLong("bathrooms")).intValue(),
                Objects.requireNonNull(document.getLong("bedrooms")).intValue(),
                null);
        realEstate.setAgentName(agentName);

        realEstate.setSync(true);
        realEstate.setListingDate(document.getDate("listingDate"));

        return realEstate;

    }
    public void clone(RealEstate toClone){
        listingDate = toClone.getListingDate();
        bedrooms = toClone.getBedrooms();
        bathrooms = toClone.getBathrooms();
        description = toClone.getDescription();
        id = toClone.getId();
        saleDate = toClone.getSaleDate();
        agentName = toClone.getAgentName();
        featuredMediaUrl = toClone.getFeaturedMediaUrl();
        jsonPoint = toClone.getJsonPoint();
        location = toClone.getLocation();
        name = toClone.getName();
        listingDate = toClone.getListingDate();
        mediaList = toClone.getMediaList();
        price = toClone.getPrice();
        region = toClone.getRegion();
        surface = toClone.getSurface();
        rooms = toClone.getRooms();


    }

    public static RealEstate fromContentValues(ContentValues values) {
        final RealEstate estate = new RealEstate();

        if(values.containsKey("id")) estate.setId(values.getAsLong("id"));

        if(values.containsKey("name")) estate.setName(values.getAsString("name"));

        if(values.containsKey("jsonPoint")) estate.setJsonPoint(values.getAsString("jsonPoint"));

        if(values.containsKey("region")) estate.setRegion(values.getAsString("region"));

        if(values.containsKey("location")) estate.setLocation(values.getAsString("location"));


        if(values.containsKey("description")) estate.setDescription(values.getAsString("description"));

        if(values.containsKey("featuredMediaUrl")) estate.setFeaturedMediaUrl(values.getAsString("featuredMediaUrl"));

        if(values.containsKey("agentName")) estate.setAgentName(values.getAsString("agentName"));

        if(values.containsKey("price")) estate.setPrice(values.getAsInteger("price"));

        if (values.containsKey("surface")) estate.setSurface(values.getAsInteger("surface"));

        if(values.containsKey("rooms")) estate.setRooms(values.getAsInteger("rooms"));

        if(values.containsKey("bathrooms")) estate.setBathrooms(values.getAsInteger("bathrooms"));

        if(values.containsKey("bedrooms")) estate.setBedrooms(values.getAsInteger("bedrooms"));

        if(values.containsKey("listing_date")) estate.setListingDate(new Date(values.getAsLong("listing_date")));

        if(values.containsKey("is_sold")) {estate.setSold(values.getAsBoolean("is_sold"));
        }


        return estate;
    }


  /*  public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

   */

    // Getter pour isSold
    public boolean isSold() {
        return isSold;
    }

    // Setter pour isSold
    public void setSold(boolean sold) {
        isSold = sold;


    }
}

