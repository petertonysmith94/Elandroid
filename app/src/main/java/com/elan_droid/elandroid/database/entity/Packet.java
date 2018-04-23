package com.elan_droid.elandroid.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.elan_droid.elandroid.database.converter.DateTimeConverter;

import java.util.Date;

/**
 * Created by Peter Smith
 */
@Entity (
    tableName = Packet.TABLE_NAME,
    foreignKeys = {
        @ForeignKey (
            entity = Trip.class,
            parentColumns = Trip.COLUMN_ID,
            childColumns = Trip.REFERENCE_COLUMN_ID,
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    }
)
public class Packet {

    public final static String EXTRA = "com.elan_droid.elandroid.database.entity.TRIP";

    public final static String TABLE_NAME = "packet";
    public final static String REFERENCE_COLUMN_ID = "packet_id";
    public final static String COLUMN_ID = "packetId";
    public final static String COLUMN_TIMESTAMP = "timestamp";

    @PrimaryKey (autoGenerate = true)
    @ColumnInfo (name = COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long id;

    @ColumnInfo (name = Trip.REFERENCE_COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long tripId;

    @ColumnInfo (name = COLUMN_TIMESTAMP, typeAffinity = ColumnInfo.INTEGER)
    @TypeConverters (value = DateTimeConverter.class)
    private Date timestamp;

    @Ignore
    public Packet (long tripId) {
        this (0, tripId, new Date(System.currentTimeMillis()));
    }

    public Packet (long id, long tripId, Date timestamp) {
        this.id = id;
        this.tripId = tripId;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTripId() {
        return tripId;
    }

    public void setTripId(long tripId) {
        this.tripId = tripId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
