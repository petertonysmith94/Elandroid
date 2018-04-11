package com.elan_droid.elandroid.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static android.arch.persistence.room.ForeignKey.SET_NULL;

/**
 * Created by Peter Smith
 */
@Entity(
tableName = User.TABLE_NAME,
indices = {
    @Index (
        value = { User.COLUMN_ID },
        unique = true
    ),
    @Index (
        value = { Vehicle.REFERENCE_COLUMN_ID }
    )
},
foreignKeys =
    @ForeignKey (
        entity = Vehicle.class,
        parentColumns = Vehicle.COLUMN_ID,
        childColumns = Vehicle.REFERENCE_COLUMN_ID,
        onDelete = SET_NULL,
        onUpdate = CASCADE
    )
)
public class User {

    public final static String TABLE_NAME = "user";
    public final static String REFERENCE_COLUMN_ID = "user_id";
    public final static String COLUMN_ID = "uid";
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_REG = "registration";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    public long mId;

    @ColumnInfo (name = Vehicle.REFERENCE_COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long mVehicleId;

    @ColumnInfo (name = COLUMN_NAME, typeAffinity = ColumnInfo.TEXT)
    private String mName;

    @ColumnInfo (name = COLUMN_REG, typeAffinity = ColumnInfo.TEXT)
    private String mRegistration;

    @Ignore
    public User(Vehicle vehicle, @NonNull String name, @Nullable String registration) {
        this (vehicle.getId(), name, registration);
    }

    @Ignore
    public User(long vehicleId, @NonNull String name, @Nullable String registration) {
        this (0, vehicleId, name, registration);
    }

    public User(long id, long vehicleId, @NonNull String name, @Nullable String registration) {
        this.mId = id;
        this.mVehicleId = vehicleId;
        this.mName = name;
        this.mRegistration = registration;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public long getVehicleId() {
        return mVehicleId;
    }

    public void setVehicleId(long id) {
        this.mVehicleId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getRegistration() {
        return mRegistration;
    }

    public void setRegistration(String registration) {
        this.mRegistration = registration;
    }

}
