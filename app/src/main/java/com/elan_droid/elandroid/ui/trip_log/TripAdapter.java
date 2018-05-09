package com.elan_droid.elandroid.ui.trip_log;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.data.entity.Trip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith
 */

public class TripAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnTripSelectedListener {

        void onTripSelected (Trip trip);

    }

    private List<Trip> mTrips;
    private OnTripSelectedListener mSelectedListener;

    public TripAdapter () {
        this (null, null);
    }

    public TripAdapter (OnTripSelectedListener listener) {
        this (null, listener);
    }

    public TripAdapter (List<Trip> trips, OnTripSelectedListener listener) {
        this.mTrips = (trips == null) ? new ArrayList<Trip>() : trips;
        this.mSelectedListener = listener;
    }

    public void update (List<Trip> trips) {
        if (trips != null) {
            this.mTrips = trips;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.list_item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((TripViewHolder) holder).bind(mTrips.get(position), mSelectedListener);
    }

    private static class TripViewHolder extends RecyclerView.ViewHolder {

        private TextView mmTripName;
        private TextView mmTripStartTime;

        TripViewHolder (View view) {
            super (view);

            mmTripName = view.findViewById(R.id.list_item_trip_name);
            mmTripStartTime = view.findViewById(R.id.list_item_trip_date);
        }

        void bind(final Trip trip, final OnTripSelectedListener listener) {
            mmTripName.setText(trip.getName());
            //mmTripStartTime.setText(trip.getDate());

            if (listener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onTripSelected(trip);
                    }
                });
            }
        }

    }
}
