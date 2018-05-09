package com.elan_droid.elandroid.database.dao;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;

/**
 * Created by Peter Smith
 *
 * Generic DAO for the insertion and deletion of database Entities
 */
public interface BaseDao<T> {

    @Insert
    long baseInsert(T element);

    @Delete
    void baseDelete(T element);

}
