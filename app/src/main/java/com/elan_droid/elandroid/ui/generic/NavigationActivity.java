package com.elan_droid.elandroid.ui.generic;

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
import com.elan_droid.elandroid.adapter.ProfileAdapter;
import com.elan_droid.elandroid.database.relation.Profile;
import com.elan_droid.elandroid.ui.dashboard.DashboardFragment;
import com.elan_droid.elandroid.ui.profile.ProfileActivity;
import com.elan_droid.elandroid.ui.dialog.ProfileSelectDialog;
import com.elan_droid.elandroid.ui.trip_log.TripList;

/**
 * Created by Peter Smith
 *
 * A @link{NavigationActivity}
 */
public abstract class NavigationActivity extends BaseActivity implements
            NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ProfileAdapter.OnProfileSelectedListener {

    private static final String NAVIGATION_ITEM_ID = "com.elan_droid.elandroid.ui.generic.NAVIGATION_ITEM_ID";
    private static final String PROFILE_ID = "com.elan_droid.elandroid.ui.generic.PROFILE_ID";

    public static final String TAG = "NavigationActivity";

    // The main navigation UI elements
    protected NavigationView mNavView;
    private DrawerLayout mNavDrawer;
    protected ActionBarDrawerToggle mDrawerToggle;

    // The navigation header UI elements
    private TextView mNameText;
    private TextView mMakeModel;
    private TextView mRegistration;

    private int mSelectedNavItemId;
    protected long mProfileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Log.d(TAG, "onCreate(savedInstanceState) called");


        Toolbar toolbar = initToolbar();
        setupToolbar(toolbar, false);

        // Setup the main navigation header
        mNavDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

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

        mNavView = (NavigationView) findViewById(R.id.nav_view);
        mNavView.setNavigationItemSelectedListener(this);

        // Setup the navigation header
        View header = mNavView.getHeaderView(0);
        header.setOnClickListener(this);

        mNameText = (TextView) header.findViewById(R.id.nav_profile_name);
        mMakeModel = (TextView) header.findViewById(R.id.nav_profile_make_model);
        mRegistration = (TextView) header.findViewById(R.id.nav_profile_registration);

        if (savedInstanceState == null) {
            transitionFragmentWithFallback(R.id.nav_dashboard, true);
        }
        else {
            mNavView.setCheckedItem(mSelectedNavItemId);
        }

    }

    @Override
    public void onChanged(@Nullable Profile profile) {
        super.onChanged(profile);

        if (profile != null) {
            updateHeader(profile);
            transitionFragmentWithFallback(false);
        }
        else {
            forceNewProfile();
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
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onProfileSelected(Profile profile) {
        getProfileModel().setActiveProfile(profile);
    }

    /**
     * Populates the navigation header with profile information
     * @param profile   the profile to display
     */
    private void updateHeader(@NonNull Profile profile) {
        mNameText.setText(profile.getName());
        mMakeModel.setText(profile.getMakeModel());
        mRegistration.setText(profile.getRegistration());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNavDrawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return transitionFragment(item);
    }


    private boolean transitionFragmentWithFallback(boolean closeDrawer) {
        boolean result = transitionFragmentWithFallback(mSelectedNavItemId, closeDrawer);

        if (!result)
            result = transitionFragmentWithFallback(mNavView.getMenu().getItem(0), closeDrawer);

        return result;
    }

    private boolean transitionFragmentWithFallback(@IdRes int navigationId, boolean closeDrawer) {
        return transitionFragment(mNavView.getMenu().findItem(navigationId), closeDrawer);
    }

    private boolean transitionFragmentWithFallback(MenuItem item, boolean closeDrawer) {
        boolean result = transitionFragment(item, closeDrawer);

        if (!result) {
            forceNewProfile();
        }
        return result;
    }

    private boolean transitionFragment(MenuItem item) {
        return transitionFragment(item, true);
    }

    private boolean transitionFragment(MenuItem item, boolean closeDrawer) {
        // If no current active profile set then force a new one
        if (getActiveProfileId() == 0) {
            return false;
        }

        final Fragment fragment = createFragment(item.getItemId());

        if (fragment != null) {
            item.setChecked(true);
            mSelectedNavItemId = item.getItemId();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_base, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();

            if (closeDrawer)
                mNavDrawer.closeDrawer(GravityCompat.START);
        }

        return fragment != null;
    }

    private Fragment createFragment(@IdRes int navItemId) {
        Fragment fragment = null;

        switch (navItemId) {
            case R.id.nav_dashboard:
                fragment = DashboardFragment.getInstance(mProfileId);
                break;

            case R.id.nav_trip_log:
                fragment = TripList.getInstance(mProfileId);
                break;

            case R.id.nav_statistics:

                break;

            case R.id.nav_settings:

                break;
        }

        return fragment;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAVIGATION_ITEM_ID, mSelectedNavItemId);
        outState.putLong(PROFILE_ID, mProfileId);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mSelectedNavItemId = savedInstanceState.getInt(NAVIGATION_ITEM_ID);
        this.mProfileId = savedInstanceState.getLong(PROFILE_ID);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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


}
