package com.elan_droid.elandroid.database.embedded;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;

import com.elan_droid.elandroid.database.entity.Flag;
import com.elan_droid.elandroid.database.entity.Packet;
import com.elan_droid.elandroid.database.entity.ParameterStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Ignore
    private List<ParameterStream> streamParameters;

    public Response (int rawLength, int payloadOffset, int payloadLength) {
        this.rawLength = rawLength;
        this.payloadOffset = payloadOffset;
        this.payloadLength = payloadLength;
        this.streamParameters = new ArrayList<>();
    }

    public boolean validate (byte[] data) {
        return true;
    }

    public Packet format(Packet packet) {
        final byte[] raw = packet.getData();
        byte[] tmp;

        for (ParameterStream sp : streamParameters) {
            tmp = Arrays.copyOfRange(raw, sp.getPosition(), sp.getPosition() + sp.getLength());
            sp.format(packet, tmp);
        }

        return packet;
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

    public List<ParameterStream> getStreamParameters() {
        return streamParameters;
    }

    public void setStreamParameters(List<ParameterStream> streamParameters) {
        this.streamParameters = streamParameters;
    }
}
