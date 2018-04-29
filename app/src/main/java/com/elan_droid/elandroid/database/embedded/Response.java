package com.elan_droid.elandroid.database.embedded;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;

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
    public final static String COLUMN_VALIDATE_FROM = "validate_from";
    public final static String COLUMN_VALIDATE_TO = "validate_to";
    public final static String COLUMN_PAYLOAD_FROM = "payload_from";
    public final static String COLUMN_PAYLOAD_TO = "payload_to";

    @ColumnInfo(name = COLUMN_RESPONSE_LENGTH, typeAffinity = ColumnInfo.INTEGER)
    private int rawLength;

    @ColumnInfo(name = COLUMN_VALIDATE_FROM, typeAffinity = ColumnInfo.INTEGER)
    private int validateFrom;

    @ColumnInfo(name = COLUMN_VALIDATE_TO, typeAffinity = ColumnInfo.INTEGER)
    private int validateTo;

    @ColumnInfo(name = COLUMN_PAYLOAD_FROM, typeAffinity = ColumnInfo.INTEGER)
    private int payloadFrom;

    @ColumnInfo(name = COLUMN_PAYLOAD_TO, typeAffinity = ColumnInfo.INTEGER)
    private int payloadTo;

    @Ignore
    private final int payloadLength;

    @Ignore
    private List<ParameterStream> streamParameters;

    public Response (int rawLength, int validateFrom, int validateTo, int payloadFrom, int payloadTo) {
        this.rawLength = rawLength;
        this.validateFrom = validateFrom;
        this.validateTo = validateTo;
        this.payloadFrom = payloadFrom;
        this.payloadTo = payloadTo;
        this.payloadLength = payloadTo - payloadFrom;
        this.streamParameters = new ArrayList<>();
    }

    public boolean validate (byte[] data) {
        return data.length == rawLength && checksum(data);
    }

    private boolean checksum (byte[] data) {
        //data = Arrays.copyOfRange(data, validateFrom, validateTo);
        byte checksum = 0x00;

        for (byte d : data) {
            checksum += d;
        }
        return checksum == 0;
    }

    /**
     * Strips the
     * @param rawBytes
     * @return
     */
    public byte[] stripPayload (byte[] rawBytes) {
        byte[] result = rawBytes;
        if (result.length == rawLength) {
            result = Arrays.copyOfRange (rawBytes, payloadFrom, payloadTo);
        }
        return result.length == payloadLength ? result : null;
    }


    public Packet format(Packet packet) {
        packet.clearFlags();

        for (ParameterStream sp : streamParameters) {
            sp.format(packet,
                Arrays.copyOfRange(
                    packet.getData(), sp.getPosition(), sp.getPosition() + sp.getLength()
                )
            );
        }
        return packet;
    }

    public int getRawLength() {
        return rawLength;
    }

    public void setRawLength(int rawLength) {
        this.rawLength = rawLength;
    }

    public int getValidateFrom() {
        return validateFrom;
    }

    public void setValidateFrom(int validateFrom) {
        this.validateFrom = validateFrom;
    }

    public int getValidateTo() {
        return validateTo;
    }

    public void setValidateTo(int validateTo) {
        this.validateTo = validateTo;
    }

    public int getPayloadFrom() {
        return payloadFrom;
    }

    public void setPayloadFrom(int payloadFrom) {
        this.payloadFrom = payloadFrom;
    }

    public int getPayloadTo() {
        return payloadTo;
    }

    public void setPayloadTo(int payloadTo) {
        this.payloadTo = payloadTo;
    }

    public List<ParameterStream> getStreamParameters() {
        return streamParameters;
    }

    public void setStreamParameters(List<ParameterStream> streamParameters) {
        this.streamParameters = streamParameters;
    }
}
