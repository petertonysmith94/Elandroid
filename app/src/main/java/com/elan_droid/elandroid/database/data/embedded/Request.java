package com.elan_droid.elandroid.database.data.embedded;

import android.arch.persistence.room.ColumnInfo;

/**
 * Created by Peter Smith
 */

public class Request {

    public final static String COLUMN_REQUEST_TRIGGER = "trigger";

    @ColumnInfo (name = COLUMN_REQUEST_TRIGGER, typeAffinity = ColumnInfo.BLOB)
    private byte[] trigger;

    public Request (byte[] trigger) {
        this.trigger = trigger;
    }

    public byte[] getTrigger() {
        return trigger;
    }

    public void setTrigger (byte[] trigger) {
        this.trigger = trigger;
    }

}
