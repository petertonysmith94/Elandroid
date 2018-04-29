package com.elan_droid.elandroid.ui.generic;

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
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.entity.Trip;
import com.elan_droid.elandroid.database.view_model.ActiveTrip;
import com.elan_droid.elandroid.service.BaseService;
import com.elan_droid.elandroid.service.BluetoothManager;
import com.elan_droid.elandroid.service.ConnectionStatus;
import com.elan_droid.elandroid.ui.bluetooth.DeviceAdapter;
import com.elan_droid.elandroid.ui.bluetooth.DeviceItem;
import com.elan_droid.elandroid.ui.bluetooth.DeviceSelectDialog;
import com.elan_droid.elandroid.ui.dialog.SaveTripDialog;

/**
 * Created by Peter Smith
 */

public abstract class ServiceActivity extends NavigationActivity implements
        DeviceAdapter.OnDeviceSelectedListener, SaveTripDialog.OnTripActionListener {

    private static final String TAG = "ServiceActivity";

    private ActiveTrip mTripModel;

    private ConnectionStatus mConnectionStatus;

    private final Messenger mMessenger = new Messenger(new IncomingHandler());
    private Messenger mServiceMessenger;
    private BaseService mService;
    private boolean mServiceBound = false;


    private ProgressDialog connectingDialog;

    // Handles the connection with the service
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mServiceBound = true;
            mServiceMessenger = new Messenger(binder);

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

    // Handles the messages with the service.
    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseService.MESSAGE_TOAST:
                    String toast = (String) msg.obj;
                    if (toast != null) {
                        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
                    }
                    break;

                case BaseService.MESSAGE_BLUETOOTH_STATE_CHANGE:
                    handleBluetoothState (msg);
                    break;

                case BaseService.MESSAGE_LOGGING_STATE_CHANGE:
                    handleLoggingState (msg);
                    break;
            }
        }
    }

    private void sendToService (Message msg) {
        if (mServiceBound) {
            try {
                mServiceMessenger.send(msg);
            } catch (RemoteException e) {

            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTripModel = ViewModelProviders.of(this).get(ActiveTrip.class);
        mTripModel.getActiveTrip().observe(this, new Observer<Trip>() {
            @Override
            public void onChanged(@Nullable Trip trip) {

            }
        });
        mConnectionStatus = ConnectionStatus.DISCONNECTED;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, BaseService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindService(connection);
    }

    private void setConnectionStatus(ConnectionStatus status) {
        if (mConnectionStatus != status) {
            this.mConnectionStatus = status;
            supportInvalidateOptionsMenu();
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(mConnectionStatus.getMenuResource(), menu);
        Log.d(TAG, "onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDeviceSelected(DeviceItem device) {
        connectBluetooth(device.getDevice());
        dismissDialog();
        displayConnectingDialog(device.getName());
    }

    private void displayConnectingDialog(String deviceName) {
        connectingDialog = ProgressDialog.show(this, "Connecting to device", "Connecting to: " + deviceName,
                true, true, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        disconnectBluetooth();
                    }
                });
    }

    private void dismissConnectingDialog() {
        if (connectingDialog != null) {
            connectingDialog.dismiss();
            connectingDialog = null;
        }
    }

    public void connectBluetooth (BluetoothDevice device) {
        Message msg = new Message();
        msg.what = BaseService.MESSAGE_COMMAND_BLUETOOTH_CONNECT;
        msg.obj = device;
        sendToService(msg);
    }

    private void connectedToBluetooth () {
        dismissConnectingDialog();
        Toast.makeText(this, "Connected to device", Toast.LENGTH_SHORT).show();
    }


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
                connectedToBluetooth();
                break;
        }
    }

    public void disconnectBluetooth () {
        Message msg = new Message();
        msg.what = BaseService.MESSAGE_COMMAND_BLUETOOTH_DISCONNECT;
        sendToService(msg);
    }

    public void startLogging () {
        Message msg = new Message();
        msg.what = BaseService.MESSAGE_COMMAND_START_LOGGING;
        msg.obj = new Trip(getActiveProfile());
        sendToService(msg);
    }

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
     * Launches dialog: do you want to save the current trip?
     */
    private void stopLogging() {
        DialogFragment dialog = SaveTripDialog.getInstance();
        displayDialog(dialog);
    }

    /**
     * Callbacks from SaveTripDialog, send to service
     */
    @Override
    public void onSaveTrip(String tripName) {
        serviceStopLogging(tripName);
    }
    @Override
    public void onDelete() {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_header_container:
                // Ensure's we can't change profiles while logging
                // TODO: dialog to switch profile while logging... maybe
                if (mConnectionStatus == ConnectionStatus.LOGGING) {
                    Toast.makeText(this, R.string.toast_cannot_profile_change, Toast.LENGTH_SHORT).show();
                    return;
                }
                break;

        }
        super.onClick(view);
    }

}
