package com.elan_droid.elandroid.ui.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.databinding.DeviceListBinding;

/**
 * Created by Peter Smith
 */
public class DeviceListFragment extends Fragment {


    public interface OnDiscoveringStateListener {

        void onDiscoveryResult (String result);

    }

    // Request code constants
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_COURSE_LOCATION = 1;

    private BluetoothAdapter mAdapter;

    // UI variables
    private DeviceList mDeviceList;
    private DeviceAdapter mDeviceAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // Listeners
    private DeviceAdapter.DeviceSelectedListener mDeviceListener;

    public static DeviceListFragment getInstance() {
        return new DeviceListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mDeviceListener = (DeviceAdapter.DeviceSelectedListener) getActivity();
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DeviceAdapter.DeviceSelectedListener");
        }

        // Registers the broadcast intent for finding devices
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = BluetoothAdapter.getDefaultAdapter();

        // The device doesn't support Bluetooth
        if (mAdapter == null) {
            // TODO: respond to activity?
        }
        else {
            // Check that Bluetooth is enabled, if it isn't then launch enable intent
            if (!mAdapter.isEnabled()) {
                Intent btEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(btEnable, REQUEST_ENABLE_BT);
            }

            mDeviceList = new DeviceList();
            mDeviceAdapter = new DeviceAdapter(mDeviceList, mDeviceListener);
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

            if (action != null) {
                switch (action) {
                    case BluetoothDevice.ACTION_FOUND:
                        final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        mDeviceAdapter.add(device);
                        break;

                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        mDeviceAdapter.clear();
                        mDeviceList.setDiscovering(true);
                        break;

                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        mDeviceList.setDiscovering(false);
                        break;
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onDetach() {
        super.onDetach();

        if (getContext() != null) {
            getContext().unregisterReceiver(mReceiver);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DeviceListBinding binding = DeviceListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mLayoutManager = new LinearLayoutManager(getContext());

        RecyclerView mRecyclerView = view.findViewById(R.id.device_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mDeviceAdapter);

        binding.setData(mDeviceAdapter.getDevices());

        return view;
    }

    /**
     * Starts the process of discovering Bluetooth devices
     */
    public void startDiscovery () {
        // Check we have granted permissions to discover devices
        if (checkPermissions()) {
            mAdapter.startDiscovery();
        }
    }

    public void stopDiscovery () {
        if (mAdapter.isDiscovering()) {
            mAdapter.cancelDiscovery();
        }
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

    /**
     * Checks that we have the necessary permissions
     * @return
     */
    private boolean checkPermissions() {
        boolean result = false;

        if (getActivity() != null) {
            // Failed to get permission for "coarse" access (used for BT)
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_COURSE_LOCATION);
                result = false;
            }
            else {
                result = true;
            }
        }
        return result;
    }

}
