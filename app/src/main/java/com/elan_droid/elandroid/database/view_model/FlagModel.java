package com.elan_droid.elandroid.database.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.entity.Flag;

import java.util.List;

/**
 * Created by Peter Smith on 4/24/2018.
 **/

public class FlagModel extends AndroidViewModel {


    public FlagModel(@NonNull Application application) {
        super(application);

    }

    public interface OnFetchFlagListener {
        void onFetchFlags (List<Flag> flags);
    }

    /**
     *
     */
    public static class FetchFlagsTask extends AsyncTask<Long, Void, List<Flag>> {

        private AppDatabase mmDatabase;
        private OnFetchFlagListener mmListener;

        FetchFlagsTask(@NonNull AppDatabase database, @NonNull OnFetchFlagListener listener) {
            this.mmDatabase = database;
            this.mmListener = listener;
        }

        @Override
        protected List<Flag> doInBackground(Long... params) {
            List<Flag> flags = null;
            if(params.length > 0 && params[0] != 0) {
                flags = mmDatabase.flagDao().fetchFlags(params[0]);
            }
            return flags;
        }

        @Override
        protected void onPostExecute(List<Flag> flags) {
            mmListener.onFetchFlags(flags);
        }
    }
}
