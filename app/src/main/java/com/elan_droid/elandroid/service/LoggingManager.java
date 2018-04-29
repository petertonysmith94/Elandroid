package com.elan_droid.elandroid.service;

import android.os.Messenger;
import android.support.annotation.NonNull;
import android.util.Log;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.embedded.Response;
import com.elan_droid.elandroid.database.entity.Flag;
import com.elan_droid.elandroid.database.entity.Packet;
import com.elan_droid.elandroid.database.entity.ParameterStream;
import com.elan_droid.elandroid.database.entity.Trip;
import com.elan_droid.elandroid.database.relation.Command;
import com.elan_droid.elandroid.database.view_model.TripModel;

import java.util.Arrays;

/**
 * Created by Peter Smith on 4/22/2018.
 */

public class LoggingManager extends BaseManager {

    public final static String TAG = "LoggingManager";


    private AppDatabase database;

    // Process threads
    //private DebugThread debugThread;
    private int state;

    //
    private Trip currTrip;
    private Command currCommand;


    public LoggingManager (Messenger messenger, AppDatabase database) {
        super (messenger);

        this.database = database;
        this.state = BaseService.LOGGING_STATE_STOPPED;
    }

    /**
     * Synchronously sets the state of the logging manager.
     *
     * @param state new state
     */
    private synchronized void setState(int state) {
        if (this.state != state) {
            Log.d(TAG, "setState()  " + this.state + " => " + state);
            this.state = state;

            android.os.Message msg = new android.os.Message();
            msg.what = BaseService.MESSAGE_LOGGING_STATE_CHANGE;
            msg.arg1 = state;
            msg.obj = currTrip;
            send(msg);
        }
    }

    public synchronized int getState() {
        return this.state;
    }


    protected synchronized void start (Trip trip, Command  command) {
        if (state == BaseService.LOGGING_STATE_STOPPED) {
            // Set the current trip + command, starts the process threads
            setTrip(trip);
            setCommand(command);
            setState(BaseService.LOGGING_STATE_STARTED);
            startThreads();
        }
        else sendToast ("There is already a trip running!");
    }

    /**
     *
     */
    private synchronized void startThreads () {
        stopThreads();

        //debugThread = new DebugThread(database, currTrip, currCommand);
        //debugThread.start();
    }

    private synchronized void stop () {
        stopThreads();
        setState(BaseService.LOGGING_STATE_STOPPED);
    }

    /**
     *
     * @param trip
     */
    private void setTrip (@NonNull Trip trip) {
        this.currTrip = trip;
    }



    private void setCommand (@NonNull Command command) {
        this.currCommand = command;
    }




    private synchronized void stopThreads () {

    }

    /**
     * Stops and deletes the current trip
     */
    synchronized void stopAndDelete () {
        stop();
        new TripModel.DeleteTripAsyncTask(database, null).execute(currTrip);
    }

    /**
     * Stops and saves the current trip with the given name
     * @param tripName  the new trip name
     */
    synchronized void stopAndSave (String tripName) {
        stop();
        currTrip.setName(tripName);
        new TripModel.UpdateAsyncTask(database, null).execute(currTrip);
    }

    /*
    private static class DebugThread extends Thread {

        private final static int TIMEOUT_MS = 1000;

        private AppDatabase database;
        private final Trip trip;
        private final Response response;
        private boolean running;

        private byte[] debugResponse;

        public DebugThread (AppDatabase database, Trip trip, Command command) {
            this.database = database;
            this.trip = trip;
            this.response = command.getMessage().getResponse();
            this.debugResponse = createTestResponse(command.getMessage().getResponse(), (byte) 00, (byte) 15);
            running = true;
        }

        private byte[] createTestResponse(Response response, final byte fill, final byte value) {
            byte[] buffer = new byte[response.getPayloadLength()];

            for (ParameterStream p : response.getStreamParameters()) {
                final int end = p.getPosition() + p.getLength();

                Arrays.fill(buffer, p.getPosition(), end - 1, fill);
                Arrays.fill(buffer, end - 1, end, value);
            }

            return buffer;
        }

        private void fill (byte[] buffer, byte value, byte fill) {
            final int lastIndex = buffer.length - 1;

            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = (i == lastIndex) ? value : fill;
            }
        }

        @Override
        public void run() {
            Flag[] flags;

            while (running) {
                final long packetId = database.packetDao().baseInsert(new Packet(trip.getId()));

                flags = response.format(packetId, debugResponse);
                database.flagDao().baseInsert (flags);

                try {
                    sleep(TIMEOUT_MS);
                }
                catch (InterruptedException e) {

                }
            }
        }





        public void cancel() {
            running = false;
        }
    }
    */

}
