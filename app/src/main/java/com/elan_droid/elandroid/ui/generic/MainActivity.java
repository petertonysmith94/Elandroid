package com.elan_droid.elandroid.ui.generic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.ui.profile.ProfileActivity;

/**
 * Created by Peter Smith
 */

public class MainActivity extends ServiceActivity {

    private static final String TAG = "MainActivity";

    public static Intent getIntent(Context context) {
        Intent intent = new Intent (context, MainActivity.class);
        return intent;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mDrawerToggle.syncState();
    }








}
