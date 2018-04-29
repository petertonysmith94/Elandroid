package com.elan_droid.elandroid.database.dao;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;

/**
 * Created by Peter Smith
 */

public interface BaseDao<T> {

    @Insert
    public abstract long baseInsert(T element);

    @Insert
    public abstract void baseInsertAll (T... elements);

    @Delete
    public abstract void baseDelete(T... elements);

}
