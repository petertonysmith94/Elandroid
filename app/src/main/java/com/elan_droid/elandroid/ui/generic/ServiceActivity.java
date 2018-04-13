package com.elan_droid.elandroid.ui.generic;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.entity.Trip;
import com.elan_droid.elandroid.database.view.TripModel;
import com.elan_droid.elandroid.service.ConnectionStatus;
import com.elan_droid.elandroid.ui.dialog.BluetoothDeviceSelectDialog;
import com.elan_droid.elandroid.ui.dialog.ProfileSelectDialog;

/**
 * Created by Peter Smith
 */

public abstract class ServiceActivity extends NavigationActivity {

    private static final String TAG = "ServiceActivity";

    private TripModel mTripModel;

    private ConnectionStatus mConnectionStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTripModel = ViewModelProviders.of(this).get(TripModel.class);
        mConnectionStatus = ConnectionStatus.DISCONNECTED;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Display
            case R.id.menu_disconnected_connect:
                displayDialog(BluetoothDeviceSelectDialog.getInstance());
                setConnectionStatus(ConnectionStatus.CONNECTED);
                Toast.makeText(getApplicationContext(), "Connecting", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_connected_start_logging:
                setConnectionStatus(ConnectionStatus.LOGGING);
                return true;

            case R.id.menu_connected_disconnect:
                setConnectionStatus(ConnectionStatus.LOGGING);
                return true;

            case R.id.menu_logging_stop:
                setConnectionStatus(ConnectionStatus.DISCONNECTED);
                return true;

            case R.id.menu_logging_disconnect:
                setConnectionStatus(ConnectionStatus.DISCONNECTED);
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
