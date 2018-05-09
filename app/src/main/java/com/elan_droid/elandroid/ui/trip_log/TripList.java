package com.elan_droid.elandroid.ui.trip_log;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.data.entity.Trip;
import com.elan_droid.elandroid.database.data.relation.Profile;
import com.elan_droid.elandroid.database.view_model.TripModel;
import com.elan_droid.elandroid.ui.generic.BaseFragment;

import java.util.List;

/**
 * Created by Peter Smith
 *
 * The responsibility of the TripList, is to provide a display of all trip associated
 * with a given profile.
 */
public class TripList extends BaseFragment {

    public static final String TAG = "ProfileList";

    // Trip view model and adapter
    private TripModel mModel;
    private TripAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mModel = ViewModelProviders.of (getActivity()).get(TripModel.class);
        mAdapter = new TripAdapter(new TripAdapter.OnTripSelectedListener() {
            @Override
            public void onTripSelected(Trip trip) {

            }
        });
        updateObserver(getActiveProfileId());


        getProfileModel().getActiveProfile().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(@Nullable Profile profile) {
                updateObserver(getActiveProfileId());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_list, container, false);
        setTitle(R.string.trip_log_title);
        setHasOptionsMenu(true);

        // Recycler View setup
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.trip_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    /**
     *
     * @param profileId
     */
    private void updateObserver(long profileId) {
        mModel.getTrips(profileId).removeObservers(this);
        mModel.getTrips(profileId).observe(this, new Observer<List<Trip>>() {
            @Override
            public void onChanged(@Nullable List<Trip> trips) {
                mAdapter.update(trips);
            }
        });
    }




}
