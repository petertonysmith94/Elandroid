package com.elan_droid.elandroid.database.dao;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.elan_droid.elandroid.database.entity.FormattedParameter;
import com.elan_droid.elandroid.database.entity.Message;
import com.elan_droid.elandroid.database.entity.ParameterBitwise8;

import java.util.List;

/**
 * Created by Peter Smith
 */

interface ParameterFormattedDao {

    @Query(
        "SELECT *" +
        " FROM " + FormattedParameter.TABLE_NAME +
        " WHERE " + Message.REFERENCE_COLUMN_ID + " = :messageId"
    )
    List<FormattedParameter> fetchFormatted (long messageId);

    @Insert
    public abstract long insertFormatted (FormattedParameter element);

    @Insert
    public abstract void insertFormatted (FormattedParameter... elements);

    @Delete
    public abstract void deleteFormatted (FormattedParameter element);

    @Delete
    public abstract void deleteFormatted (FormattedParameter... elements);

}
