package com.openclassrooms.realestatemanager.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class RealEstate implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String jsonPoint;
    private String region;
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
