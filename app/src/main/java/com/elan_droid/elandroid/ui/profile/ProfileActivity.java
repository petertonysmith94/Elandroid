package com.elan_droid.elandroid.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.ui.generic.BaseActivity;

/**
 * Created by Peter Smith
 */

public class ProfileActivity extends BaseActivity {

    public static final String TAG = "ProfileActivity";

    //
    public static final String EXTRA_ACTION =
            "com.elan_droid.elandroid.ui.profile.ProfileActivity.EXTRA_ACTION";
    public static final String EXTRA_USER_VEHICLE_ID =
            "com.elan_droid.elandroid.ui.profile.ProfileActivity.EXTRA_USER_VEHICLE_ID";

    //
    public static final int ACTION_MANAGE = 0;
    public static final int ACTION_FORCE = 1;
    public static final int ACTION_NEW = 2;
    public static final int ACTION_EDIT = 3;

    private int mAction;

    public static Intent getIntent(Context context, int action) {
        Intent intent = new Intent (context, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_ACTION, action);
        return intent;
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(savedInstanceState) called");

        setupToolbar(true);

        Bundle bundle = getIntent().getExtras();
        if (savedInstanceState != null) {
            bundle = savedInstanceState;
        }
        handleExtras (bundle);


        handleAction ();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void handleExtras (Bundle bundle) {
        if (bundle != null) {
            mAction = bundle.getInt(EXTRA_ACTION, ACTION_MANAGE);
        }
    }



    public void handleAction () {
        FragmentTransaction transition = null;

        switch (mAction) {
            case ACTION_MANAGE:
                transition = getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_base, ProfileManage.getInstance());
                break;

            case ACTION_FORCE:
                transition = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_base, ProfileNew.getInstanceForce());
                break;

            case ACTION_NEW:
                transition = getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_base, ProfileNew.getInstanceNew());
                break;

            case ACTION_EDIT:

                break;
        }

        if (transition != null) {
            transition.commit();
        }
    }

}
