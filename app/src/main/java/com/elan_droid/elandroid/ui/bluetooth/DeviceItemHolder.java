package com.elan_droid.elandroid.ui.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.databinding.DeviceListBinding;
import com.elan_droid.elandroid.databinding.ListItemBluetoothDeviceBinding;

/**
 * Created by Peter Smith on 4/26/2018.
 **/

public class DeviceItemHolder extends RecyclerView.ViewHolder {

    private ListItemBluetoothDeviceBinding mmBinding;

    public DeviceItemHolder (ListItemBluetoothDeviceBinding binding) {
        super (binding.getRoot());

        mmBinding = binding;
    }

    public void bind (@NonNull final DeviceItem device, @NonNull final DeviceAdapter.OnDeviceSelectedListener listener) {
        mmBinding.setDevice(device);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDeviceSelected(device);
            }
        });
    }

    public ListItemBluetoothDeviceBinding getBinding() {
        return mmBinding;
    }

}
