package com.elan_droid.elandroid.ui.bluetooth;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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

    public void bind (@NonNull final DeviceItem device, @NonNull final DeviceAdapter.DeviceSelectedListener listener) {
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
