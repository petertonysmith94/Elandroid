package com.elan_droid.elandroid.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.elan_droid.elandroid.database.entity.User;
import com.elan_droid.elandroid.database.entity.Vehicle;
import com.elan_droid.elandroid.database.relation.MinimalProfile;
import com.elan_droid.elandroid.database.relation.Profile;

import java.util.List;

/**
 * Created by Peter Smith
 */
@Dao
public abstract class ProfileDao {

    @Query("SELECT *" +
            " FROM " + User.TABLE_NAME + ", " + Vehicle.TABLE_NAME +
            " WHERE " + Vehicle.COLUMN_ID + " = " + Vehicle.REFERENCE_COLUMN_ID)
    public abstract LiveData<List<Profile>> getProfiles();

    @Query("SELECT *" +
            " FROM " + User.TABLE_NAME + ", " + Vehicle.TABLE_NAME +
            " WHERE " + Vehicle.COLUMN_ID + " = " + Vehicle.REFERENCE_COLUMN_ID)
    public abstract List<Profile> getAllProfiles();


    @Query("SELECT * " +
            " FROM " + User.TABLE_NAME + ", " + Vehicle.TABLE_NAME +
            " WHERE " + Vehicle.COLUMN_ID + " = " + Vehicle.REFERENCE_COLUMN_ID +
            " AND " + User.COLUMN_ID + " = :userId")
    public abstract Profile getProfile (long userId);

    @Query("SELECT * " +
            " FROM " + User.TABLE_NAME + ", " + Vehicle.TABLE_NAME +
            " WHERE " + Vehicle.COLUMN_ID + " = " + Vehicle.REFERENCE_COLUMN_ID +
            " AND " + User.COLUMN_ID + " = :userId")
    public abstract LiveData<Profile> getLiveProfile (long userId);



    @Query("SELECT * " +
            " FROM " + User.TABLE_NAME + ", " + Vehicle.TABLE_NAME +
            " WHERE " + Vehicle.COLUMN_ID + " = " + Vehicle.REFERENCE_COLUMN_ID +
            " AND " + User.COLUMN_ID + " = :userId")
    public abstract Profile getDefailedProfile (long userId);

}
