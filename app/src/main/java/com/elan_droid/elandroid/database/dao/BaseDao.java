package com.elan_droid.elandroid.database.dao;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;

/**
 * Created by Peter Smith
 */

public abstract class BaseDao<T> {

    @Insert
    public abstract long insert (T element);

    @Insert
    public abstract void insertAll (T... elements);

    @Delete
    public abstract void delete (T element);

    @Delete
    public abstract void delete (T... elements);

}
