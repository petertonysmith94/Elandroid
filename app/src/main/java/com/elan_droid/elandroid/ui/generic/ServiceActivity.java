package com.elan_droid.elandroid.ui.generic;

import android.app.Dialog;
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
import android.support.v7.app.AlertDialog;
import android.telecom.Connection;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.entity.Trip;
import com.elan_droid.elandroid.database.relation.Profile;
import com.elan_droid.elandroid.database.view.ActiveTrip;
import com.elan_droid.elandroid.database.view.TripModel;
import com.elan_droid.elandroid.service.BaseService;
import com.elan_droid.elandroid.service.ConnectionStatus;
import com.elan_droid.elandroid.ui.dialog.SaveTripDialog;
import com.elan_droid.elandroid.ui.profile.ProfileActivity;

/**
 * Created by Peter Smith
 */

public abstract class ServiceActivity extends NavigationActivity {



    private static final String TAG = "ServiceActivity";

    private ActiveTrip mTripModel;

    private ConnectionStatus mConnectionStatus;

    private final Messenger mMessenger = new Messenger(new IncomingHandler());
    private Messenger mServiceMessenger;
    private BaseService mService;
    private boolean mServiceBound = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTripModel = ViewModelProviders.of(this).get(ActiveTrip.class);
        mTripModel.getTrip().observe(this, new Observer<Trip>() {
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

    public void connect (BluetoothDevice device) {

    }

    public void startLogging () {
        Message msg = new Message();
        msg.what = BaseService.MESSAGE_COMMAND_START_LOGGING;
        msg.obj = getActiveProfile();
        sendToService(msg);
    }

    private void handleLoggingState (Message msg) {
        switch (msg.arg1) {
            case BaseService.LOGGING_STATE_STARTED:
                setConnectionStatus(ConnectionStatus.LOGGING);
                Trip trip = (Trip) msg.obj;

                if (trip != null) {
                    mTripModel.setTrip(trip);
                }
                break;

            case BaseService.LOGGING_STATE_STOPPED:
                setConnectionStatus(ConnectionStatus.CONNECTED);
                break;
        }
    }


    private void launchStopTripDialog (DialogInterface.OnClickListener positiveListener) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure that you want to stop logging the current trip?")
                // Inverted because we don't want users to end the trip...
                .setNegativeButton("Yes", positiveListener)
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    /**
     *
     * @param name      null then don't save, non-null then save
     */
    private void stopLogging (String name) {
        Message msg = new Message();
        msg.what = BaseService.MESSAGE_COMMAND_STOP_LOGGING;
        msg.obj = name;
        sendToService(msg);

        setConnectionStatus(ConnectionStatus.CONNECTED);
    }


    private void stopLogging (final boolean disconnect) {
        // Launch dialog, Are you sure you want to end the trip?
        launchStopTripDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                // Launch dialog, Do you want to save the trip?
                DialogFragment dialog = SaveTripDialog.getInstance();
                displayDialog(dialog);
            }
        });
    }

    public void saveTrip (@NonNull String name) {
        stopLogging(name);
    }

    public void deleteTrip () {
        stopLogging(null);
    }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Display
            case R.id.menu_disconnected_connect:
                //displayDialog(BluetoothDeviceSelectDialog.getInstance());
                setConnectionStatus(ConnectionStatus.CONNECTED);
                //Toast.makeText(getApplicationContext(), "Connecting", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_connected_start_logging:
                startLogging();
                return true;

            case R.id.menu_connected_disconnect:
                //setConnectionStatus(ConnectionStatus.LOGGING);
                return true;

            case R.id.menu_logging_stop:
                stopLogging(false);
                return true;

            case R.id.menu_logging_disconnect:
                stopLogging(true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_header_container:
                if (mConnectionStatus != ConnectionStatus.LOGGING) {
                    // Launches the profile selection dialogs
                    super.onClick(view);
                }
                else {
                    Toast.makeText(this, R.string.toast_cannot_profile_change, Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

}
