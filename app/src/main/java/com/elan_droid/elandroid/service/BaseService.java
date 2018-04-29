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
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.Toast;

import com.elan_droid.elandroid.R;
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
 *
 *
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

        database = AppDatabase.getInstance(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    /**
     * Sends a message to all registered clients of the service.
     * Will baseDelete any clients which are unresponsive.
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

    private void sendToast (@StringRes int resource, int length) {
        sendToast(getString(resource), length);
    }

    /**
     * Send's a message back with some Toast in it, which will "popup" to user.
     * @param message   the message to be displayed
     */
    private void sendToast (String message, int length) {
        Message msg = new Message();
        msg.what = MESSAGE_TOAST;
        msg.obj = message;
        msg.arg1 = length;
        sendToClients (msg);
    }


    /**
     * BLUETOOTH
     * METHODS
     * BELOW
     */

    /**
     * Get's the instance of the BluetoothManager
     * @return  the BluetoothManager
     */
    private BluetoothManager getBluetoothManager() {
        if (bluetoothManager == null) {
            bluetoothManager = BluetoothManager.getInstance(messenger);
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
        if (bluetoothManager != null)
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
     * Checks that:
     *  a) You're connect to a device with an open stream.
     *  b) There is not an active logging process.
     *  c)
     * @param trip
     */
    private void startLoggingWithValidation (Trip trip) {

        if (loggingManager != null) {
            sendToast(R.string.service_toast_logging_active, Toast.LENGTH_LONG);
        }

        else if (trip == null) {
            sendToast(R.string.service_toast_logging_unable_to_start, Toast.LENGTH_LONG);
        }

        else if (trip.getId() == 0) {   // We need to baseInsert the trip into the database
            new TripModel.InsertTripTask(database, new TripModel.InsertTripCallback() {
                @Override
                public void onTripInserted(Trip trip) {
                    // Ensure's the trip isn't null, otherwise sends toast
                    if (trip != null) {
                        startLogging(trip);
                    }
                    else sendToast(R.string.service_toast_logging_unable_to_create_trip, Toast.LENGTH_LONG);
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
            sendToast(R.string.service_toast_logging_unable_to_start, Toast.LENGTH_LONG);
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
        getBluetoothManager().startLogging(strategy);

        setLoggingState(LOGGING_STATE_STARTED);
    }

    /**
     *
     * @param state
     */
    private void setLoggingState (int state) {
        Message msg = new Message();
        msg.what = BaseService.MESSAGE_LOGGING_STATE_CHANGE;
        msg.arg1 = state;
        msg.obj = activeTrip;
        sendToClients(msg);
    }

    private void stopLogging (String tripName) {
        if (bluetoothManager != null) {
            bluetoothManager.stopLogging();
        }

        if (tripName == null)
            delete (activeTrip);
        else {
            this.activeTrip.setName(tripName);
            save(activeTrip);
        }
        this.activeTrip = null;
        this.activeCommand = null;
        setLoggingState(BaseService.LOGGING_STATE_STOPPED);
    }

    /**
     *
     * @param trip
     */
    private void delete (final Trip trip) {
        Log.i(TAG, "Deleting trip " + trip.toString());
        new TripModel.DeleteTripAsyncTask(database, new TripModel.DeleteTripCallback() {
            @Override
            public void onDelete(boolean success) {
                sendToast(success ? R.string.service_toast_logging_delete_success :
                        R.string.service_toast_logging_delete_failed, Toast.LENGTH_LONG);
            }
        }).execute(trip);
    }

    private void save (final Trip trip) {
        Log.i(TAG, "Saving trip " + trip.toString());
        new TripModel.UpdateAsyncTask(database, new TripModel.UpdateTripCallback() {
            @Override
            public void onUpdate(boolean success) {
                sendToast(success ? R.string.service_toast_logging_save_success :
                        R.string.service_toast_logging_save_failed , Toast.LENGTH_LONG);
            }
        }).execute(trip);
    }




}
