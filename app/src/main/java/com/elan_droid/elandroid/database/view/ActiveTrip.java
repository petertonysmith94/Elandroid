package com.elan_droid.elandroid.database.view;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.entity.Packet;
import com.elan_droid.elandroid.database.entity.Trip;

import java.util.List;

/**
 * Created by BorisJohnson on 4/22/2018.
 */

public class ActiveTrip extends AndroidViewModel {

    private AppDatabase database;
    private PacketModel packetModel;

    private final MutableLiveData<Trip> trip;

    private final LiveData<Packet> latestPacket;

    private final LiveData<Integer> packetCount;
    //private final LiveData<Packet> latestPacket;


    public ActiveTrip (@NonNull Application application) {
        super (application);
        packetModel = new PacketModel(application);

        this.database = AppDatabase.getInstance(application);
        this.trip = new MutableLiveData<>();
        this.latestPacket = Transformations.switchMap(this.trip, new Function<Trip, LiveData<Packet>>() {
            @Override
            public LiveData<Packet> apply(Trip trip) {
                return trip == null ? null : database.packetDao().getLatest(trip.getId());
            }
        });
        this.packetCount = Transformations.switchMap(this.trip, new Function<Trip, LiveData<Integer>>() {
            @Override
            public LiveData<Integer> apply(Trip trip) {
                return trip == null ? null : database.packetDao().getCount(trip.getId());
            }
        });
    }

    public LiveData<Trip> getTrip() {
        return trip;
    }

    public void setTrip (Trip trip) {
        this.trip.postValue(trip);
    }


    public LiveData<Packet> getLatest() {
        return latestPacket;
    }

    public LiveData<Integer> getPacketCount() {
        return packetCount;
    }

    public void fetchPackets (Trip trip, FetchPacketCallback callback) {
        new FetchAsyncTask(database, callback).execute(trip);
    }

    public interface FetchPacketCallback {
        void onFetch(List<Packet> packets);
    }

    /**
     *
     */
    private static class FetchAsyncTask extends AsyncTask<Trip, Void, List<Packet>> {

        private AppDatabase mmDatabase;
        private FetchPacketCallback mmCallback;

        FetchAsyncTask(AppDatabase database, FetchPacketCallback callback) {
            this.mmDatabase = database;
            this.mmCallback = callback;
        }

        @Override
        protected List<Packet> doInBackground(Trip... params) {
            List<Packet> items = null;
            if (params.length > 0 && params[0] != null) {
                items = mmDatabase.packetDao().getPackets(params[0].getId());
            }
            return items;
        }

        @Override
        protected void onPostExecute(List<Packet> packets) {
            if (mmCallback != null) {
                mmCallback.onFetch(packets);
            }
        }

    }





}
