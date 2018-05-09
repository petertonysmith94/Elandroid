package com.elan_droid.elandroid.ui.dashboard.page;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.elan_droid.elandroid.database.data.entity.Trip;
import com.elan_droid.elandroid.database.view_model.ActiveTrip;

/**
 * Created by Peter Smith on 4/21/2018.
 */
public abstract class BasePage extends Fragment {

    protected ActiveTrip tripModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tripModel = ViewModelProviders.of(getActivity()).get(ActiveTrip.class);
        tripModel.getActiveTrip().observe(this, new Observer<Trip>() {
            @Override
            public void onChanged(@Nullable Trip trip) {
                startTrip(trip);
            }
        });
    }


    public abstract void startTrip (Trip trip);


}
