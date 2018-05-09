package com.elan_droid.elandroid.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.elan_droid.elandroid.database.data.entity.Trip;
import com.elan_droid.elandroid.database.data.entity.User;

import java.util.List;

/**
 * Created by Peter Smith
 */
@Dao
public abstract class TripDao implements BaseDao<Trip> {

    @Query("SELECT * " +
            " FROM " + Trip.TABLE_NAME +
            " WHERE " + User.REFERENCE_COLUMN_ID + " = :userId")
    public abstract List<Trip> getTrips (long userId);

    @Query("SELECT * " +
            " FROM " + Trip.TABLE_NAME +
            " WHERE " + User.REFERENCE_COLUMN_ID + " = :userId")
    public abstract LiveData<List<Trip>> getProfileTrips (long userId);

    @Update (onConflict = OnConflictStrategy.REPLACE)
    public abstract int update (Trip... trips);

}
