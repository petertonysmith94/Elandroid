package com.elan_droid.elandroid.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.elan_droid.elandroid.database.entity.Flag;
import com.elan_droid.elandroid.database.entity.FlagFormatted;
import com.elan_droid.elandroid.database.entity.Packet;

import java.util.List;

/**
 * Created by Peter Smith on 4/23/2018.
 **/

interface FlagFormattedDao {

    @Query ("SELECT *" +
            " FROM " + FlagFormatted.TABLE_NAME +
            " WHERE " + Packet.REFERENCE_COLUMN_ID + " = :packetId")
    List<FlagFormatted> fetchFormatted (long packetId);

    @Insert
    long insertFormatted (FlagFormatted element);

    @Insert
    void insertFormatted (FlagFormatted... elements);

    @Delete
    void deleteFormatted (FlagFormatted element);

    @Delete
    void deleteFormatted (FlagFormatted... elements);

}
