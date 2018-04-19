package com.elan_droid.elandroid.database.dao;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.elan_droid.elandroid.database.entity.Message;
import com.elan_droid.elandroid.database.entity.ParameterBitwise8;

import java.util.List;

/**
 * Created by Peter Smith
 */

interface ParameterBitwise8Dao {

    @Query (
        "SELECT *" +
        " FROM " + ParameterBitwise8.TABLE_NAME +
        " WHERE " + Message.REFERENCE_COLUMN_ID + " = :messageId"
    )
    List<ParameterBitwise8> fetchBitwise (long messageId);

    @Insert
    public abstract long insertBitwise8 (ParameterBitwise8 element);

    @Insert
    public abstract void insertAll (ParameterBitwise8... elements);

    @Delete
    public abstract void delete (ParameterBitwise8 element);

    @Delete
    public abstract void delete (ParameterBitwise8... elements);

}
