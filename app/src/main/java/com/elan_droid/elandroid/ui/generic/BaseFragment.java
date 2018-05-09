package com.elan_droid.elandroid.ui.generic;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.elan_droid.elandroid.database.data.relation.Profile;
import com.elan_droid.elandroid.database.view_model.ActiveProfile;

/**
 * Created by Peter Smith
 *
 *
 */
public abstract class BaseFragment extends Fragment implements UpdateDialogListener {

    private static final String TAG = "BaseFragment";
    public static final String EXTRA_PROFILE_ID = "com.elan_droid.elandroid.generic.EXTRA_PROFILE_ID";

    private UpdateDialogListener mDialogCallback;

    private ActiveProfile mProfileModel;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(savedInstanceState) called");

        mProfileModel = ViewModelProviders.of(getActivity()).get(ActiveProfile.class);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mDialogCallback = (UpdateDialogListener) getActivity();
        }
        catch (ClassCastException e) {

        }
    }

    @Override
    public void displayDialog (final DialogFragment fragment) {
        mDialogCallback.displayDialog(fragment);
    }

    /**
     *
     * TODO: can't we get the profile id the method?
     * @param userId
     * @return
     */
    public static Bundle createArguments(long userId) {
        Bundle args  = new Bundle();
        args.putLong(EXTRA_PROFILE_ID, userId);
        return args;
    }

    /**
     *
     * @return
     */
    public ActiveProfile getProfileModel() {
        return mProfileModel;
    }

    /**
     *
     * @return
     */
    public Profile getActiveProfile() {
        return mProfileModel.getActiveProfile().getValue();
    }

    public long getActiveProfileId() {
        return getActiveProfile() == null ? 0 : getActiveProfile().getProfileId();
    }

    public void setTitle(@StringRes int titleId) {
        setTitle (getString(titleId));
    }

    public void setTitle(String title) {
        getActivity().setTitle(title);
    }
}
