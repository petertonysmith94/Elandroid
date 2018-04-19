package com.elan_droid.elandroid.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.elan_droid.elandroid.database.entity.Trip;
import com.elan_droid.elandroid.database.entity.User;
import com.elan_droid.elandroid.database.entity.Vehicle;
import com.elan_droid.elandroid.database.relation.Profile;

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

}
