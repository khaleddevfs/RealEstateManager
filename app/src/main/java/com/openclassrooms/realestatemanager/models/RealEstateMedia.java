package com.openclassrooms.realestatemanager.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Entity(foreignKeys = @ForeignKey(entity = RealEstate.class, parentColumns = "id", childColumns = "realEstateId"),
        indices = @Index(value = "realEstateId"))
@Data
@NoArgsConstructor
public class RealEstateMedia implements Parcelable {

    @NonNull
    private String mediaUrl;
    @NonNull
    private String mediaCaption;

    private long realEstateId;

    @PrimaryKey(autoGenerate = true)
    private long id;

    @Ignore
    private Boolean isSync = false;
    private String firestoreUrl = "";

    protected RealEstateMedia(Parcel in) {
        mediaUrl = in.readString();
        mediaCaption = in.readString();
        realEstateId = in.readLong();
        id = in.readLong();
        isSync = in.readByte() != 0;
        firestoreUrl = in.readString();
    }

    public static final Creator<RealEstateMedia> CREATOR = new Creator<RealEstateMedia>() {
        @Override
        public RealEstateMedia createFromParcel(Parcel in) {
            return new RealEstateMedia(in);
        }

        @Override
        public RealEstateMedia[] newArray(int size) {
            return new RealEstateMedia[size];
        }
    };

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(mediaUrl);
        parcel.writeString(mediaCaption);
        parcel.writeLong(realEstateId);
        parcel.writeLong(id);
        parcel.writeByte((byte) (isSync != null && isSync ? 1 : 0));
        parcel.writeString(firestoreUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setRealEstateId(long realEstateId) {
    }
}
