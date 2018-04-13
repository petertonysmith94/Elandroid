package com.elan_droid.elandroid.ui.dialog;

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
import android.support.annotation.StyleRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.ui.profile.ProfileActivity;

/**
 * Created by Peter Smith
 */

public class BluetoothDeviceSelectDialog extends DialogFragment {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_COURSE_LOCATION = 1;

    private BluetoothAdapter mAdapter;

    public static DialogFragment getInstance() {
        DialogFragment dialog = new BluetoothDeviceSelectDialog();
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = BluetoothAdapter.getDefaultAdapter();

        // The device doesn't support Bluetooth
        if (mAdapter == null) {

        }

        // Check that Bluetooth is enabled, if it isn't then launch enable intent
        if (!mAdapter.isEnabled()) {
            Intent btEnable = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(btEnable, REQUEST_ENABLE_BT);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        getActivity().registerReceiver(mReceiver, filter);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    retryDialog();
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    discoveringDialog();
                    break;
            }
        }
    };

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_device_search, null);


        return view;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setPositiveButton(null, null)
                .setNegativeButton(null, null)
                .setView(getView())
                .create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initialDialog();
    }

    /**
     * Starts the process of discovering Bluetooth devices
     */
    private void startDiscovery () {
        // Check we have granted permissions to discover devices
        if (checkPermissions()) {
            mAdapter.startDiscovery();
        }
    }

    private void stopDiscovery () {
        if (mAdapter.isDiscovering()) {
            mAdapter.cancelDiscovery();
        }
    }

    /**
     * Checks that we have the necessary permissions
     * @return
     */
    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    REQUEST_COURSE_LOCATION);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COURSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Restart discovery, we have permission
                    startDiscovery();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
    }

    private void setupDialog(String title, final String positiveText, final String negativeText,
                             final View.OnClickListener positiveListener,
                             final View.OnClickListener negativeListener) {
        if (getDialog() != null) {
            final AlertDialog dialog = (AlertDialog) getDialog();
            //dialog.setOnShowListener(null);
            dialog.setView (getView());

            DialogInterface.OnClickListener listener = null;

            dialog.setTitle(title);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, positiveText, listener);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, negativeText, listener);

            if (positiveListener != null || negativeListener != null) {
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        if (positiveListener != null) {
                            Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            positive.setText(positiveText);
                            positive.setOnClickListener(positiveListener);
                        }
                        if (negativeListener != null) {
                            Button negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            negative.setText(negativeText);
                            negative.setOnClickListener(negativeListener);
                        }
                    }
                });
            }
        }
    }

    private void initialDialog() {
        setupDialog("Search for a Bluetooth device", "Search", "Cancel",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startDiscovery();
                    }
                },
                null);
    }

    private void discoveringDialog() {
        setupDialog("Searching for Bluetooth devices...", "Stop", "Cancel",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stopDiscovery();
                    }
                },
                null);
    }

    private void retryDialog() {
        setupDialog("Select a Bluetooth device or rediscover", "Retry", "Cancel",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startDiscovery();
                    }
                },
                null);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        FragmentManager manager = getActivity().getSupportFragmentManager();
        final Fragment fragment = manager.findFragmentById(R.id.device_list_fragment);
        if (fragment != null) {
            manager.beginTransaction().remove(fragment).commit();
        }
    }


}
