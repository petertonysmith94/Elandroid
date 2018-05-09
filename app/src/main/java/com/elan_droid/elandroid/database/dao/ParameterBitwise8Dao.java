package com.elan_droid.elandroid.database.dao;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.elan_droid.elandroid.database.data.entity.Message;
import com.elan_droid.elandroid.database.data.entity.ParameterBitwise8;

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
    long insertBitwise8(ParameterBitwise8 element);

    @Insert
    void insertAll(ParameterBitwise8... elements);

    @Delete
    void delete(ParameterBitwise8 element);

    @Delete
    void delete(ParameterBitwise8... elements);

}
