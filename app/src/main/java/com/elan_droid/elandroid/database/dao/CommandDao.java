package com.elan_droid.elandroid.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Transaction;

import com.elan_droid.elandroid.database.entity.Message;
import com.elan_droid.elandroid.database.entity.Parameter;
import com.elan_droid.elandroid.database.relation.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith on 4/24/2018.
 **/
@Dao
public abstract class CommandDao extends ParameterDao implements MessageDao {

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
