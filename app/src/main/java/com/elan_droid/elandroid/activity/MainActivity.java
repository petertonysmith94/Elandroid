package com.elan_droid.elandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Peter Smith
 *
 *
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
