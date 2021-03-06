package com.elan_droid.elandroid.ui.profile;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.elan_droid.elandroid.database.data.relation.Profile;
import com.elan_droid.elandroid.database.view_model.ActiveProfile;
import com.elan_droid.elandroid.database.view_model.VehicleModel;
import com.elan_droid.elandroid.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static com.elan_droid.elandroid.ui.profile.ProfileActivity.ACTION_FORCE;

/**
 * Created by Peter Smith
 */

public class ProfileNew extends Fragment {

    public static final String TAG = "ProfileNew";

    // Database View Models
    private VehicleModel mVModel;
    private ActiveProfile mActiveProfile;

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
    private int mAction;

    /**
     *
     */
    public static Fragment getInstanceNew() {
        return getInstance(ProfileActivity.ACTION_NEW);
    }

    /**
     *
     * @return
     */
    public static Fragment getInstanceForce() {
        return getInstance(ProfileActivity.ACTION_FORCE);
    }

    private static Fragment getInstance(int action) {
        Bundle args = new Bundle();
        args.putInt(ProfileActivity.EXTRA_ACTION, action);

        Fragment fragment = new ProfileNew();
        fragment.setArguments(args);
        return fragment;
    }

    private void handleBundle(Bundle bundle) {
        mAction = bundle.getInt(ProfileActivity.EXTRA_ACTION, ProfileActivity.ACTION_NEW);
    }

    public void spinnerAdapters() {
        List<String> makes = mVModel.getManufacturers().getValue();
        if (makes == null) {
            makes = new ArrayList<String>();
        }

        // Setup the model adapter first, because it is updated once a make is selected
        mModelAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, makes);
        mMakeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>());

        mMakeListener = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mVModel.getModels(mMakeAdapter.getItem(position), mModelAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

        mVModel.getManufacturers().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> manufacturers) {
                if (manufacturers != null) {
                    mMakeAdapter.clear();
                    mMakeAdapter.addAll(manufacturers);
                    mMakeAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    private boolean parseAndCreate() {
        String name = mNameEditText.getText().toString();
        if (name.equals("")) {
            mNameEditText.requestFocus();
            Toast.makeText(getContext(), getString(R.string.edit_profile_error_name_missing), Toast.LENGTH_SHORT).show();
            return false;
        }

        String make = mMakeSpinner.getSelectedItem().toString();
        String model = mModelSpinner.getSelectedItem().toString();
        String reg = mRegEditText.getText().toString();
        boolean active = mAction == ACTION_FORCE || mActiveCheck.isChecked();


        mActiveProfile.newProfile(name, make, model, reg, active, new ActiveProfile.InternalPopulateProfileCallback() {
            @Override
            public void onInsertion(Profile profile) {
                if (profile == null) {
                    Toast.makeText(getContext(), "Unable to save the profile", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (mAction == ProfileActivity.ACTION_FORCE) {
                        Intent intent = MainActivity.getIntent(getContext());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }
            }
        });
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_new, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile_create:
                parseAndCreate();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(savedInstanceState) called");

        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            handleBundle(savedInstanceState);
        } else {
            handleBundle(getArguments());
        }

        mVModel = ViewModelProviders.of(this).get(VehicleModel.class);
        mActiveProfile = ViewModelProviders.of(this).get(ActiveProfile.class);

        spinnerAdapters();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        getActivity().setTitle(getString(R.string.new_profile_title));


        /** Fetching the UI elements **/
        mNameEditText = view.findViewById(R.id.edit_profile_name_edit);
        mNameEditText.requestFocus();
        mMakeSpinner = view.findViewById(R.id.edit_profile_make_spinner);
        mModelSpinner = view.findViewById(R.id.edit_profile_model_spinner);
        mRegEditText = view.findViewById(R.id.edit_profile_reg_edit);
        mActiveCheck = view.findViewById(R.id.edit_profile_active_check);

        /** Attaches the make spinner logic **/
        mMakeSpinner.setAdapter(mMakeAdapter);
        mMakeSpinner.setOnItemSelectedListener(mMakeListener);

        /** Attaches the model spinner logic **/
        mModelSpinner.setAdapter(mModelAdapter);

        /** Forced new profile, therefore we want to set it active no matter what **/
        if (mAction == ACTION_FORCE) {


            mActiveCheck.setChecked(true);
            mActiveCheck.setVisibility(View.GONE);
        }
        return view;
    }


}