package com.elan_droid.elandroid.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.data.relation.Profile;
import com.elan_droid.elandroid.database.view_model.ActiveProfile;
import com.elan_droid.elandroid.ui.generic.UpdateDialogListener;
import com.elan_droid.elandroid.ui.profile.ProfileActivity;

/**
 * Created by Peter Smith
 *
 * The responsibility of the BaseActivity, is to handle all activities used application wide.
 * Ensures a consistent implementation over a number of activities.
 *      => ActiveProfile
 *      => Toolbar
 *      => Dialog support
 */
public abstract class BaseActivity extends AppCompatActivity implements UpdateDialogListener, Observer<Profile> {

    private static final String TAG = "BaseActivity";

    private ActiveProfile mProfileModel;

    private Profile mActiveProfile;

    // Dialog executeRequest codes
    public static final int NEW_PAGE_REQUEST_CODE = 0;
    public static final int NEW_PAGE_ITEM_REQUEST_CODE = 1;
    public static final int MANAGE_PROFILE_REQUEST_CODE = 3;
    //public static final int SAVE_TRIP_REQUEST_CODE = 4;
    //public static final int EDIT_PAGE_REQUEST_CODE = 1;

    private static final String DIALOG_FRAGMENT_TAG = "com.elan_droid.elandroid.generic.DIALOG_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(savedInstanceState) called");
        setContentView(R.layout.activity_base);

        // Retrieves the active profile view model
        mProfileModel = ViewModelProviders.of(this).get(ActiveProfile.class);
        mProfileModel.getActiveProfile().observe(this, this);

        // Finds the toolbar and sets support action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Ensures setup before setting title and home enabled
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * Active profile listener
     * @param profile   new active profile
     */
    @Override
    public void onChanged(@Nullable Profile profile) {
        this.mActiveProfile = profile;
    }

    /**
     * Starts activity to the ProfileActivity with ACTION_FORCE flag.
     * Called when no profiles have been found (app startup)
     */
    protected void forceNewProfile () {
        Intent intent = ProfileActivity.getIntent(this, ProfileActivity.ACTION_FORCE);
        startActivity(intent);
        finish();
    }

    /**
     * Get's the profile model
     * @return  the profile model
     */
    public ActiveProfile getProfileModel() {
        return mProfileModel;
    }

    /**
     * Get's the current active profile
     * @return  the active profile
     */
    public Profile getActiveProfile() {
        return mActiveProfile;
    }

    /**
     * Get's the current active profile's id
     * @return 0 if no active profile, otherwise ID
     */
    public long getActiveProfileId() {
        return mProfileModel.getActiveProfileId();
    }

    /**
     * Finds the toolbar
     * @return  the toolbar
     */
    @Nullable
    public Toolbar getToolbar() {
        Log.d(TAG, "setupToolbar");
        return (Toolbar) findViewById(R.id.toolbar);
    }

    /**
     * Setup a given toolbar
     * @param toolbar   the new toolbar
     * @param upButton  up button active?
     */
    public void setupToolbar(Toolbar toolbar, boolean upButton) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            //actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(upButton);
        }
    }

    /**
     * Displays a dialog fragment, callback from UpdateDialogListener
     * @param fragment  an instance of DialogFragment
     */
    @Override
    public void displayDialog(final DialogFragment fragment) {
        Fragment previous = getSupportFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG);

        if (previous != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(previous).commit();
        }

        fragment.show(this.getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
    }

    /**
     * Dismisses the current dialog fragment
     */
    public void dismissDialog () {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG);

        if (fragment != null) {
            ((DialogFragment) fragment).dismiss();
        }
    }

}
