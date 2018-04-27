package com.elan_droid.elandroid.database.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.entity.Packet;

/**
 * Created by Peter Smith on 4/22/2018.
 */

public class PacketModel extends AndroidViewModel {

    private AppDatabase mDatabase;

    public PacketModel (Application application) {
        super (application);
        mDatabase = AppDatabase.getInstance(application);
    }




    public void createPacket (final Packet packet, final InsertPacketCallback callback) {
        new PopulateAsyncTask(mDatabase, new InsertPacketCallback() {
            @Override
            public void onInsert(Packet packet) {
                if (callback != null) {
                    callback.onInsert(packet);
                }
            }
        }).execute(packet);
    }

    public interface InsertPacketCallback {
        void onInsert(Packet packet);
    }

    private static class PopulateAsyncTask extends AsyncTask<Packet, Void, Packet> {
        private AppDatabase mmDatabase;
        private InsertPacketCallback mmCallback;

        PopulateAsyncTask(@NonNull AppDatabase database, @NonNull InsertPacketCallback callback) {
            this.mmDatabase = database;
            this.mmCallback = callback;
        }

        @Override
        protected Packet doInBackground(Packet... params) {
            Packet result = (params.length > 0) ? params[0] : null;

            if (result != null) {
                long id = mmDatabase.packetDao().insert(result);

                if (id != 0) {
                    result.setId(id);
                }
                else
                    result = null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Packet result) {
            mmCallback.onInsert(result);
        }
    }

}
