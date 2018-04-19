package com.elan_droid.elandroid.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Transaction;

import com.elan_droid.elandroid.database.entity.FormattedParameter;
import com.elan_droid.elandroid.database.entity.Parameter;
import com.elan_droid.elandroid.database.entity.ParameterBitwise8;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith
 */
@Dao
public abstract class ParameterDao implements ParameterFormattedDao, ParameterBitwise8Dao {

    @Transaction
    public List<Parameter> fetch (long messageId) {
        List<Parameter> parameters = new ArrayList<>();
        //parameters.addAll(fetchBitwise(messageId));
        parameters.addAll(fetchFormatted(messageId));
        return parameters;
    }

    @Transaction
    public void insert (Parameter... parameters) {
        for (Parameter p : parameters) {
            switch (p.getType()) {
                case FORMATTED:
                    insertFormatted( (FormattedParameter) p);
                    break;

                case BITWISE_8:
                    insertBitwise8( (ParameterBitwise8) p);
                    break;
            }
        }
    }

}
