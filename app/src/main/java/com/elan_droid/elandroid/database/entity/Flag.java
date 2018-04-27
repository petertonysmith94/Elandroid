package com.elan_droid.elandroid.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Peter Smith on 4/23/2018.
 */

public abstract class Flag {

    public final static String REFERENCE_COLUMN_ID = "flag_id";
    public final static String COLUMN_ID = "id";
    public final static String COLUMN_RAW = "raw";

    @PrimaryKey (autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long id;

    @ColumnInfo(name = Packet.REFERENCE_COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long packetId;

    @ColumnInfo(name = Parameter.REFERENCE_COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long parameterId;

    @ColumnInfo(name = COLUMN_RAW, typeAffinity = ColumnInfo.BLOB)
    private byte[] raw;

    public Flag(long id, long parameterId, long packetId, byte[] raw) {
        this.id = id;
        this.parameterId = parameterId;
        this.packetId = packetId;
        this.raw = raw;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPacketId() {
        return packetId;
    }

    public void setPacketId(long packetId) {
        this.packetId = packetId;
    }

    public long getParameterId() {
        return parameterId;
    }

    public void setParameterId(long parameterId) {
        this.parameterId = parameterId;
    }

    public byte[] getRaw() {
        return raw;
    }

    public void setRaw(byte[] raw) {
        this.raw = raw;
    }
}
