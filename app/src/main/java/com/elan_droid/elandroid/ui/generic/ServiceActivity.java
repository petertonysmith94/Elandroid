package com.elan_droid.elandroid.ui.generic;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.entity.Trip;
import com.elan_droid.elandroid.database.view.TripModel;

/**
 * Created by Peter Smith
 */

public abstract class ServiceActivity extends NavigationActivity {

    private static final String TAG = "ServiceActivity";

    private TripModel mTripModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTripModel = ViewModelProviders.of(this).get(TripModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.service_connect, menu);
        Log.d(TAG, "onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                mTripModel.newTrip(new Trip(mProfileId, "Temp"), new TripModel.InsertTripCallback() {
                    @Override
                    public void onTripInserted(Trip trip) {

                    }
                });
                Toast.makeText(getApplicationContext(), "Connecting", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
