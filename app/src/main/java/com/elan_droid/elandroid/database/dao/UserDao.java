package com.elan_droid.elandroid.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.elan_droid.elandroid.database.entity.User;

/**
 * Created by Peter Smith
 */
@Dao
public abstract class UserDao extends BaseDao<User> {

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update (User user);

    @Query("SELECT " + User.COLUMN_ID +
            " FROM " + User.TABLE_NAME + " LIMIT 1")
    public abstract long fetchOne();


}
