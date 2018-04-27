package com.elan_droid.elandroid.database.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.entity.Vehicle;

import java.util.List;

/**
 * Created by Peter Smith
 */

public class VehicleModel extends AndroidViewModel {

    private AppDatabase mDatabase;
    private LiveData<List<String>> mManufacturers;

    public VehicleModel (Application application) {
        super (application);

        this.mDatabase = AppDatabase.getInstance(application);
        this.mManufacturers = mDatabase.vehicleDao().getManufacturers();
    }

    /**
     * All the manufacturers that is currently supported by the application
     * @return      supported manufacturers
     */
    public LiveData<List<String>> getManufacturers() {
        return mManufacturers;
    }

    /**
     * Fetch's all the supported vehicle models for a given manufactuer
     * TODO:    change adapter to some interface callback
     * @param manufacturer  the manufactuer
     * @param adapter       the adapter to populate
     */
    public void getModels (String manufacturer, ArrayAdapter<String> adapter) {
        new FetchModelAsyncTask(mDatabase, adapter).execute(manufacturer);
    }



    public void insert (Vehicle... vehicle) {
        new PopulateAsyncTask(mDatabase).execute(vehicle);
    }



    /**
     * This AsyncTask will populate the database with all the given Vehicle entities
     */
    private static class PopulateAsyncTask extends AsyncTask<Vehicle, Void, Void> {

        private AppDatabase mmDatabase;

        PopulateAsyncTask(AppDatabase database) {
            this.mmDatabase = database;
        }

        @Override
        protected Void doInBackground(Vehicle... params) {
            if(params.length > 0) {
                mmDatabase.vehicleDao().insertAll(params);
            }
            return null;
        }
    }

    /**
     * This AsyncTask will fetch a list of compatible vehicle models for a given manufacturer.
     * It query a vehicle model given to it and populate a given adapter.
     */
    private static class FetchModelAsyncTask extends AsyncTask<String, Void, List<String>> {

        private AppDatabase mmDatabase;
        private ArrayAdapter<String> mmAdapter;

        /**
         * Prevent default constructor
         */
        private FetchModelAsyncTask () {}

        /**
         * Provides the task with the given parameters needed.
         * @param database  the database getInstance
         * @param adapter   the model adapter to populate
         */
        protected FetchModelAsyncTask (AppDatabase database, ArrayAdapter<String> adapter) {
            this.mmDatabase = database;
            this.mmAdapter = adapter;
        }

        @Override
        protected List<String> doInBackground(String... params) {
            List<String> result = null;
            if (params.length > 0 && params[0] != null) {
                result = mmDatabase.vehicleDao().getModels(params[0]);
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            mmAdapter.clear();
            mmAdapter.addAll(result);
            mmAdapter.notifyDataSetChanged();
        }
    }
}
