package com.elan_droid.elandroid.ui.trip_log;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.adapter.TripAdapter;
import com.elan_droid.elandroid.database.entity.Trip;
import com.elan_droid.elandroid.database.relation.Profile;
import com.elan_droid.elandroid.database.view_model.TripModel;
import com.elan_droid.elandroid.ui.generic.BaseFragment;

import java.util.List;

/**
 * Created by Peter Smith
 */

public class TripList extends BaseFragment {

    public static final String TAG = "ProfileList";

    // Database View Models
    private TripModel mModel;

    // UI variables
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private TripAdapter mAdapter;

    public static Fragment getInstance(long userId) {
        Fragment fragment = new TripList();
        fragment.setArguments(createArguments(userId));
        return fragment;
    }

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

    private void updateObserver(long profileId) {
        mModel.getTrips(profileId).removeObservers(this);
        mModel.getTrips(profileId).observe(this, new Observer<List<Trip>>() {
            @Override
            public void onChanged(@Nullable List<Trip> trips) {
                mAdapter.update(trips);
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu(menu) called");

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu(menu, inflater) called");

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_list, container, false);
        setHasOptionsMenu(true);
        getActivity().setTitle("Trip Log");

        mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.trip_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

}
