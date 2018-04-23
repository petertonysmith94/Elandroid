package com.elan_droid.elandroid.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.elan_droid.elandroid.database.entity.Packet;
import com.elan_droid.elandroid.database.entity.Page;
import com.elan_droid.elandroid.database.entity.Trip;
import com.elan_droid.elandroid.database.entity.User;

import java.util.List;

/**
 * Created by BorisJohnson on 4/22/2018.
 */
@Dao
public abstract class PacketDao implements BaseDao<Packet> {

    @Query("SELECT *" +
            " FROM " + Packet.TABLE_NAME +
            " WHERE " + Trip.REFERENCE_COLUMN_ID + " = :tripId")
    public abstract List<Packet> getPackets (long tripId);

    @Query("SELECT *" +
            " FROM " + Packet.TABLE_NAME +
            " WHERE " + Trip.REFERENCE_COLUMN_ID + " = :tripId" +
            " ORDER BY " + Packet.COLUMN_TIMESTAMP + " ASC")
    public abstract LiveData<Packet> getLatest (long tripId);

    @Query("SELECT COUNT (" + Packet.COLUMN_ID + ")" +
            " FROM " + Packet.TABLE_NAME +
            " WHERE " + Trip.REFERENCE_COLUMN_ID + " = :tripId")
    public abstract LiveData<Integer> getCount (long tripId);


}
