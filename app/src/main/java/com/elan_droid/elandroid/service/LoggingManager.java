package com.elan_droid.elandroid.service;

import android.bluetooth.BluetoothManager;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.entity.Packet;
import com.elan_droid.elandroid.database.entity.Trip;
import com.elan_droid.elandroid.database.view.TripModel;

/**
 * Created by BorisJohnson on 4/22/2018.
 */

public class LoggingManager {

    public final static String TAG = "LoggingManager";

    private Messenger messenger;
    private AppDatabase database;


    private DebugThread debugThread;

    private Trip currentTrip;
    private int state;

    public LoggingManager (Messenger messenger, AppDatabase database) {
        this.messenger = messenger;
        this.database = database;
        this.state = BaseService.LOGGING_STATE_STOPPED;
    }

    /**
     * Sends a message via the messenger which was passed in
     *
     * @param message the message to be sent
     */
    private void send(android.os.Message message) {
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            Log.e(TAG, "Unabled to sendMessage message", e);
        }
    }

    /**
     * Synchronously sets the state of the logging manager
     *
     * @param state new state
     */
    private synchronized void setState(int state) {
        Log.d(TAG, "setState()  " + this.state + " => " + state);
        this.state = state;

        android.os.Message msg = new android.os.Message();
        msg.what = BaseService.MESSAGE_LOGGING_STATE_CHANGE;
        msg.arg1 = state;
        msg.obj = currentTrip;
        send(msg);
    }

    public synchronized void start (Trip trip) {
        if (state == BaseService.LOGGING_STATE_STOPPED) {
            currentTrip = trip;
            setState (BaseService.LOGGING_STATE_STARTED);
            debugThread = new DebugThread(database, currentTrip);
            debugThread.start();
        }
    }

    private synchronized void stop () {
        if (state == BaseService.LOGGING_STATE_STARTED) {
            setState(BaseService.LOGGING_STATE_STOPPED);

            if (debugThread != null) {
                debugThread.cancel();
                debugThread = null;
            }
        }
    }

    synchronized void stopAndDelete () {
        stop();
        new TripModel.DeleteTripAsyncTask(database, null).execute(currentTrip);
    }

    synchronized void stopAndSave (String tripName) {
        stop();
        currentTrip.setName(tripName);
        new TripModel.UpdateAsyncTask(database, null).execute(currentTrip);
    }

    private static class DebugThread extends Thread {

        private final static int TIMEOUT_MS = 1000;

        private AppDatabase database;
        private final long tripId;
        private boolean running;

        public DebugThread (AppDatabase database, Trip trip) {
            this.database = database;
            this.tripId = trip.getId();
            running = true;
        }

        @Override
        public void run() {
            while (running) {
                database.packetDao().insert(new Packet(tripId));

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

}
