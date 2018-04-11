package com.elan_droid.elandroid.database.view;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.entity.Page;
import com.elan_droid.elandroid.database.entity.Trip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith
 */

public class TripModel extends AndroidViewModel {

    private AppDatabase mDatabase;

    private LiveData<List<Trip>> mAllTrips;
    private final MutableLiveData<List<Trip>> mTrips = new MutableLiveData<>();

    public TripModel (Application application) {
        super(application);
        mDatabase = AppDatabase.getInstance(application);
    }

    public MutableLiveData<List<Trip>> getTrips (long userId) {
        fetchTrips(userId, new FetchTripsCallback() {
            @Override
            public void onFetch(List<Trip> trips) {
                mTrips.setValue(trips);
            }
        });
        return mTrips;
    }

    public void newTrip (final Trip trip, final InsertTripCallback callback) {
        new PopulateAsyncTask(mDatabase, new InsertTripCallback() {
            @Override
            public void onTripInserted(Trip trip) {
                if (callback != null) {
                    callback.onTripInserted(trip);
                }

                if (trip != null) {
                    getTrips(trip.getUserId());
                }
            }
        }).execute(trip);
    }

    public interface InsertTripCallback {
        void onTripInserted (Trip trip);
    }

    private static class PopulateAsyncTask extends AsyncTask<Trip, Void, Trip> {
        private AppDatabase mmDatabase;
        private InsertTripCallback mmCallback;

        PopulateAsyncTask(AppDatabase database, InsertTripCallback callback) {
            this.mmDatabase = database;
            this.mmCallback = callback;
        }

        @Override
        protected Trip doInBackground(Trip... params) {
            Trip result = (params.length > 0) ? params[0] : null;

            if (result != null) {
                long tripId = mmDatabase.tripDao().insert(params[0]);

                if (tripId != -1) {
                    result.setId(tripId);
                }
                else {
                    result = null;
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Trip result) {
            if (mmCallback != null) {
                mmCallback.onTripInserted(result);
            }
        }
    }

    public void fetchTrips (long userId, FetchTripsCallback callback) {
        new FetchAsyncTask(mDatabase, callback).execute(userId);
    }

    public interface FetchTripsCallback {
        void onFetch (List<Trip> trips);
    }

    private static class FetchAsyncTask extends AsyncTask<Long, Void, List<Trip>> {

        private AppDatabase mmDatabase;
        private FetchTripsCallback mmCallback;

        FetchAsyncTask(AppDatabase database, FetchTripsCallback callback) {
            this.mmDatabase = database;
            this.mmCallback = callback;
        }

        @Override
        protected List<Trip> doInBackground(Long... params) {
            List<Trip> trips = null;
            if(params.length > 0 && params[0] != 0) {
                trips = mmDatabase.tripDao().getTrips(params[0]);
            }
            return trips;
        }

        @Override
        protected void onPostExecute(List<Trip> trips) {
            if (mmCallback != null) {
                mmCallback.onFetch(trips);
            }
        }

    }

}
