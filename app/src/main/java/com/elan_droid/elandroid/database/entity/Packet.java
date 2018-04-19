package com.elan_droid.elandroid.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

/**
 * Created by Peter Smith
 */
@Entity (
    tableName = Packet.TABLE_NAME,
        foreignKeys = {
                @ForeignKey (

                )
        }
)
public class Packet {

    public final static String EXTRA = "com.elan_droid.elandroid.database.entity.PACKET";

    public final static String TABLE_NAME = "packet";
    public final static String REFERENCE_COLUMN_ID = "packet_id";
    public final static String COLUMN_ID = "packetId";
    public final static String COLUMN_NAME = "name";

}
