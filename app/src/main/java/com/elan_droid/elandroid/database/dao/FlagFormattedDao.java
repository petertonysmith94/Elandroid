package com.elan_droid.elandroid.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.elan_droid.elandroid.database.data.entity.FlagFormatted;
import com.elan_droid.elandroid.database.data.entity.Packet;

import java.util.List;

/**
 * Created by Peter Smith on 4/23/2018.
 **/

abstract class FlagFormattedDao {

    @Query ("SELECT *" +
            " FROM " + FlagFormatted.TABLE_NAME +
            " WHERE " + Packet.REFERENCE_COLUMN_ID + " = :packetId")
    public abstract LiveData<List<FlagFormatted>> fetchLatestFormatted (long packetId);

    @Query ("SELECT *" +
            " FROM " + FlagFormatted.TABLE_NAME +
            " WHERE " + Packet.REFERENCE_COLUMN_ID + " = :packetId")
    public abstract List<FlagFormatted> fetchFormatted (long packetId);

    @Insert
    public abstract long insertFormatted (FlagFormatted element);

    @Insert
    public abstract void insertFormatted (FlagFormatted... elements);

    @Delete
    public abstract void deleteFormatted (FlagFormatted element);

    @Delete
    public abstract void deleteFormatted (FlagFormatted... elements);

}
