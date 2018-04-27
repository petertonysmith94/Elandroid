package com.elan_droid.elandroid.ui.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.databinding.ListItemBluetoothDeviceBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceItemHolder> {

    public interface OnDeviceSelectedListener {
        void onDeviceSelected(DeviceItem device);
    }

    private DeviceList mDevices;
    private OnDeviceSelectedListener mListener;

    public DeviceAdapter (@Nullable OnDeviceSelectedListener listener) {
        this (null, listener);
    }

    public DeviceAdapter (@Nullable List<BluetoothDevice> devices, @Nullable OnDeviceSelectedListener listener) {
        this.mDevices = new DeviceList(devices);
        this.mListener = listener;
    }

    public DeviceList getDevices() {
        return mDevices;
    }

    /**
     *
     * @param device
     */
    public void add (BluetoothDevice device) {
        mDevices.add(device);
        notifyDataSetChanged();
    }

    public void clear() {
        mDevices.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDevices.getSize();
    }


    @Override
    public DeviceItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListItemBluetoothDeviceBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_item_bluetooth_device, parent, false
        );

        return new DeviceItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(DeviceItemHolder holder, int position) {
        holder.bind(mDevices.get(position), mListener);
    }

}
