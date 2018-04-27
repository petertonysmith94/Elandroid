package com.elan_droid.elandroid.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.relation.Profile;

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
    private long userId;

    @ColumnInfo (name = Message.REFERENCE_COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long messageId;

    @ColumnInfo (name = COLUMN_NAME, typeAffinity = ColumnInfo.TEXT)
    private String mName;

    @Ignore
    public Trip (Profile profile) {
        this (AppDatabase.NEW_ENTITY_ID, profile.getProfileId(), profile.getDefaultMessageId(), "Active: " + profile.getName());
    }

    public Trip (long id, long userId, long messageId, @Nullable String name) {
        this.mId = id;
        this.userId = userId;
        this.messageId = messageId;
        this.mName = name;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

}
