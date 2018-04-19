package com.elan_droid.elandroid.database.view;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.entity.Parameter;
import com.elan_droid.elandroid.database.relation.Profile;

import java.util.List;

/**
 * Created by Peter Smith
 */

public class ParameterModel extends AndroidViewModel {


    private AppDatabase mDatabase;

    public ParameterModel(@NonNull Application application) {
        super(application);

        this.mDatabase = AppDatabase.getInstance(application);
    }

    public void fetchParameters (long messageId, FetchParameterCallback callback) {
        new FetchAsyncTask(mDatabase, callback).execute(messageId);
    }

    public interface FetchParameterCallback {
        void onFetch (List<Parameter> parameters);
    }

    /**
     *
     */
    private static class FetchAsyncTask extends AsyncTask<Long, Void, List<Parameter>> {

        private AppDatabase mmDatabase;
        private FetchParameterCallback mmCallback;

        FetchAsyncTask(AppDatabase database, FetchParameterCallback callback) {
            this.mmDatabase = database;
            this.mmCallback = callback;
        }

        @Override
        protected List<Parameter> doInBackground(Long... params) {
            List<Parameter> parameters = null;
            if(params.length > 0 && params[0] != 0) {
                parameters = mmDatabase.parameterDao().fetch(params[0]);
            }
            return parameters;
        }

        @Override
        protected void onPostExecute(List<Parameter> parameters) {
            if (mmCallback != null) {
                mmCallback.onFetch(parameters);
            }
        }
    }

}
