package com.elan_droid.elandroid.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.elan_droid.elandroid.database.data.entity.Page;
import com.elan_droid.elandroid.database.data.entity.PageItem;

import java.util.List;

/**
 * Created by Peter Smith
 */
@Dao
public abstract class PageItemDao implements BaseDao<PageItem> {

    @Update (onConflict = OnConflictStrategy.REPLACE)
    public abstract void update (PageItem item);

    @Query("SELECT *" +
            " FROM " + PageItem.TABLE_NAME +
            " WHERE " + Page.REFERENCE_COLUMN_ID + " = :pageId")
    public abstract List<PageItem> getPageItems (long pageId);

}
