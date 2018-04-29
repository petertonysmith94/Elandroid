package com.elan_droid.elandroid.service;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import static com.elan_droid.elandroid.service.BaseService.MESSAGE_TOAST;

/**
 * Created by Peter Smith on 4/24/2018.
 **/

public abstract class BaseManager {

    public final static String TAG = "BaseManager";

    private Messenger messenger;

    public BaseManager (Messenger messenger) {
        this.messenger = messenger;
    }

    /**
     * Sends a message via the messenger which was passed in with the constructor
     *
     * @param message the message to be sent
     */
    protected void send(android.os.Message message) {
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to send a message", e);
        }
    }

    /**
     *
     * @param toast
     */
    protected void sendToast(String toast) {
        Message msg = Message.obtain();
        msg.what = MESSAGE_TOAST;
        msg.obj = toast;
        send (msg);
    }

}
