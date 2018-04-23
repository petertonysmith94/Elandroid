package com.elan_droid.elandroid.database.dao;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.elan_droid.elandroid.database.entity.ParameterFormatted;
import com.elan_droid.elandroid.database.entity.Message;

import java.util.List;

/**
 * Created by Peter Smith
 */

interface ParameterFormattedDao {

    @Query(
        "SELECT *" +
        " FROM " + ParameterFormatted.TABLE_NAME +
        " WHERE " + Message.REFERENCE_COLUMN_ID + " = :messageId"
    )
    List<ParameterFormatted> fetchFormatted (long messageId);

    @Insert
    public abstract long insertFormatted (ParameterFormatted element);

    @Insert
    public abstract void insertFormatted (ParameterFormatted... elements);

    @Delete
    public abstract void deleteFormatted (ParameterFormatted element);

    @Delete
    public abstract void deleteFormatted (ParameterFormatted... elements);

}
