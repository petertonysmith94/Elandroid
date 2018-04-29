package com.elan_droid.elandroid.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.elan_droid.elandroid.database.entity.Vehicle;

import java.util.List;

/**
 * Created by Peter Smith
 */
@Dao
public abstract class VehicleDao implements BaseDao<Vehicle> {

    @Query ("SELECT *" +
            " FROM " + Vehicle.TABLE_NAME +
            " WHERE " + Vehicle.COLUMN_MAKE + " LIKE :make" +
            " AND " + Vehicle.COLUMN_MODEL  + " LIKE :model LIMIT 1")
    public abstract Vehicle getVehicle (String make, String model);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update (Vehicle vehicle);

    @Query("SELECT DISTINCT " + Vehicle.COLUMN_MAKE + " FROM " + Vehicle.TABLE_NAME)
    public abstract LiveData<List<String>> getManufacturers ();

    @Query ("SELECT DISTINCT " + Vehicle.COLUMN_MODEL + " FROM " + Vehicle.TABLE_NAME +
            " WHERE " + Vehicle.COLUMN_MAKE + " LIKE :manufacturer")
    public abstract List<String> getModels (String manufacturer);

    @Query ("SELECT " + Vehicle.COLUMN_ID +
            " FROM " + Vehicle.TABLE_NAME +
            " WHERE " + Vehicle.COLUMN_MAKE + " LIKE :make" +
            " AND " + Vehicle.COLUMN_MODEL  + " LIKE :model LIMIT 1")
    public abstract long getId (String make, String model);


}
