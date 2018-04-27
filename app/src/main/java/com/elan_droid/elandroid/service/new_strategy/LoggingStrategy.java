package com.elan_droid.elandroid.service.new_strategy;

import android.support.annotation.NonNull;
import android.util.Log;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.embedded.Request;
import com.elan_droid.elandroid.database.embedded.Response;
import com.elan_droid.elandroid.database.entity.Packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Peter Smith on 4/27/2018.
 **/
public class LoggingStrategy implements ResponseStrategy {

    private static final String TAG = "LoggingStrategy";
    private static final int DEFAULT_TIMEOUT = 400;

    private final AppDatabase database;
    private final long tripId;
    private final Request request;
    private final Response response;

    private final Packet packet;
    private byte[] buffer;
    private int length;

    private int success;
    private int total;
    private double rate;

    NumberFormat formatter;

    public LoggingStrategy (AppDatabase database, long tripId, @NonNull Request request, @NonNull Response response) {
        this.database = database;
        this.tripId = tripId;
        this.request = request;
        this.response = response;
        this.packet = new Packet(tripId);
        this.length = response.getRawLength();
        this.buffer = new byte[length];
        success = 0;
        total = 0;
        rate = 0.0;
        formatter = new DecimalFormat("#0.00");
    }

    @Override
    public int executeRequest(int requestCode, OutputStream out) throws IOException {
        if (requestCode == RESULT_TRIGGER) {
            out.write(request.getTrigger());
            packet.setTimestamp(System.currentTimeMillis());
            total++;
        }
        return RESULT_TRIGGERED;
    }

    @Override
    public int executeResponse(int requestCode, InputStream in) throws IOException {
        int result = 0;

        // We need to read in the data and validate the response
        if (requestCode == RESULT_TRIGGERED) {
            result = readWithTimeout(in, buffer, length, DEFAULT_TIMEOUT);

            // Failed to read input stream
            if (result < 0) {
                Log.i(TAG, "process(): no bytes read or timeout occurred reading");
            }
            // Failed to validate the buffer
            else if (!response.validate(buffer)) {
                Log.i(TAG, "process(): failed to validate data");
                result = ERROR_VALIDATION;
            }
            // Successfully validated the data
            else {
                Log.i(TAG, "process(): successfully validated data");
                result = RESULT_VALIDATED;
                success++;
            }
        }
        else {
            result = RESULT_IDLE;
        }

        return result;
    }


    @Override
    public int postResponse(int requestCode) {
        rate = success / (double) total;
        Log.i(TAG,
                "result: " + requestCode + ", " +
                        "success      - " + success + ", " +
                        "total        - " + total + ", " +
                        "success rate - " + formatter.format(rate));


        if (requestCode == RESULT_VALIDATED) {
            // Saves packet
            packet.setData(buffer);
            database.packetDao().insert(packet);

            //
            requestCode = RESULT_TRIGGER;
        }
        return requestCode;
    }

    @Override
    public int idleTimeout() {
        return 175;
    }

    private static int readWithTimeout (InputStream in, byte[] buffer,
                                        final int length, final int timeout) throws IOException {
        final long maxTimeout = System.currentTimeMillis() + timeout;
        int bufferOffset = 0;
        int readLength, readResult;

        while (in.available() > 0 && bufferOffset < length) {
            readLength = Math.min (in.available(), length - bufferOffset);
            readResult = in.read (buffer, bufferOffset, readLength);

            if (readResult == -1) {
                return ERROR_READ;
            }

            bufferOffset += readResult;

            // We are gonna rage quit and take a timeout
            if (System.currentTimeMillis() > maxTimeout) {
                return ERROR_TIMEOUT;
            }
        }

        return bufferOffset == length ? bufferOffset : ERROR_READ;
    }

}
