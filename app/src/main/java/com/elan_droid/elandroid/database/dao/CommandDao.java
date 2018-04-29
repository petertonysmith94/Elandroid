package com.elan_droid.elandroid.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.elan_droid.elandroid.database.entity.Message;
import com.elan_droid.elandroid.database.entity.Parameter;
import com.elan_droid.elandroid.database.relation.Command;

import java.util.List;

/**
 * Created by Peter Smith on 4/27/2018.
 **/
@Dao
public abstract class CommandDao extends ParameterDao implements BaseDao<Message> {

    @Insert
    protected abstract long insertMessage (Message message);

    @Transaction
    public void insertCommands (Command... command) {
        for (Command c : command) {
            long messageId = insertMessage(c.getMessage());
            insert(c.getParametersArray());
        }
    }

    @Query("SELECT *" +
            " FROM " + Message.TABLE_NAME +
            " WHERE " + Message.COLUMN_ID + " = :messageId")
    public abstract Message fetchMessage (long messageId);

    @Transaction
    public Command getCommand (final long messageId) {
        Message msg = fetchMessage(messageId);
        Command result = null;

        if (msg != null) {
            List<Parameter> parameters = fetchParameters(messageId);

            if (parameters != null) {
                result = new Command(fetchMessage(messageId), parameters);
            }
        }
        return result;
    }
}
