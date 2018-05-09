package com.elan_droid.elandroid.activity;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.data.entity.Trip;
import com.elan_droid.elandroid.database.view_model.ActiveTrip;
import com.elan_droid.elandroid.service.BaseService;
import com.elan_droid.elandroid.service.BluetoothManager;
import com.elan_droid.elandroid.service.ConnectionStatus;
import com.elan_droid.elandroid.ui.bluetooth.DeviceAdapter;
import com.elan_droid.elandroid.ui.bluetooth.DeviceItem;
import com.elan_droid.elandroid.ui.dialog.DeviceSelectDialog;
import com.elan_droid.elandroid.ui.dialog.SaveTripDialog;

/**
 * Created by Peter Smith
 *
 * The responsibility of the ServiceActivity, is to handle the mServiceConnection to the Service.
 */
public abstract class ServiceActivity extends NavigationActivity implements
        DeviceAdapter.DeviceSelectedListener, SaveTripDialog.OnTripActionListener {

    private static final String SAVED_CONNECTION_STATUS =
            "com.elan_droid.elandroid.activity.SAVED_CONNECTION_STATUS";


    private static final String TAG = "ServiceActivity";

    // Service variables
    private final Messenger mMessenger = new Messenger(new Handler(new IncomingHandler()));
    private Messenger mServiceMessenger;
    private boolean mServiceBound = false;

    // State of mServiceConnection
    private ConnectionStatus mConnectionStatus;

    // The ActiveTrip ViewModel
    private ActiveTrip mTripModel;

    // Connecting dialog reference
    private ProgressDialog connectingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Remove????
        mTripModel = ViewModelProviders.of(this).get(ActiveTrip.class);
        mTripModel.getActiveTrip().observe(this, new Observer<Trip>() {
            @Override
            public void onChanged(@Nullable Trip trip) {

            }
        });

        if (savedInstanceState == null) {
            mConnectionStatus = ConnectionStatus.DISCONNECTED;
        }
    }

    //TODO: Load state from service to ensure correct
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(SAVED_CONNECTION_STATUS, mConnectionStatus);
    }

    //TODO: Load state from service to ensure correct
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mConnectionStatus = (ConnectionStatus) savedInstanceState.getSerializable(SAVED_CONNECTION_STATUS);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Binds the service to this activity
        Intent intent = new Intent(this, BaseService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindService(mServiceConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(mConnectionStatus.getMenuResource(), menu);
        Log.d(TAG, "onCreateOptionsMenu");
        return true;
    }

    // Handles the mServiceConnection with the service
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mServiceBound = true;
            mServiceMessenger = new Messenger(binder);

            // Registers client with service
            Message msg = new Message();
            msg.what = BaseService.MESSAGE_REGISTER_CLIENT;
            msg.replyTo = mMessenger;
            sendToService(msg);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBound = false;
            //mServiceMessenger = null;
        }
    };

    /**
     *  Handles the messages with the service.
     */
    private class IncomingHandler implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case BaseService.MESSAGE_TOAST:
                    String toast = (String) msg.obj;
                    if (toast != null) {
                        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
                    }
                    return true;

                case BaseService.MESSAGE_BLUETOOTH_STATE_CHANGE:
                    handleBluetoothState (msg);
                    return true;

                case BaseService.MESSAGE_LOGGING_STATE_CHANGE:
                    handleLoggingState (msg);
                    return true;
            }
            return false;
        }
    }

    /**
     * Sends a message directly to the service, if service bound
     * @param msg   service message
     */
    private void sendToService (Message msg) {
        if (mServiceBound) {
            try {
                mServiceMessenger.send(msg);
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to send message to service");
            }
        }
    }

    /*
    BLUETOOTH METHODS
     */

    /**
     * Set the connecting status, update the menu
     * @param status    current status
     */
    private void setConnectionStatus(ConnectionStatus status) {
        if (mConnectionStatus != status) {
            this.mConnectionStatus = status;
            supportInvalidateOptionsMenu();
            invalidateOptionsMenu();
        }
    }

    /**
     * Display the connecting to BT device dialog
     * @param deviceName    the device name we're connecting to
     */
    private void displayConnectingDialog(String deviceName) {
        connectingDialog = ProgressDialog.show(this, "Connecting to device", "Connecting to: " + deviceName,
                true, true, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        disconnectBluetooth();
                    }
                });
    }

    /**
     * DeviceItem selected, connect to the device and display connecting dialog
     * Callback provided by DeviceAdapter.DeviceSelectedListener
     * @param device    the DeviceItem selected
     */
    @Override
    public void onDeviceSelected(DeviceItem device) {
        connectBluetooth(device.getDevice());
        dismissDialog();
        displayConnectingDialog(device.getName());
    }

    /**
     * Dismiss the connecting dialog display
     */
    private void dismissConnectingDialog() {
        if (connectingDialog != null) {
            connectingDialog.dismiss();
            connectingDialog = null;
        }
    }

    /**
     * Sends message to service, connect to Bluetooth Device
     * @param device    the target BluetoothDevice
     */
    private void connectBluetooth (BluetoothDevice device) {
        Message msg = new Message();
        msg.what = BaseService.MESSAGE_COMMAND_BLUETOOTH_CONNECT;
        msg.obj = device;
        sendToService(msg);
    }

    /**
     * Handles a Bluetooth state message from the service
     */
    private void handleBluetoothState (Message msg) {
        switch (msg.arg1) {
            case BluetoothManager.BLUETOOTH_STATE_NONE:
                setConnectionStatus(ConnectionStatus.DISCONNECTED);
                break;

            case BluetoothManager.BLUETOOTH_STATE_LISTEN:

                break;

            case BluetoothManager.BLUETOOTH_STATE_CONNECTING:

                break;

            case BluetoothManager.BLUETOOTH_STATE_CONNECTED:
                setConnectionStatus(ConnectionStatus.CONNECTED);
                dismissConnectingDialog();
                Toast.makeText(this, "Connected to device", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * Sends message to service, disconnect from Bluetooth
     */
    private void disconnectBluetooth () {
        Message msg = new Message();
        msg.what = BaseService.MESSAGE_COMMAND_BLUETOOTH_DISCONNECT;
        sendToService(msg);
    }

    /*
    LOGGING METHODS
     */

    /**
     * Sends message to service, start logging
     */
    private void startLogging () {
        Message msg = new Message();
        msg.what = BaseService.MESSAGE_COMMAND_START_LOGGING;
        msg.obj = new Trip(getActiveProfile());     // Create a trip from the active profile
        sendToService(msg);
    }

    /**
     * Handles a Logging state messages from the service
     * @param msg   state message
     */
    private void handleLoggingState (Message msg) {
        switch (msg.arg1) {
            case BaseService.LOGGING_STATE_STARTED:
                final Trip trip = (Trip) msg.obj;

                if (trip != null) {
                    setConnectionStatus(ConnectionStatus.LOGGING);
                }
                mTripModel.setTrip(trip);
                break;

            case BaseService.LOGGING_STATE_STOPPED:
                setConnectionStatus(ConnectionStatus.CONNECTED);
                mTripModel.setTrip(null);
                break;
        }
    }

    /**
     * Launches a dialog to save the current trip?
     */
    private void stopLogging() {
        DialogFragment dialog = SaveTripDialog.getInstance();
        displayDialog(dialog);
    }

    /**
     * Callbacks from SaveTripDialog, send to service stop logging
     * @param tripName  the name of the trip
     */
    @Override
    public void onSaveCurrentTrip(@NonNull String tripName) {
        serviceStopLogging(tripName);
    }

    /**
     * Callbacks from SaveTripDialog, send to service stop logging
     */
    @Override
    public void onDeleteCurrentTrip () {
        serviceStopLogging(null);
    }

    /**
     * Sends message to the service, notifying it to stop logging
     * @param name      null then don't save, non-null then save
     */
    private void serviceStopLogging (@Nullable String name) {
        Message msg = new Message();
        msg.what = BaseService.MESSAGE_COMMAND_STOP_LOGGING;
        msg.obj = name;
        sendToService(msg);

        setConnectionStatus(ConnectionStatus.CONNECTED);
    }

    /**
     * Listener for the service menu options, connect, start logging, ect...
     * @param item  the menu item
     * @return      indication to the success
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                displayDialog(DeviceSelectDialog.getInstance());
                return true;

            case R.id.menu_connected_start_logging:
                startLogging();
                return true;

            case R.id.menu_connected_disconnect:
                disconnectBluetooth();
                return true;

            case R.id.menu_logging_stop:
                stopLogging();
                return true;

            case R.id.menu_logging_disconnect:
                stopLogging();
                disconnectBluetooth();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Overridden the onClick listener from NavigationActivity.
     * Ensures that the user can not change profile while logging is occurring.
     * @param view  the originating view which was clicked
     */
    @Override
    public void onClick(View view) {
        // If we're logging, then block profile change
        if (view.getId() == R.id.nav_header_container &&
                mConnectionStatus == ConnectionStatus.LOGGING) {
            Toast.makeText(this, R.string.toast_cannot_profile_change, Toast.LENGTH_SHORT).show();
        }
        else {
            super.onClick(view);
        }
    }

}
