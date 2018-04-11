package com.elan_droid.elandroid.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Peter Smith
 */
@Entity(
    tableName = Trip.TABLE_NAME,
    foreignKeys =
    {
        @ForeignKey(
            entity = User.class,
            parentColumns = User.COLUMN_ID,
            childColumns = User.REFERENCE_COLUMN_ID,
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    }
)
public class Trip {

    public final static String TABLE_NAME = "trip";
    public final static String REFERENCE_COLUMN_ID = "trip_id";
    public final static String COLUMN_ID = "tripId";
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_START_TIME = "start";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long mId;

    // Associates a UserVehicle with a trip
    @ColumnInfo (name = User.REFERENCE_COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long mUserId;

    //@ColumnInfo (name = COLUMN_START_TIME, typeAffinity = ColumnInfo.INTEGER)
    //private long mMessageId;

    @ColumnInfo (name = COLUMN_NAME, typeAffinity = ColumnInfo.TEXT)
    private String mName;

    @Ignore
    public Trip (long userId, @Nullable String name) {
        this (0, userId, name);
    }

    public Trip (long id, long userId, @Nullable String name) {
        this.mId = id;
        this.mUserId = userId;
        this.mName = name;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public long getUserId() {
        return mUserId;
    }

    public void setUserId(long userId) {
        this.mUserId = userId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

}
