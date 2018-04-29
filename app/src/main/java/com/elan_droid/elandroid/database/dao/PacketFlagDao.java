package com.elan_droid.elandroid.database.dao;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Transaction;

import com.elan_droid.elandroid.database.entity.Packet;

/**
 * Created by Peter Smith on 4/24/2018.
 **/
@Dao
public abstract class PacketFlagDao {
/**
    @Transaction
    public LiveData<Packet> getLatestPacketWithFlags (long tripId) {
        return Transformations.map(getLatestPacket(tripId), new Function<Packet, Packet>() {
            @Override
            public Packet apply(Packet input) {
                input.setFlags(fetchFlags(input.getId()));
                return input;
            }
        });

    }

    @Transaction
    public Packet insertPacket (Packet packet) {
        long packetId = baseInsert(packet);
        if (packetId != 0) {
            packet.setId(packetId);
        }
        else packet = null;

        return packet;
    }
**/
}