package com.elan_droid.elandroid.service;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.entity.Trip;
import com.elan_droid.elandroid.database.relation.Command;
import com.elan_droid.elandroid.database.relation.Profile;
import com.elan_droid.elandroid.database.view_model.CommandModel;
import com.elan_droid.elandroid.database.view_model.TripModel;
import com.elan_droid.elandroid.service.new_strategy.IOStrategy;
import com.elan_droid.elandroid.service.new_strategy.LoggingStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith on 4/22/2018.
 */

public class BaseService extends Service {

    public final static String TAG = "BaseService";

    public static final int MESSAGE_TOAST = 0;
    public static final int MESSAGE_REGISTER_CLIENT = 1;
    public static final int MESSAGE_UNREGISTER_CLIENT = 2;

    public static final int MESSAGE_COMMAND_BLUETOOTH_CONNECT = 3;
    public static final int MESSAGE_BLUETOOTH_STATE_CHANGE = 4;
    public static final int MESSAGE_COMMAND_BLUETOOTH_DISCONNECT = 5;

    public static final int MESSAGE_COMMAND_START_LOGGING = 7;
    public static final int MESSAGE_LOGGING_STATE_CHANGE = 8;
        public static final int LOGGING_STATE_STARTED = 0;
        public static final int LOGGING_STATE_STOPPED = 1;
    public static final int MESSAGE_COMMAND_STOP_LOGGING = 9;

    private final Messenger messenger = new Messenger(new IncomingHandler());
    private final List<Messenger> clients = new ArrayList<>();

    private AppDatabase database;
    private LoggingManager loggingManager;
    private BluetoothManager bluetoothManager;

    private Trip activeTrip;
    private Command activeCommand;

    private Profile activeProfile;
    private boolean init = false;

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_REGISTER_CLIENT:
                    //
                    if (!clients.contains(msg.replyTo)) {
                        clients.add(msg.replyTo);
                    }
                    break;

                case MESSAGE_UNREGISTER_CLIENT:
                    clients.remove(msg.replyTo);
                    break;

                case MESSAGE_COMMAND_BLUETOOTH_CONNECT:
                    connectBluetooth((BluetoothDevice) msg.obj);
                    break;

                case MESSAGE_COMMAND_BLUETOOTH_DISCONNECT:
                    disconnectBluetooth();
                    break;

                case MESSAGE_COMMAND_START_LOGGING:
                    startLoggingWithValidation( (Trip) msg.obj);
                    break;

                case MESSAGE_COMMAND_STOP_LOGGING:
                    stopLogging ((String) msg.obj);
                    break;

                default:
                    sendToClients(msg);
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!init) {
            database = AppDatabase.getInstance(getApplicationContext());
            init = true;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    /**
     * Sends a message to all registered clients of the service.
     * Will delete any clients which are unresponsive.
     * @param msg   the message in which to send
     */
    private void sendToClients(Message msg) {
        Message m = new Message();
        m.copyFrom(msg);

        //Iterates backwards to allow removal of elements
        for (int i = clients.size() - 1; i >= 0; i--)
        {
            try {
                clients.get(i).send(m);
            }
            catch (RemoteException e) {
                clients.remove(i);
                Log.e(TAG, "Removed client from client list");
            }
        }
    }

    /**
     * Send's a message back with some Toast in it, which will "popup" to user.
     * @param message   the message to be displayed
     */
    private void sendToast (String message) {
        Message msg = new Message();
        msg.what = MESSAGE_TOAST;
        msg.obj = message;
        sendToClients (msg);
    }


    /**
     * Bluetooth methods
     */

    /**
     *
     * @return
     */
    private BluetoothManager getBluetoothManager() {
        if (bluetoothManager == null) {
            bluetoothManager = new BluetoothManager(messenger);
        }
        return bluetoothManager;
    }

    /**
     * Connects to a given bluetooth device securely
     * @param device    the target Bluetooth device
     */
    private void connectBluetooth (BluetoothDevice device) {
        getBluetoothManager().connect(device, true);
    }

    /**
     * Disconnects from Bluetooth
     */
    private void disconnectBluetooth () {
        if (loggingManager != null) {
            loggingManager.stopAndSave("Latest trip");
        }
        bluetoothManager.disconnect();
    }


    /**
     * LOGGING SECTION
     */

    private LoggingManager getLoggingManager() {
        if (loggingManager == null) {
            loggingManager = new LoggingManager(messenger, database);
        }
        return loggingManager;
    }

    /**
     *
     * @param trip
     */
    private void startLoggingWithValidation (Trip trip) {

        if (loggingManager != null) {
            sendToast("There is already an active trip");
        }

        else if (trip == null) {
            sendToast("Unable to start trip, please try again");
        }

        else if (trip.getId() == 0) {   // We need to insert the trip into the database
            new TripModel.PopulateAsyncTask(database, new TripModel.InsertTripCallback() {
                @Override
                public void onTripInserted(Trip trip) {
                    if (trip != null) {
                        startLogging(trip);
                    }
                    else sendToast("Unable to create new trip.");
                }
            }).execute(trip);
        }
        // The trip is already in the database, continue with validation
        else startLogging(trip);
    }

    /**
     *
     * @param trip
     */
    private void startLogging (final Trip trip) {
        if (trip.getMessageId() == 0) {
            sendToast ("The trip has no message associated");
        }
        else {
            new CommandModel.FetchCommandTask(database, new CommandModel.FetchCommandCallback() {
                @Override
                public void onFetchCommand(Command command) {
                    startLogging(trip, command);
                }
            }).execute(trip.getMessageId());
        }
    }

    /**
     * Command for starting logging with NO validation.
     * DON'T use this method directly!
     * @param trip
     * @param command
     */
    private void startLogging (final Trip trip, final Command command) {
        this.activeTrip = trip;
        this.activeCommand = command;

        IOStrategy strategy = new LoggingStrategy(database, trip.getId(), command.getRequest(), command.getResponse());
        getBluetoothManager().setStrategy(strategy);
    }

    private void stopLogging (String tripName) {
        // We need to deleting the trip we just created
        if (tripName == null) {
            loggingManager.stopAndDelete();
        }
        // Otherwise we save it
        else {
            loggingManager.stopAndSave(tripName);
        }

    }




}
