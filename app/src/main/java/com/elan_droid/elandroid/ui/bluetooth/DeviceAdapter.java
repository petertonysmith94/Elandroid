package com.elan_droid.elandroid.ui.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.adapter.ProfileAdapter;
import com.elan_droid.elandroid.database.relation.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith
 */

public class DeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnDeviceSelectedListener {
        void onSelected (BluetoothDevice device);
    }

    private Devices mDevices;
    private OnDeviceSelectedListener mListener;

    public DeviceAdapter () {
        this (null, null);
    }

    public DeviceAdapter (@Nullable List<BluetoothDevice> devices, @Nullable OnDeviceSelectedListener listener) {
        this.mDevices = new Devices(devices);
        this.mListener = listener;
    }

    public Devices getDevices() {
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bluetooth_device, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((DeviceViewHolder) holder).bind(mDevices.get(position), mListener);
    }


    private static class DeviceViewHolder extends RecyclerView.ViewHolder {

        public static final int TYPE = 1;

        private ImageView mmImage;
        private TextView mmName;
        private TextView mmMac;

        DeviceViewHolder (View view) {
            super (view);

            mmImage = (ImageView) view.findViewById(R.id.list_item_device_paired_image);
            mmName = (TextView) view.findViewById(R.id.list_item_device_name_text);
            mmMac = (TextView) view.findViewById(R.id.list_item_device_mac_text);
        }

        void bind(final BluetoothDevice device, final OnDeviceSelectedListener listener) {
            mmImage.setImageResource(device.getBondState() == BluetoothDevice.BOND_BONDED ?
                                        android.R.drawable.star_big_on :
                                        android.R.drawable.star_big_off);

            String name = device.getName() == "" ? "Name not available" : device.getName();
            mmName.setText(name);
            mmMac.setText(device.getAddress());

            if (listener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onSelected(device);
                    }
                });
            }
        }

    }


}
