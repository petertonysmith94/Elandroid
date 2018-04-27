package com.elan_droid.elandroid.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Transaction;
import android.support.annotation.Nullable;

import com.elan_droid.elandroid.database.entity.Flag;
import com.elan_droid.elandroid.database.entity.FlagFormatted;
import com.elan_droid.elandroid.database.entity.Parameter;
import com.elan_droid.elandroid.database.entity.ParameterBitwise8;
import com.elan_droid.elandroid.database.entity.ParameterFormatted;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith on 4/23/2018.
 */
@Dao
public abstract class FlagDao implements FlagFormattedDao {

    @Transaction
    public List<Flag> fetchFlags (long packetId) {
        List<Flag> flags = new ArrayList<>();
        //parameters.addAll(fetchBitwise(messageId));
        flags.addAll(fetchFormatted(packetId));
        return flags;
    }

    @Transaction
    public void insert (Flag... flags) {
        for (Flag flag : flags) {
            insert(flag);
        }
    }

    @Transaction
    public void insert (Flag flag) {
        if (flag instanceof FlagFormatted) {
            insertFormatted((FlagFormatted) flag);
        }
    }

}
