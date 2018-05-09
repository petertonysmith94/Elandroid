package com.elan_droid.elandroid.database.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Peter Smith on 4/23/2018.
 */
@Entity (
    tableName = FlagFormatted.TABLE_NAME,
    foreignKeys = {
        @ForeignKey (
            entity = Packet.class,
            parentColumns = Packet.COLUMN_ID,
            childColumns = Packet.REFERENCE_COLUMN_ID,
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        @ForeignKey (
            entity = ParameterFormatted.class,
            parentColumns = Parameter.COLUMN_ID,
            childColumns = Parameter.REFERENCE_COLUMN_ID,
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    }
)
public class FlagFormatted extends Flag {

    public final static String TABLE_NAME = "flag_formatted";
    public final static String COLUMN_VALUE = "value";
    public final static String COLUMN_STRING_VALUE = "string_value";

    @ColumnInfo(name = COLUMN_VALUE, typeAffinity = ColumnInfo.REAL)
    private double value;

    @ColumnInfo(name = COLUMN_STRING_VALUE, typeAffinity = ColumnInfo.TEXT)
    private String stringValue;

    @Ignore
    public FlagFormatted(long parameterId, long packetId, byte[] raw, double value, String stringValue) {
        this (0, parameterId, packetId, raw, value, stringValue);
    }

    public FlagFormatted(long id, long parameterId, long packetId, byte[] raw, double value, String stringValue) {
        super(id, parameterId, packetId, raw);

        this.value = value;
        this.stringValue = stringValue;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
