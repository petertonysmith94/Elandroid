package com.elan_droid.elandroid.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.elan_droid.elandroid.database.entity.Message;
import com.elan_droid.elandroid.database.entity.Page;
import com.elan_droid.elandroid.database.entity.User;
import com.elan_droid.elandroid.database.relation.Command;

/**
 * Created by Peter Smith
 */
@Dao
public interface MessageDao extends BaseDao<Message> {

    @Query("SELECT *" +
            " FROM " + Message.TABLE_NAME +
            " WHERE " + Message.COLUMN_ID + " = :messageId")
    public abstract Message fetchMessage (long messageId);

}
