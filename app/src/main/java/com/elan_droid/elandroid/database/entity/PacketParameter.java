package com.elan_droid.elandroid.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;

import static com.elan_droid.elandroid.database.entity.PacketParameter.COLUMN_RAW;

/**
 * Created by BorisJohnson on 4/23/2018.
 */

abstract class PacketParameter {

    public final static String REFERENCE_COLUMN_ID = "packet_parameter_id";
    public final static String COLUMN_ID = "packetParameterId";
    public final static String COLUMN_RAW = "raw";

    @PrimaryKey
    @ColumnInfo(name = COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long id;

    @ColumnInfo(name = Packet.REFERENCE_COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long packetId;

    @ColumnInfo(name = Parameter.REFERENCE_COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long parameterId;

    @ColumnInfo(name = COLUMN_RAW, typeAffinity = ColumnInfo.BLOB)
    private byte[] raw;

    public PacketParameter (long id, long packetId, long parameterId, byte... raw) {
        this.id = id;
        this.packetId = packetId;
        this.parameterId = parameterId;
        this.raw = raw;
    }



}
