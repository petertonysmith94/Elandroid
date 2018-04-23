package com.elan_droid.elandroid.database.dao;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;

/**
 * Created by Peter Smith
 */

public interface BaseDao<T> {

    @Insert
    long insert (T element);

    @Insert
    void insertAll (T... elements);

    @Delete
    void delete (T element);

    @Delete
    void delete (T... elements);

}
