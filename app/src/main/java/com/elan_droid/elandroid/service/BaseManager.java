package com.elan_droid.elandroid.service;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import static com.elan_droid.elandroid.service.BaseService.MESSAGE_TOAST;

/**
 * Created by Peter Smith on 4/24/2018.
 *
 * BaseManager links the service to the inherited class with a messenger
 **/
public abstract class BaseManager {

    public final static String TAG = "BaseManager";

    private Messenger messenger;

    /**
     * BaseManager links the service to the inherited class with a messenger
     * @param messenger the service messenger
     */
    BaseManager (Messenger messenger) {
        this.messenger = messenger;
    }

    /**
     * Sends a message via the messenger which was passed in with the constructor
     * @param message the message to be sent
     */
    void send(android.os.Message message) {
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to send a message", e);
        }
    }

    /**
     * Sends a toast message via the messenger
     * @param toast     The message string to be sent
     */
    void sendToast(String toast) {
        Message msg = Message.obtain();
        msg.what = MESSAGE_TOAST;
        msg.obj = toast;
        send (msg);
    }

}
