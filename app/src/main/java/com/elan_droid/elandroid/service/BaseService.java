package com.elan_droid.elandroid.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.entity.Packet;
import com.elan_droid.elandroid.database.entity.Trip;
import com.elan_droid.elandroid.database.relation.Profile;
import com.elan_droid.elandroid.database.view.TripModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by BorisJohnson on 4/22/2018.
 */

public class BaseService extends Service {

    public final static String TAG = "BaseService";

    public static final int MESSAGE_TOAST = 0;
    public static final int MESSAGE_REGISTER_CLIENT = 1;
    public static final int MESSAGE_UNREGISTER_CLIENT = 2;

    public static final int MESSAGE_COMMAND_START_LOGGING = 3;
    public static final int MESSAGE_LOGGING_STATE_CHANGE = 4;
        public static final int LOGGING_STATE_STARTED = 0;
        public static final int LOGGING_STATE_STOPPED = 1;
    public static final int MESSAGE_COMMAND_STOP_LOGGING = 5;




    private final Messenger messenger = new Messenger(new IncomingHandler());
    private final List<Messenger> clients = new ArrayList<>();

    private AppDatabase database;
    private LoggingManager loggingManager;

    private Profile activeProfile;
    private boolean init = false;

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST:
                    sendToClients(msg);
                    break;

                case MESSAGE_REGISTER_CLIENT:
                    if (!clients.contains(msg.replyTo)) {
                        clients.add(msg.replyTo);
                    }
                    break;

                case MESSAGE_UNREGISTER_CLIENT:
                    clients.remove(msg.replyTo);
                    break;

                case MESSAGE_COMMAND_START_LOGGING:
                    startLogging ((Profile) msg.obj);
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
            loggingManager = new LoggingManager(messenger, database);
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

    private void sendToast (String message) {
        Message msg = new Message();
        msg.what = MESSAGE_TOAST;
        msg.obj = message;
        sendToClients (msg);
    }



    private void startLogging (Profile profile) {
        if (activeProfile != null) {
            sendToast("There is already an active trip.");
            return;
        }
        if (profile == null) {
            sendToast("You can't start a trip with no profile selected");
            return;
        }

        new TripModel.PopulateAsyncTask(database, new TripModel.InsertTripCallback() {
            @Override
            public void onTripInserted(Trip trip) {
                if (trip != null) {
                    loggingManager.start(trip);
                }
                else
                    sendToast("Unable to create new trip.");
            }
        }).execute(new Trip(profile.getProfileId(), profile.getName() + "_tmp"));
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
