package com.elan_droid.elandroid.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Messenger;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.util.Log;

import com.elan_droid.elandroid.service.new_strategy.IOStrategy;
import com.elan_droid.elandroid.service.new_strategy.RequestStrategy;
import com.elan_droid.elandroid.service.new_strategy.ResponseStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Peter Smith on 4/26/2018.
 *
 *
 **/
public class BluetoothManager extends BaseManager {

    private final static String TAG = "BluetoothManager";

    public static final int BLUETOOTH_STATE_NONE = 0;
    public static final int BLUETOOTH_STATE_LISTEN = 1;
    public static final int BLUETOOTH_STATE_CONNECTING = 2;
    public static final int BLUETOOTH_STATE_CONNECTED = 3;

    private final BluetoothAdapter mAdapter;

    // Threads
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    private int state;
    private BluetoothDevice target;

    private static BluetoothManager INSTANCE;

    /**
     * Retrieves the single instance of the BluetoothManager
     * @param messenger
     * @return
     */
    public static BluetoothManager getInstance (Messenger messenger) {
        if (INSTANCE == null) {
            INSTANCE = new BluetoothManager(messenger);
        }
        return INSTANCE;
    }

    /**
     * Private the ensure that it is use with the singleton
     * @param messenger
     */
    private BluetoothManager(Messenger messenger) {
        super(messenger);

        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
        this.state = BLUETOOTH_STATE_NONE;
    }


    /**
     * Synchronously sets the state of the logging manager.
     * Only if the state has not changed.
     * @param state     the new state
     */
    private synchronized void setState(int state) {
        if (this.state != state) {
            Log.d(TAG, "setState()  " + this.state + " => " + state);
            this.state = state;

            android.os.Message msg = new android.os.Message();
            msg.what = BaseService.MESSAGE_BLUETOOTH_STATE_CHANGE;
            msg.arg1 = state;
            msg.obj = target;
            send(msg);
        }
    }

    /**
     * Synchronously retrieves the state
     * @return
     */
    public synchronized int getState () {
        return this.state;
    }

    /**
     * Connects securely to the given BluetoothDevice
     * @param device
     */
    protected synchronized void connect (BluetoothDevice device) {
        connect (device, true);
    }

    /**
     * Connects to a give Bluetooth device
     * @param device    the target Bluetooth device
     * @param secure    whether the connection should be secure
     */
    private synchronized void connect (BluetoothDevice device, boolean secure) {
        if (setTarget(device)) {
            ParcelUuid[] ids = device.getUuids();

            if (ids != null) {
                for (ParcelUuid uuid : ids) {
                    if (uuid != null) {
                        connect(device, secure, uuid.getUuid());
                        return;
                    }
                }
            }
        }
        // We're already connect to a device
        else {

        }
        // TODO: return error code
    }

    /**
     * Disconnects and cancels any active threads
     */
    protected synchronized void disconnect () {
        cancelConnectThread();
        cancelConnectedThread();

        setState(BLUETOOTH_STATE_NONE);
        setTarget(null);
    }

    /**
     * Get's the current targeted device
     * @return  the targeted BluetoothDevice
     */
    private synchronized BluetoothDevice getTarget () {
        return this.target;
    }

    /**
     * Set's the target device that we want to connect to.
     * @param device    the targeted device
     * @return  condition bluetooth not null and no current target
     */
    private synchronized boolean setTarget (BluetoothDevice device) {
        final boolean condition = state == BLUETOOTH_STATE_NONE;
        if (condition) {
            this.target = device;
        }
        return condition;
    }

    /**
     *
     * @param strategy
     */
    synchronized void startLogging (IOStrategy strategy) {
        ConnectedThread thread;

        synchronized (this) {
            if (state != BLUETOOTH_STATE_CONNECTED) {
                return;
            }
            thread = mConnectedThread;
        }

        thread.startLogging(strategy);
    }

    synchronized void stopLogging () {
        ConnectedThread thread;

        synchronized (this) {
            if (state != BLUETOOTH_STATE_CONNECTED) {
                return;
            }
            thread = mConnectedThread;
        }


        if (thread != null) {
            thread.stopLogging(new StopLoggingListener() {
                @Override
                public void onFinish(final BluetoothSocket socket, final InputStream in, final OutputStream out) {
                    if (socket != null) {
                        mConnectedThread = null;
                        mConnectedThread = new ConnectedThread(socket, in, out);
                        mConnectedThread.start();
                    }
                    else {
                        disconnect();
                    }
                }
            });
        }

    }

    /**
     *
     */
    private interface StopLoggingListener {

        /**
         *
         * @param socket
         */
        void onFinish (final BluetoothSocket socket, final InputStream in, final OutputStream out);
    }

    /**
     * Connects to a give Bluetooth device with no validation.
     * @param device    the target Bluetooth device
     * @param secure    whether the connection should be secure
     * @param uuid      the uuid to be used in the connection
     */
    private synchronized void connect (BluetoothDevice device, boolean secure, UUID uuid) {
        Log.d(TAG, "Connecting to: " + device + " with uuid: " + uuid.toString());
        mAdapter.cancelDiscovery(); //Saves power by canceling

        cancelConnectThread();

        setTarget(device);
        mConnectThread = new ConnectThread (device, secure, uuid);
        mConnectThread.start();
        setState (BLUETOOTH_STATE_CONNECTING);
    }

    /**
     * Connection has failed, send message to service.
     * TODO: send message to service
     * @param device
     * @param secure
     * @param uuid
     */
    private synchronized void connectFailed (BluetoothDevice device, boolean secure, UUID uuid) {
        disconnect();
    }

    /**
     * Transition to a connected state
     * @param socket    the socket established during ConnectThread
     * @param device    the device we are connected to
     */
    private synchronized void connected (BluetoothSocket socket, BluetoothDevice device) {
        Log.d(TAG, "BluetoothManager:connected() => " + device);
        setTarget(device);
        setState (BLUETOOTH_STATE_CONNECTED);

        //Cancels any threads
        cancelConnectThread();
        cancelConnectedThread();

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

    }

    /**
     * Connection has been lost
     * TODO: send message to service
     */
    private synchronized void connectionLost () {
        disconnect();
    }

    /**
     * Cancels any thread attempting to make a connection
     */
    private void cancelConnectThread() {
        if (mConnectThread != null) {
            //mConnectThread.interrupt();
            mConnectThread.cancel();
            mConnectThread = null;
        }
    }

    /**
     * Cancels any thread attempting to make a connection
     */
    private void cancelConnectedThread() {
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    /**
     * Establishes a connection with a BluetoothDevice.
     *  If successful, a BluetoothSocket will be the result.
     */
    private class ConnectThread extends Thread {

        private final BluetoothDevice mmDevice;
        private final boolean mmSecure;
        private final UUID mmUuid;

        private BluetoothSocket mmSocket = null;

        private ConnectThread (BluetoothDevice device, boolean secure, UUID uuid) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            mmSecure = secure;
            mmUuid = uuid;

            try {
                tmp = secure ? mmDevice.createRfcommSocketToServiceRecord(uuid)
                        : mmDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            }
            catch (IOException e) {
                Log.e(TAG, "ConnectThread:constructor(device, secure, uuid) failed to connect to socket", e);
                connectFailed(device, secure, uuid);
            }

            mmSocket = tmp;
        }

        @Override
        public void run() {
            try {
                if (mmSocket != null) {
                    mmSocket.connect();
                }
            }
            catch (IOException e) {
                Log.e(TAG, "ConnectThread:run() unable to connect to socket", e);

                try {
                    if (mmSocket != null) {
                        mmSocket.close();
                    }
                }
                catch (IOException e2) {
                    Log.e(TAG, "ConnectThread:run() unable to close the socket", e2);
                }

                mmSocket = null;
                connectFailed(mmDevice, mmSecure, mmUuid);
            }

            if (mmSocket != null) {
                //TODO: CAN I MOVE THIS TO THE connected() METHOD?
                synchronized (BluetoothManager.this) {
                    mConnectThread = null;
                }

                connected(mmSocket, mmDevice);
            }
        }

        public void cancel() {
            Log.d(TAG, "ConnectThread:cancel() called");
            try {
                if (mmSocket != null) {
                    mmSocket.close();
                    mmSocket = null;
                }
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread:cancel() failed to close the socket", e);
            }
        }
    }

    /**
     * Maintains the socket to a BluetoothDevice.
     * TODO: Move logging implementation into separate classes
     */
    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private RequestStrategy mmRequestStrategy;
        private ResponseStrategy mmResponseStrategy;

        private boolean mmHasResponse;
        private boolean mmAutoStart;

        private boolean mmStop;
        private StopLoggingListener stopListener;

        public ConnectedThread (final BluetoothSocket socket) {
            mmAutoStart = false;
            mmSocket = socket;
            mmStop = false;

            // Ensures socket ain't null then gets the I/0 stream
            if(socket != null) {
                InputStream tmpIn = null;
                OutputStream tmpOut = null;

                try {
                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();
                } catch (IOException e) {
                    Log.d(TAG, "ConnectedThread: constructor() couldn't get input and output streams", e);
                    disconnect();
                }

                mmInStream = tmpIn;
                mmOutStream = tmpOut;
            }
            else {
                disconnect();
                mmInStream = null;
                mmOutStream = null;
            }
        }

        private ConnectedThread (final BluetoothSocket socket, final InputStream in, final OutputStream out) {
            if (socket == null || in == null || out == null) {
                disconnect();
            }

            mmAutoStart = false;
            mmSocket = socket;
            mmStop = false;
            mmInStream = in;
            mmOutStream = out;
        }


        synchronized void startLogging (IOStrategy strategy) {
            if (strategy instanceof RequestStrategy) {
                this.mmRequestStrategy = (RequestStrategy) strategy;
            }

            if (mmRequestStrategy != null) {
                if (strategy instanceof ResponseStrategy) {
                    this.mmResponseStrategy = (ResponseStrategy) strategy;
                    mmHasResponse = true;
                }

                if (mmAutoStart) start();
            }
        }

        @Override
        public void run() {
            if (mmInStream == null) {
                connectionLost();
            }

            int result = IOStrategy.RESULT_TRIGGER;
            byte[] tmp = new byte[1024];

            while (state == BLUETOOTH_STATE_CONNECTED) {
                try {
                    while (mmRequestStrategy == null) {
                        if (mmStop) {
                            mmRequestStrategy = null;
                            mmResponseStrategy = null;
                            mmStop = false;
                        }
                        sleep(20);

                        if (mmInStream != null && mmInStream.available() > 0) {
                            mmInStream.skip(mmInStream.available());
                        }
                    }

                    if (result < 0) {
                        sleep(100);

                        if (mmInStream != null && mmInStream.available() > 0) {

                            if (mmInStream.available() > 0) {
                                sleep (200);

                                while (mmInStream.available() > 0) {
                                    long skip = mmInStream.skip(mmInStream.available());
                                    Log.i(TAG, "Skipping " + skip + " bytes");
                                    sleep(50);
                                }
                            }
                        }
                        //sleep(100);
                        result = IOStrategy.RESULT_TRIGGER;
                    }

                    if (result == IOStrategy.RESULT_TRIGGER) {
                        result = mmRequestStrategy.executeRequest(result, mmOutStream);
                    }

                    sleep (mmRequestStrategy.idleTimeout());

                    if (result == IOStrategy.RESULT_TRIGGERED && mmHasResponse) {
                        result = mmResponseStrategy.executeResponse(result, mmInStream);
                        result = mmResponseStrategy.postResponse(result);
                    }

                    if (mmStop) {
                        stopListener.onFinish(mmSocket, mmInStream, mmOutStream);
                        return;
                    }
                }
                catch (IOException e) {
                    Log.e(TAG, "ConnectedThread:run() connection lost", e);
                    connectionLost();
                }
                catch (InterruptedException e) {
                    Log.e(TAG, "ConnectedThread:run() failed to sleep thread", e);
                    return;
                }
            }
        }

        /**
         * Writes an array of bytes to the output stream
         * @param buffer    the bytes to write
         */
        void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "ConnectedThread:write(byte[]) exception during outputting", e);
            }
        }

        void stopLogging(@NonNull StopLoggingListener listener) {
            this.mmStop = true;
            this.stopListener = listener;
        }

        void cancel() {
            Log.d(TAG, "ConnectedThread:cancel() called");
            try {
                if (mmSocket != null) {
                    mmSocket.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "ConnectedThread:cancel() failed to close the socket", e);
            }
        }

    }

}
