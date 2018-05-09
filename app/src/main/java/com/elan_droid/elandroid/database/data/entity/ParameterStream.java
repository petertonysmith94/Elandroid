package com.elan_droid.elandroid.database.data.entity;

import android.arch.persistence.room.ColumnInfo;

import java.util.Arrays;

/**
 * Created by Peter Smith
 *
 * A stream parameter represents a parameter which originates from a data stream.
 * Therefore it can be formatted into a value from a byte array.
 *
 * TODO : change position and length to
 */
public abstract class ParameterStream extends Parameter {

    public final static String COLUMN_STREAM_POSITION = "position";
    public final static String COLUMN_STREAM_LENGTH = "length";

    @ColumnInfo (name = COLUMN_STREAM_POSITION, typeAffinity = ColumnInfo.INTEGER)
    private int position;

    @ColumnInfo (name = COLUMN_STREAM_LENGTH, typeAffinity = ColumnInfo.INTEGER)
    private int length;

    /**
     *
     * @param id
     * @param messageId
     * @param identifier
     * @param name
     * @param type
     * @param position
     * @param length
     */
    public ParameterStream(long id, long messageId, String identifier, String name, Type type,
                           int position, int length) {
        super (id, messageId, identifier, name, type);

        this.position = position;
        this.length = length;
    }

    protected byte[] strip (byte[] data) {
        return Arrays.copyOfRange(data, position, position + length);
    }

    public abstract void format (Packet packet, byte[] data);

    @Override
    public void format (Packet packet, Object data) {
        format(packet, (byte[]) data);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
