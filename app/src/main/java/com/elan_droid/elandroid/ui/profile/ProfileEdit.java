package com.elan_droid.elandroid.ui.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.relation.Profile;
import com.elan_droid.elandroid.database.view_model.ActiveProfile;

import java.util.ArrayList;

/**
 * Created by Peter Smith
 */

public class ProfileEdit extends Fragment implements ActiveProfile.FetchActiveProfileCallback {

    public static final String TAG = "ProfileEdit";

    // Database View Models
    private ActiveProfile mProfileModel;
    //private UserVehicleModel mUVModel;

    // Spinner Adapters
    ArrayAdapter<String> mMakeAdapter, mModelAdapter;
    AdapterView.OnItemSelectedListener mMakeListener;

    // UI variables
    private EditText mNameEditText;
    private Spinner mMakeSpinner;
    private Spinner mModelSpinner;
    private EditText mRegEditText;
    private CheckBox mActiveCheck;

    // Variables
    private long mUserVehicleId;
    private boolean mUpdated;
    private Profile mCurrProfile;
    private int mAction;
    private boolean mDisableMakeModel;



    public static Fragment getInstance(long uvId) {
        Bundle args = new Bundle();
        args.putLong(ProfileActivity.EXTRA_USER_VEHICLE_ID, uvId);

        Fragment fragment = new ProfileEdit();
        fragment.setArguments(args);
        return fragment;
    }

    private void handleBundle(Bundle bundle) {
        mUserVehicleId = bundle.getLong(ProfileActivity.EXTRA_USER_VEHICLE_ID, 0);
    }

    private boolean parseAndSave() {
        String name = mNameEditText.getText().toString();
        if (name.equals("")) {
            mNameEditText.requestFocus();
            Toast.makeText(getContext(), getString(R.string.edit_profile_error_name_missing), Toast.LENGTH_SHORT).show();
            return false;
        }
        String reg = mRegEditText.getText().toString();
        boolean active = mActiveCheck.isChecked();

        mCurrProfile.setName(name);
        mCurrProfile.setRegistration(reg);

        // Update the profile for the  current profile
        mProfileModel.updateProfile(mCurrProfile, active, new ActiveProfile.UpdateProfileCallback() {
            @Override
            public void onProfileUpdated(boolean success) {
                if (success) {
                    Toast.makeText(getContext(), R.string.edit_profile_toast_updated_profile_success, Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                else {
                    Toast.makeText(getContext(), R.string.edit_profile_toast_updated_profile_fail, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return true;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile_save:
                parseAndSave();
                return true;

            case R.id.menu_profile_delete:
                deleteProfile();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private AlertDialog deleteProfile() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
        .setMessage("Are you sure you want to delete this profile?")
        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mProfileModel.deleteProfile(mCurrProfile, new ActiveProfile.DeleteProfileCallback() {
                    @Override
                    public void onProfileDeleted(boolean success) {
                        if (success) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                        else {
                            Toast.makeText(getActivity(), "The profile deletion was unsuccessful!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setBackgroundResource(R.drawable.ic_delete_white_24dp);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            handleBundle (savedInstanceState);
        }
        else {
            handleBundle (getArguments());
        }

        mUpdated = false;
        mProfileModel = ViewModelProviders.of(this).get(ActiveProfile.class);

        // If not equal to 0, then we can try fetch the profile
        if (mUserVehicleId != 0) {
            mProfileModel.fetchProfile(mUserVehicleId, this);
        }
        // Otherwise transition back with an error
        else {
            transitionError();
        }

    }

    public void transitionError() {
        getActivity().getSupportFragmentManager().popBackStack();
        Toast.makeText(getContext(), R.string.edit_profile_error_profile_missing, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFetch(Profile profile) {
        // We return an error to the user and pop back the stack
        if (profile == null) {
            transitionError();
        }

        else {
            this.mCurrProfile = profile;
            updateUI(mCurrProfile);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        /** Fetching the UI elements **/
        mNameEditText = (EditText) view.findViewById(R.id.edit_profile_name_edit);
        mMakeSpinner = (Spinner) view.findViewById(R.id.edit_profile_make_spinner);
        mModelSpinner = (Spinner) view.findViewById(R.id.edit_profile_model_spinner);
        mRegEditText = (EditText) view.findViewById(R.id.edit_profile_reg_edit);
        mActiveCheck = (CheckBox) view.findViewById(R.id.edit_profile_active_check);

        updateUI(mCurrProfile);

        return view;
    }

    private void updateUI (Profile profile) {
        if (!mUpdated && profile != null &&
                mNameEditText != null &&
                mMakeSpinner != null && mModelSpinner != null) {
            String name = profile.getUser().getName();
            mNameEditText.setText(name);
            getActivity().setTitle(getString(R.string.edit_profile_title_preamble) + " " + name);

            ArrayAdapter<String> makeAdapter = new ArrayAdapter<String>
                    (getContext(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>());
            makeAdapter.add(profile.getVehicle().getMake());
            mMakeSpinner.setAdapter(makeAdapter);
            mMakeSpinner.setEnabled(false);

            ArrayAdapter<String> modelAdapter = new ArrayAdapter<String>
                    (getContext(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>());
            modelAdapter.add(profile.getVehicle().getModel());
            mModelSpinner.setAdapter(modelAdapter);
            mModelSpinner.setEnabled(false);

            mRegEditText.setText(profile.getUser().getRegistration());

            mActiveCheck.setChecked(profile.isActive());

            this.mUpdated = true;
        }
    }
}
