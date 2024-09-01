package com.example.lolab_3;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "table_name")
public class Element implements Serializable{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @NonNull
    @ColumnInfo(name = "manufacturer")
    private String mManufacturer;

    @NonNull
    @ColumnInfo(name = "model")
    private String mModel;

    @NonNull
    @ColumnInfo(name = "android_version")
    private String mAndroidVersion;

    @NonNull
    @ColumnInfo(name = "website")
    private String mWebsite;


    public Element(@NonNull String manufacturer, @NonNull String model, @NonNull String androidVersion, @NonNull String website) {
        this.mManufacturer = manufacturer;
        this.mModel = model;
        this.mAndroidVersion = androidVersion;
        this.mWebsite = website;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    @NonNull
    public String getManufacturer() {
        return mManufacturer;
    }

    public void setManufacturer(@NonNull String manufacturer) {
        this.mManufacturer = manufacturer;
    }

    @NonNull
    public String getModel() {
        return mModel;
    }

    public void setModel(@NonNull String model) {
        this.mModel = model;
    }

    @NonNull
    public String getAndroidVersion() {
        return mAndroidVersion;
    }

    public void setAndroidVersion(@NonNull String androidVersion) {
        this.mAndroidVersion = androidVersion;
    }

    @NonNull
    public String getWebsite() {
        return mWebsite;
    }

    public void setWebsite(@NonNull String website) {
        this.mWebsite = website;
    }

}