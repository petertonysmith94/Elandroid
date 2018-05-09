package com.elan_droid.elandroid.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.ui.profile.ProfileAdapter;
import com.elan_droid.elandroid.database.data.relation.Profile;
import com.elan_droid.elandroid.ui.dashboard.DashboardFragment;
import com.elan_droid.elandroid.ui.profile.ProfileActivity;
import com.elan_droid.elandroid.ui.profile.ProfileSelectDialog;
import com.elan_droid.elandroid.ui.trip_log.TripLogFragment;

/**
 * Created by Peter Smith
 *
 * The responsibility of the NavigationActivity, is to handle the navigational elements.
 *      => Navigation Slider
 *      => Navigation Header population
 *      => Managing profile
 */
public abstract class NavigationActivity extends BaseActivity implements
            NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ProfileAdapter.OnProfileSelectedListener {

    private static final String SAVED_NAVIGATION_ITEM_ID = "com.elan_droid.elandroid.activity.SAVED_NAVIGATION_ITEM_ID";
    private static final String SAVED_PROFILE_ID = "com.elan_droid.elandroid.activity.SAVED_PROFILE_ID";

    public static final String TAG = "NavigationActivity";

    // The main navigation UI elements
    private NavigationView mNavView;
    private DrawerLayout mNavDrawer;
    private ActionBarDrawerToggle mDrawerToggle;

    // The navigation header UI elements
    private TextView mProfileName;
    private TextView mProfileMakeModel;
    private TextView mProfileRegistration;

    // User variables
    private int mSelectedNavItemId;
    private long mProfileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Log.d(TAG, "onCreate(savedInstanceState) called");

        // Set up the toolbar
        Toolbar toolbar = getToolbar();
        setupToolbar(toolbar, false);

        setupNavigation(toolbar);

        if (savedInstanceState == null) {
            transitionWithFallback(R.id.nav_dashboard, false);
        }
        else {
            mNavView.setCheckedItem(mSelectedNavItemId);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mNavDrawer.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_NAVIGATION_ITEM_ID, mSelectedNavItemId);
        outState.putLong(SAVED_PROFILE_ID, mProfileId);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mSelectedNavItemId = savedInstanceState.getInt(SAVED_NAVIGATION_ITEM_ID);
        this.mProfileId = savedInstanceState.getLong(SAVED_PROFILE_ID);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MANAGE_PROFILE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     *
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return transitionFragment(item, true);
    }

    /**
     *  Header click listener
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_header_container:
                // Launches the profile selection dialogs
                DialogFragment fragment = ProfileSelectDialog.getInstance();
                displayDialog(fragment);
                break;

        }
    }

    /**
     * Updates the profile model when a profile has been selected
     * @param profile   the profile selected
     */
    @Override
    public void onProfileSelected(Profile profile) {
        getProfileModel().setActiveProfile(profile);
        dismissDialog();
    }


    /**
     * Overridden from BaseActivity
     * @param profile   the new profile selected
     */
    @Override
    public void onChanged(@Nullable Profile profile) {
        super.onChanged(profile);

        if (profile != null) {
            updateHeader(profile);
            transitionWithFallback(mSelectedNavItemId, false);
        }
        else {
            forceNewProfile();
        }
    }



    /**
     * Setup the navigation UI elements
     * @param toolbar
     */
    private void setupNavigation(Toolbar toolbar) {
        // Setup the main navigation features: drawer, toolbar toggle
        mNavDrawer = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mNavDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerToggle.syncState();
        mNavDrawer.addDrawerListener(mDrawerToggle);

        // Sets the navigation view listener
        mNavView = findViewById(R.id.nav_view);
        mNavView.setNavigationItemSelectedListener(this);

        // Setup the navigation header
        View header = mNavView.getHeaderView(0);
        header.setOnClickListener(this);
        mProfileName = header.findViewById(R.id.nav_profile_name);
        mProfileMakeModel = header.findViewById(R.id.nav_profile_make_model);
        mProfileRegistration = header.findViewById(R.id.nav_profile_registration);
    }

    /**
     * Updates the navigation header with profile information
     * @param profile   the profile to display
     */
    private void updateHeader(@NonNull Profile profile) {
        mProfileName.setText(profile.getName());
        mProfileMakeModel.setText(profile.getMakeModel());
        mProfileRegistration.setText(profile.getRegistration());
    }

    /**
     *
     * @param navigationId
     * @param closeDrawer
     * @return
     */
    private boolean transitionWithFallback(@IdRes int navigationId, boolean closeDrawer) {
        return transitionWithFallback(mNavView.getMenu().findItem(navigationId), closeDrawer);
    }

    /**
     * Transition to fragment, otherwise
     * @param item
     * @param closeDrawer
     * @return
     */
    private boolean transitionWithFallback(@Nullable MenuItem item, boolean closeDrawer) {
        // Force new profile if not set
        if (getActiveProfileId() == 0) {
            forceNewProfile();
        }
        // Selects the top element if no item
        else if (item == null) {
            item = mNavView.getMenu().getItem(0);
        }
        // Couldn't transition, force new profile
        else if (!transitionFragment(item, closeDrawer)) {
            Log.e(TAG, "Failed to transition navigation fragment");
        }
        return true;
    }

    /**
     * Transitions the content_base
     * @param item
     * @param closeDrawer
     * @return
     */
    private boolean transitionFragment(MenuItem item, boolean closeDrawer) {
        final Fragment fragment = createFragment(item.getItemId());

        // Ensures fragment creation, transitions to fragment
        if (fragment != null) {
            item.setChecked(true);
            mSelectedNavItemId = item.getItemId();

            // TODO: move to BaseActivity?
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_base, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();

            if (closeDrawer) {
                mNavDrawer.closeDrawer(GravityCompat.START);
            }
            return true;
        }
        return false;
    }

    /**
     * Creates the navigation fragments from their ids
     * @param navigationId the navigation item id
     * @return  a navigational fragment
     */
    private Fragment createFragment(@IdRes int navigationId) {
        Fragment fragment = null;

        switch (navigationId) {
            case R.id.nav_dashboard:
                fragment = DashboardFragment.getInstance(mProfileId);
                break;

            case R.id.nav_trip_log:
                fragment = TripLogFragment.getInstance(mProfileId);
                break;
        }

        return fragment;
    }


}
