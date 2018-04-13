package com.elan_droid.elandroid.ui.generic;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.ui.callback.UpdateDialogListener;

public abstract class BaseActivity extends AppCompatActivity implements UpdateDialogListener {

    private static final String TAG = "BaseActivity";

    // Dialog request codes
    public static final int NEW_PAGE_REQUEST_CODE = 0;
    public static final int NEW_PAGE_ITEM_REQUEST_CODE = 1;
    public static final int MANAGE_PROFILE_REQUEST_CODE = 3;
    //public static final int EDIT_PAGE_REQUEST_CODE = 1;

    private static final String DIALOG_FRAGMENT_TAG = "com.elan_droid.elandroid.ui.generic.DIALOG_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(savedInstanceState) called");
        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
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

}
