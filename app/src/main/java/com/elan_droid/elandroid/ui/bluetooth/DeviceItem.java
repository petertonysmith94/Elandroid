package com.elan_droid.elandroid.ui.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * Created by Peter Smith on 4/26/2018.
 **/

public class DeviceItem extends BaseObservable {

    private BluetoothDevice device;

    public DeviceItem (BluetoothDevice device) {
        this.device = device;

    }

    @Bindable
    public String getName() {
        return device.getName();
    }

    @Bindable
    public boolean isPaired() {
        return device.getBondState() == BluetoothDevice.BOND_BONDED;
    }

    @Bindable
    public String getAddress() {
        return device.getAddress();
    }

    public BluetoothDevice getDevice() {
        return device;
    }
}
