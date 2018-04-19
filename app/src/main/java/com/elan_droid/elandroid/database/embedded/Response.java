package com.elan_droid.elandroid.database.embedded;

import android.arch.persistence.room.ColumnInfo;

/**
 * Created by Peter Smith
 */
public class Response {

    public final static String COLUMN_RESPONSE_LENGTH = "raw_length";
    public final static String COLUMN_PAYLOAD_OFFSET = "payload_offset";
    public final static String COLUMN_PAYLOAD_LENGTH = "payload_length";

    @ColumnInfo(name = COLUMN_RESPONSE_LENGTH, typeAffinity = ColumnInfo.INTEGER)
    private int rawLength;

    @ColumnInfo(name = COLUMN_PAYLOAD_OFFSET, typeAffinity = ColumnInfo.INTEGER)
    private int payloadOffset;

    @ColumnInfo(name = COLUMN_PAYLOAD_LENGTH, typeAffinity = ColumnInfo.INTEGER)
    private int payloadLength;

    public Response (int rawLength, int payloadOffset, int payloadLength) {
        this.rawLength = rawLength;
        this.payloadOffset = payloadOffset;
        this.payloadLength = payloadLength;
    }

    public int getRawLength() {
        return rawLength;
    }

    public void setRawLength(int rawLength) {
        this.rawLength = rawLength;
    }

    public int getPayloadOffset() {
        return payloadOffset;
    }

    public void setPayloadOffset(int payloadOffset) {
        this.payloadOffset = payloadOffset;
    }

    public int getPayloadLength() {
        return payloadLength;
    }

    public void setPayloadLength(int payloadLength) {
        this.payloadLength = payloadLength;
    }

}
