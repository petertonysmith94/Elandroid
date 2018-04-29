package com.elan_droid.elandroid.database.dao;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.support.annotation.Nullable;

import com.elan_droid.elandroid.database.entity.Packet;
import com.elan_droid.elandroid.database.entity.Trip;

import java.util.List;

/**
 * Created by Peter Smith on 4/22/2018.
 */
@Dao
public abstract class PacketDao implements BaseDao<Packet> {

    @Transaction
    public Packet insertPacket (Packet packet) {
        packet.setId(0);
        long packetId = this.baseInsert(packet);

        if (packetId == 0) {
            return null;
        }
        else {
            packet.setId(packetId);
            return packet;
        }
    }

    @Query("SELECT *" +
            " FROM " + Packet.TABLE_NAME +
            " WHERE " + Trip.REFERENCE_COLUMN_ID + " = :tripId")
    public abstract List<Packet> getPackets (long tripId);

    @Query("SELECT *" +
            " FROM " + Packet.TABLE_NAME +
            " WHERE " + Trip.REFERENCE_COLUMN_ID + " = :tripId" +
            " ORDER BY " + Packet.COLUMN_TIMESTAMP + " DESC" +
            " LIMIT 1")
    public abstract LiveData<Packet> getLatest (long tripId);

    @Transaction
    public LiveData<Packet> getLatestPacket (LiveData<Trip> source) {
        /**MediatorLiveData<Packet> latest = new MediatorLiveData<>();

        latest.addSource(source, new Observer<Trip>() {
            @Override
            public void onChanged(@Nullable Trip trip) {
                latest.removeSource(getLatest());
                getLatest (trip.getId());
            }
        });

        latest.
         **/
        return null;
    }


    @Query("SELECT COUNT (" + Packet.COLUMN_ID + ")" +
            " FROM " + Packet.TABLE_NAME +
            " WHERE " + Trip.REFERENCE_COLUMN_ID + " = :tripId")
    public abstract LiveData<Integer> getCount (long tripId);


}
