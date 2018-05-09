package com.elan_droid.elandroid.ui.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.databinding.ListItemBluetoothDeviceBinding;

/**
 * Created by Peter Smith
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceItemHolder> {

    public interface DeviceSelectedListener {
        void onDeviceSelected(DeviceItem device);
    }

    private DeviceList mDevices;
    private DeviceSelectedListener mListener;


    public DeviceAdapter (@NonNull DeviceList devices, @Nullable DeviceSelectedListener listener) {
        this.mDevices = devices;
        this.mListener = listener;
    }

    public DeviceList getDevices() {
        return mDevices;
    }

    public void setDiscovery(boolean discovering) {
        mDevices.setDiscovering(discovering);
        notifyDataSetChanged();
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
