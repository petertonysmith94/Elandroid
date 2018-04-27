package com.elan_droid.elandroid.database.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.relation.Command;

/**
 * Created by Peter Smith on 4/24/2018.
 **/

public class CommandModel extends AndroidViewModel {

    public CommandModel(@NonNull Application application) {
        super(application);
    }


    public interface FetchCommandCallback {
        void onFetchCommand (Command command);
    }


    public static AsyncTask<Long, Void, Command> getFetchTask
            (AppDatabase database, FetchCommandCallback callback) {
        return new FetchCommandTask(database, callback);
    }

    /**
     *
     */
    public static class FetchCommandTask extends AsyncTask<Long, Void, Command> {

        private AppDatabase mmDatabase;
        private FetchCommandCallback mmCallback;

        public FetchCommandTask(@NonNull AppDatabase database, @NonNull FetchCommandCallback callback) {
            this.mmDatabase = database;
            this.mmCallback = callback;
        }

        @Override
        protected Command doInBackground(Long... params) {
            Command command = null;
            if (params.length > 0 && params[0] != null) {
                command = mmDatabase.commandDao().getCommand(params[0]);
            }
            return command;
        }

        @Override
        protected void onPostExecute(Command command) {
            mmCallback.onFetchCommand(command);
        }

    }
}
