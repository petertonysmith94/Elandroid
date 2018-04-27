package com.elan_droid.elandroid.ui.generic;

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
import com.elan_droid.elandroid.database.relation.Profile;
import com.elan_droid.elandroid.database.view_model.ActiveProfile;
import com.elan_droid.elandroid.ui.callback.UpdateDialogListener;
import com.elan_droid.elandroid.ui.profile.ProfileActivity;

public abstract class BaseActivity extends AppCompatActivity implements UpdateDialogListener, Observer<Profile> {

    private static final String TAG = "BaseActivity";

    private ActiveProfile mProfileModel;

    private Profile mActiveProfile;

    // Dialog executeRequest codes
    public static final int NEW_PAGE_REQUEST_CODE = 0;
    public static final int NEW_PAGE_ITEM_REQUEST_CODE = 1;
    public static final int MANAGE_PROFILE_REQUEST_CODE = 3;
    public static final int SAVE_TRIP_REQUEST_CODE = 4;
    //public static final int EDIT_PAGE_REQUEST_CODE = 1;

    private static final String DIALOG_FRAGMENT_TAG = "com.elan_droid.elandroid.ui.generic.DIALOG_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(savedInstanceState) called");
        setContentView(R.layout.activity_base);

        //
        mProfileModel = ViewModelProviders.of(this).get(ActiveProfile.class);
        mProfileModel.getActiveProfile().observe(this, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void onChanged(@Nullable Profile profile) {
        this.mActiveProfile = profile;
    }

    protected void forceNewProfile () {
        Intent intent = ProfileActivity.getIntent(this, ProfileActivity.ACTION_FORCE);
        startActivity(intent);
        finish();
    }

    public ActiveProfile getProfileModel() {
        return mProfileModel;
    }

    public Profile getActiveProfile() {
        return mActiveProfile;
    }

    public long getActiveProfileId() {
        return mActiveProfile == null ? 0 : mActiveProfile.getProfileId();
    }

    public Toolbar initToolbar () {
        Log.d(TAG, "setupToolbar");
        return (Toolbar) findViewById(R.id.toolbar);
    }

    public void setupToolbar(Toolbar toolbar, boolean upButton) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            //actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(upButton);
        }
    }

    @Override
    public void displayDialog(final DialogFragment fragment) {
        Fragment previous = getSupportFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG);

        if (previous != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(previous).commit();
        }

        fragment.show(this.getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
    }

    public void dismissDialog () {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG);

        if (fragment != null) {
            ((DialogFragment) fragment).dismiss();
        }
    }

}
