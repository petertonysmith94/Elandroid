package com.elan_droid.elandroid.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.elan_droid.elandroid.database.entity.Page;
import com.elan_droid.elandroid.database.entity.User;

import java.util.List;

/**
 * Created by Peter Smith
 */
@Dao
public abstract class PageDao extends BaseDao<Page> {

    @Query("SELECT *" +
            " FROM " + Page.TABLE_NAME +
            " WHERE " + User.REFERENCE_COLUMN_ID + " = :userId")
    public abstract List<Page> getPages(long userId);

}
