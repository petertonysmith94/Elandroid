package com.elan_droid.elandroid.database.view;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
    private final MutableLiveData<Trip> mActiveTrip = new MutableLiveData<>();

    public final static int RESPONSE_SUCCESS = 0;
    public final static int RESPONSE_FAIL_ACTIVE_TRIP = 1;
    public final static int RESPONSE_FAIL_INSERT_TRIP = 2;

    public TripModel (Application application) {
        super(application);
        mDatabase = AppDatabase.getInstance(application);
    }

    public MutableLiveData<Trip> getActiveTrip () {
        return mActiveTrip;
    }

    private void setActiveTrip (Trip trip) {
        mActiveTrip.postValue(trip);
    }

    public boolean isActive() {
        return mActiveTrip.getValue() != null;
    }

    public void newActiveTrip (@NonNull Trip trip, @NonNull final ActiveTripListener listener) {
        // We're already active, report back to listener
        if (isActive()) {
            listener.onResponse(RESPONSE_FAIL_ACTIVE_TRIP);
        }
        else {
            newTrip(trip, new InsertTripCallback() {
                @Override
                public void onTripInserted(Trip trip) {
                    // Success, update new active trip
                    if (trip != null) {
                        setActiveTrip(trip);
                        listener.onResponse(RESPONSE_SUCCESS);
                    }
                    // Failed to create a new trip
                    else {
                        listener.onResponse(RESPONSE_FAIL_INSERT_TRIP);
                    }
                }
            });
        }
    }

    public interface ActiveTripListener {
        void onResponse (int responseCode);
    }

    public MutableLiveData<List<Trip>> getTrips (long userId) {
        updateTrips(userId);
        return mTrips;
    }

    public void updateTrips (long userId) {
        fetchTrips(userId, new FetchTripsCallback() {
            @Override
            public void onFetch(List<Trip> trips) {
                mTrips.setValue(trips);
            }
        });
    }

    public void newTrip (final Trip trip, final InsertTripCallback callback) {
        new PopulateAsyncTask(mDatabase, new InsertTripCallback() {
            @Override
            public void onTripInserted(Trip trip) {
                if (callback != null) {
                    callback.onTripInserted(trip);
                }

                if (trip != null) {
                    updateTrips(trip.getUserId());
                }
            }
        }).execute(trip);
    }

    public interface InsertTripCallback {
        void onTripInserted (Trip trip);
    }

    public static class PopulateAsyncTask extends AsyncTask<Trip, Void, Trip> {
        private AppDatabase mmDatabase;
        private InsertTripCallback mmCallback;

        public PopulateAsyncTask(AppDatabase database, InsertTripCallback callback) {
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

    public interface UpdateTripCallback {
        public void onUpdate (boolean success);
    }

    public static class UpdateAsyncTask extends AsyncTask<Trip, Void, Boolean> {

        private AppDatabase mmDatabase;
        private UpdateTripCallback mmCallback;

        public UpdateAsyncTask(AppDatabase database, UpdateTripCallback callback) {
            this.mmDatabase = database;
            this.mmCallback = callback;
        }

        @Override
        protected Boolean doInBackground(Trip... params) {
            if (params.length > 0) {
                mmDatabase.tripDao().update(params);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public interface DeleteTripCallback {
        public void onDelete (boolean success);
    }

    public static class DeleteTripAsyncTask extends AsyncTask<Trip, Void, Boolean> {

        private AppDatabase mmDatabase;
        private UpdateTripCallback mmCallback;

        public DeleteTripAsyncTask(AppDatabase database, UpdateTripCallback callback) {
            this.mmDatabase = database;
            this.mmCallback = callback;
        }

        @Override
        protected Boolean doInBackground(Trip... params) {
            if (params.length > 0) {
                mmDatabase.tripDao().delete(params);
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

}
