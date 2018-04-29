package com.elan_droid.elandroid.ui.bluetooth;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.elan_droid.elandroid.R;

/**
 * Created by Peter Smith
 */

public class DeviceSelectDialog extends DialogFragment implements DialogInterface.OnDismissListener {

    private DeviceListFragment deviceListFragment;

    private DeviceListFragment.OnDiscoveringStateListener discoveryListener;

    public static DialogFragment getInstance() {
        DialogFragment dialog = new DeviceSelectDialog();
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Registers the broadcast intent for finding devices
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null) {
            deviceListFragment = (DeviceListFragment) getActivity()
                    .getSupportFragmentManager().findFragmentById(R.id.device_list_fragment);
            deviceListFragment.startDiscovery();
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_device_search, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Search for nearby devices")
                .setView(view)
                .setPositiveButton("Stop", null)
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deviceListFragment.stopDiscovery();
                    }
                })
                .setOnDismissListener(this);

        final AlertDialog dialog = builder.create();

        discoveryListener = new DeviceListFragment.OnDiscoveringStateListener() {
            @Override
            public void onDiscoveryResult(String result) {
                switch (result) {
                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText("Stop");
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deviceListFragment.stopDiscovery();
                            }
                        });
                        break;

                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText("Retry");
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deviceListFragment.startDiscovery();
                            }
                        });
                        break;
                }
            }
        };

        return dialog;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        deviceListFragment.stopDiscovery();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregisters the receiver
        if (getContext() != null) {
            getContext().unregisterReceiver(mReceiver);
        }

        //Destroys the deviceListFragment
        if (deviceListFragment != null && getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .remove(deviceListFragment).commit();
        }
    }

    /**
     * The broadcast receiver notifies the fragment of system broadcasts
     */
    @NonNull
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (discoveryListener != null && action != null) {
                discoveryListener.onDiscoveryResult(action);
            }
        }
    };


}
