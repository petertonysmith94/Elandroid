package com.elan_droid.elandroid.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.elan_droid.elandroid.database.entity.Packet;
import com.elan_droid.elandroid.database.entity.Trip;

import java.util.List;

/**
 * Created by Peter Smith on 4/22/2018.
 */
@Dao
public interface PacketDao extends BaseDao<Packet> {

    @Query("SELECT *" +
            " FROM " + Packet.TABLE_NAME +
            " WHERE " + Trip.REFERENCE_COLUMN_ID + " = :tripId")
    List<Packet> getPackets (long tripId);

    @Query("SELECT *" +
            " FROM " + Packet.TABLE_NAME +
            " WHERE " + Trip.REFERENCE_COLUMN_ID + " = :tripId" +
            " ORDER BY " + Packet.COLUMN_TIMESTAMP + " ASC")
    LiveData<Packet> getLatestPacket (long tripId);

    @Query("SELECT COUNT (" + Packet.COLUMN_ID + ")" +
            " FROM " + Packet.TABLE_NAME +
            " WHERE " + Trip.REFERENCE_COLUMN_ID + " = :tripId")
    LiveData<Integer> getCount (long tripId);


}
