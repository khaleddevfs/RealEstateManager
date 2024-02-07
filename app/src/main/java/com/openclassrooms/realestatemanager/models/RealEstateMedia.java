package com.openclassrooms.realestatemanager.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Objects;

@Entity(foreignKeys = @ForeignKey(entity = RealEstate.class, parentColumns = "id", childColumns = "realEstateId"),
        indices = @Index(value = "realEstateId"))
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
        byte tmpIsSync = in.readByte();
        isSync = tmpIsSync == 0 ? null : tmpIsSync == 1;;
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



    public Boolean getSync() {
        return isSync;
    }

    public String getFirestoreUrl() {
        return firestoreUrl;
    }


    public void setSync(Boolean sync) {
        isSync = sync;
    }

    public RealEstateMedia(long id, long realEstateId, @NonNull String mediaUrl, @NonNull String mediaCaption,String firestoreUrl) {
        this.id = id;
        this.realEstateId = realEstateId;
        this.mediaUrl = mediaUrl;
        this.mediaCaption = mediaCaption;
        this.firestoreUrl = firestoreUrl;
    }

    @Ignore
    public RealEstateMedia(@NonNull String mediaUrl, @NonNull String mediaCaption) {
        this.mediaUrl = mediaUrl;
        this.mediaCaption = mediaCaption;

    }
    @Ignore
    public RealEstateMedia(long realEstateId, @NonNull String mediaUrl, @NonNull String mediaCaption) {
        this.realEstateId = realEstateId;
        this.mediaUrl = mediaUrl;
        this.mediaCaption = mediaCaption;

    }

    public long getRealEstateId() {
        return realEstateId;
    }

    public void setRealEstateId(long realEstateId) {
        realEstateId = realEstateId;
    }

    public long getID() {
        return id;
    }

    @NonNull
    public String getMediaUrl() {
        return mediaUrl;
    }

    @NonNull
    public String getMediaCaption() {
        return mediaCaption;
    }

    public void setMediaCaption(@NonNull String mediaCaption) {
        mediaCaption = mediaCaption;
    }

    public HashMap<String, Object> toHashMap(String agentName) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("mediaUrl", firestoreUrl);
        map.put("mediaCaption",mediaCaption);
        map.put("realEstateId",agentName + realEstateId);

        return map;

    }

    public static RealEstateMedia fromQueryDocumentSnapshot(QueryDocumentSnapshot document, String agent) {

        String idS = Objects.requireNonNull(document.getString("realEstateId")).substring(agent.length());

        Log.d("TAG", "fromQueryDocumentSnapshot: " + idS);

        int id = Integer.parseInt(idS);


        RealEstateMedia media = new  RealEstateMedia(id,
                Objects.requireNonNull(document.getString("mediaUrl")),
                Objects.requireNonNull(document.getString("mediaCaption")));

        media.setSync(false);

        return media;
    }


    public void setMediaURL(String url) {
        mediaUrl = url;
    }


    public void setFirestoreUrl(String url) {
        firestoreUrl = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(mediaUrl);
        parcel.writeString(mediaCaption);
        parcel.writeLong(realEstateId);
        parcel.writeLong(id);
        parcel.writeByte((byte) (isSync != null && isSync ? 1 : 0));
        parcel.writeString(firestoreUrl);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



}
