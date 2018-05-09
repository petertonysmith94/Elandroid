package com.elan_droid.elandroid.ui.debug;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.data.entity.Trip;
import com.elan_droid.elandroid.database.view_model.ActiveTrip;

/**
 * Created by Peter Smith on 4/22/2018.
 */

public class ActiveTripDebug extends Fragment {

    private ActiveTrip mModel;

    private TextView tripId;
    private TextView packetCount;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mModel = ViewModelProviders.of(getActivity()).get(ActiveTrip.class);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_trip_debug,  container);

        tripId = view.findViewById(R.id.debug_current_trip);
        packetCount = view.findViewById(R.id.debug_current_packet);



        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mModel.getActiveTrip().observe(getActivity(), new Observer<Trip>() {
            @Override
            public void onChanged(@Nullable Trip trip) {
                tripId.setText(trip == null ? "-" : "" + trip.getId());
            }
        });
        mModel.getPacketCount().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer count) {
                packetCount.setText(count == null ? "-" : count.toString());
            }
        });
    }


}
