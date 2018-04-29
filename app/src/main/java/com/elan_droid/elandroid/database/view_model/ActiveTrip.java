package com.elan_droid.elandroid.database.view_model;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.entity.Flag;
import com.elan_droid.elandroid.database.entity.Packet;
import com.elan_droid.elandroid.database.entity.Trip;

import java.util.List;

/**
 * Created by Peter Smith on 4/22/2018.
 */

public class ActiveTrip extends AndroidViewModel {

    private AppDatabase database;
    private PacketModel packetModel;

    private final MutableLiveData<Trip> trip;
    private final LiveData<Packet> latestPacket;
    private final MediatorLiveData<List<Flag>> latestFlags;

    private final LiveData<Integer> packetCount;

    public ActiveTrip (@NonNull final Application application) {
        super (application);
        packetModel = new PacketModel(application);

        this.database = AppDatabase.getInstance(application);
        this.trip = new MutableLiveData<>();
        this.latestPacket = Transformations.switchMap(trip, new Function<Trip, LiveData<Packet>>() {
            @Override
            public LiveData<Packet> apply(Trip input) {
                return database.packetDao().getLatest(input.getId());
            }
        });
        this.latestFlags = new MediatorLiveData<>();
        latestFlags.addSource(latestPacket, new Observer<Packet>() {
            @Override
            public void onChanged(@Nullable Packet packet) {
                if (packet != null) {
                    new FlagModel.FetchFlagsTask(database, new FlagModel.OnFetchFlagListener() {
                        @Override
                        public void onFetchFlags(List<Flag> flags) {
                            latestFlags.postValue(flags);
                        }
                    }).execute(packet.getId());
                }
            }
        });

        this.packetCount = Transformations.switchMap(trip, new Function<Trip, LiveData<Integer>>() {
            @Override
            public LiveData<Integer> apply(Trip trip) {
                return trip == null ? null : database.packetDao().getCount(trip.getId());
            }
        });
    }


    public LiveData<Trip> getActiveTrip() {
        return trip;
    }

    public void setTrip (Trip trip) {
        this.trip.postValue(trip);
    }


    public LiveData<List<Flag>> getLatestFlags() {
        return latestFlags;
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
