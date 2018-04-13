package com.elan_droid.elandroid.ui.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith
 */

public class Devices extends BaseObservable {

    private List<BluetoothDevice> devices;

    Devices(List<BluetoothDevice> devices) {
        this.devices = devices == null ? new ArrayList<BluetoothDevice>() : devices;
    }

    @Bindable
    public int getSize() {
        return devices.size();
    }

    @Bindable
    public List<BluetoothDevice> getDevices() {
        return devices;
    }

    public void add (final BluetoothDevice device) {
        devices.add(devices.size(), device);
        notifyChange();
    }

    public void clear () {
        devices.clear();
        notifyChange();
    }

    public BluetoothDevice get (int position) {
        return devices.get(0);
    }

}
