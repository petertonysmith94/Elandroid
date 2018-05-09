package com.elan_droid.elandroid.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Transaction;

import com.elan_droid.elandroid.database.data.entity.Flag;
import com.elan_droid.elandroid.database.data.entity.FlagFormatted;
import com.elan_droid.elandroid.database.data.entity.Packet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith on 4/23/2018.
 */
@Dao
public abstract class FlagDao extends FlagFormattedDao {

    @Transaction
    public boolean insert (Packet packet) {
        if (packet != null) {
            insert(packet.getFlagArray());
            return true;
        }
        return false;
    }


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
