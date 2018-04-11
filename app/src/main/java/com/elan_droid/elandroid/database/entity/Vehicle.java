package com.elan_droid.elandroid.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Peter Smith
 */
@Entity(
tableName = Vehicle.TABLE_NAME,
indices = {
    @Index (
        value = { Vehicle.COLUMN_MAKE, Vehicle.COLUMN_MODEL },
        unique = true
    )
}
)
public class Vehicle {

    public final static String TABLE_NAME = "vehicle";
    public final static String REFERENCE_COLUMN_ID = "vehicle_id";
    public final static String COLUMN_ID = "vid";
    public final static String COLUMN_MAKE = "make";
    public final static String COLUMN_MODEL = "model";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    public long mId;

    @ColumnInfo (name =  COLUMN_MAKE, typeAffinity = ColumnInfo.TEXT)
    private String mMake;

    @ColumnInfo (name = COLUMN_MODEL, typeAffinity = ColumnInfo.TEXT)
    private String mModel;

    @Ignore
    public Vehicle(String make, String model) {
        this (0, make, model);
    }

    public Vehicle(long id, String make, String model) {
        this.mId = id;
        this.mMake = make;
        this.mModel = model;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getMake() {
        return mMake;
    }

    public void setMake(String make) {
        this.mMake = make;
    }

    public String getModel() {
        return mModel;
    }

    public void setModel(String model) {
        this.mModel = model;
    }

    public String getMakeModel() {
        return mMake + " " +mModel;
    }

}