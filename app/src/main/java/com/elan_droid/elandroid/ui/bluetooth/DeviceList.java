package com.elan_droid.elandroid.ui.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.elan_droid.elandroid.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith
 */

public class DeviceList extends BaseObservable {

    private List<DeviceItem> devices;
    private boolean discovering;

    DeviceList() {
        this.devices = new ArrayList<>();
        this.discovering = false;
    }

    @Bindable
    public int getDiscoveryVisibility() {
        return discovering ? View.VISIBLE : View.INVISIBLE;
    }

    @Bindable
    public boolean getDiscovering () {
        return discovering;
    }

    public void setDiscovering(boolean discovering) {
        this.discovering = discovering;
        notifyChange();
    }

    @Bindable
    public boolean getNothing () {
        return devices.size() == 0;
    }

    @Bindable
    public int getNothingVisibility() {
        return !discovering && devices.size() == 0  ? View.VISIBLE : View.GONE;
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
