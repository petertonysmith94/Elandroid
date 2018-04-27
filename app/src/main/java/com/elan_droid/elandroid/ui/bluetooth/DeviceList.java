package com.elan_droid.elandroid.ui.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith
 */

public class DeviceList extends BaseObservable {

    private List<DeviceItem> devices;


    DeviceList(@Nullable List<BluetoothDevice> devices) {
        this.devices = new ArrayList<>();

        if (devices != null) {
            for (BluetoothDevice device : devices) {
                this.devices.add(new DeviceItem(device));
            }
        }
    }

    @Bindable
    public int getNothingVisibility() {
        return devices.size() > 0 ? View.GONE : View.VISIBLE;
    }

    @Bindable
    public int getListVisibility() {
        return devices.size() > 0 ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getSize() {
        return devices.size();
    }

    @Bindable
    public List<DeviceItem> getDevices() {
        return devices;
    }

    public void add (final BluetoothDevice device) {
        devices.add(devices.size(), new DeviceItem(device));
        notifyChange();
    }

    public void clear () {
        devices.clear();
        notifyChange();
    }

    public DeviceItem get (int position) {
        return devices.get(position);
    }

}
