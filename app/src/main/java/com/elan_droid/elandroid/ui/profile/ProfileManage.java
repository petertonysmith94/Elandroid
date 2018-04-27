package com.elan_droid.elandroid.ui.profile;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.adapter.ProfileAdapter;
import com.elan_droid.elandroid.database.relation.Profile;
import com.elan_droid.elandroid.database.view_model.ActiveProfile;

import java.util.ArrayList;
import java.util.List;

/**
/**
 * Created by Peter Smith
 */

public class ProfileManage extends Fragment {

    public static final String TAG = "ProfileManage";


    // Database View Models
    private ActiveProfile mProfileModel;


    // UI variables
    private RecyclerView mRecyclerView;
    private ProfileAdapter mProfileAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // Variables
    private long mUserVehicleId;
    private int mAction;


    public static Fragment getInstance() {
        Fragment fragment = new ProfileManage();
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_manage, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile_add:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_base, ProfileNew.getInstanceNew())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(ProfileNew.TAG)
                        .commit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProfileModel = ViewModelProviders.of(this).get(ActiveProfile.class);

        mProfileAdapter = new ProfileAdapter(new ArrayList<Profile>(), new ProfileAdapter.OnProfileSelectedListener() {
            @Override
            public void onProfileSelected(Profile profile) {
                // Start the ProfileEdit framgent
                // TODO: Passing in the Profile into the fragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_base, ProfileEdit.getInstance(profile.getUser().getId()))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(ProfileEdit.TAG)
                        .commit();
            }
        });

        mProfileModel.getActiveProfiles().observe(this, new Observer<List<Profile>>() {
            @Override
            public void onChanged(@Nullable List<Profile> profiles) {
                mProfileAdapter.update(profiles);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_list, container, false);
        setHasOptionsMenu(true);
        getActivity().setTitle(getString(R.string.manage_profiles_title));

        mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.profile_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mProfileAdapter);

        return view;
    }
}