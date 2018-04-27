package com.elan_droid.elandroid.ui.profile;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.adapter.ProfileAdapter;
import com.elan_droid.elandroid.database.relation.Profile;
import com.elan_droid.elandroid.database.view_model.ActiveProfile;
import com.elan_droid.elandroid.database.view_model.ProfileModel;
import com.elan_droid.elandroid.ui.generic.BaseFragment;

import java.util.List;

/**
 * Created by Peter Smith
 */

public class ProfileList extends BaseFragment {

    public static final String TAG = "ProfileList";


    // Database View Models
    private ActiveProfile mActiveProfile;
    private ProfileModel mProfileModel;
    private MediatorLiveData<List<Profile>> mProfiles = new MediatorLiveData<>();

    // UI variables
    private RecyclerView mRecyclerView;
    private ProfileAdapter mProfileAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProfileAdapter.OnProfileSelectedListener mListener;

    public static BaseFragment getInstance() {
        BaseFragment fragment = new ProfileList();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (ProfileAdapter.OnProfileSelectedListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Calling context must implement the ProfileAdapter.OnProfileSelectedListener to use a ProfileList");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActiveProfile = ViewModelProviders.of(getActivity()).get(ActiveProfile.class);
        mProfileModel = ViewModelProviders.of(getActivity()).get(ProfileModel.class);

        mProfileAdapter = new ProfileAdapter(mListener);

        mActiveProfile.getActiveProfiles().observe(this, new Observer<List<Profile>>() {
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

    @Override
    public void onStop() {
        super.onStop();

        Fragment fragment = getFragmentManager().findFragmentById(R.id.profile_list_fragment);

        if(fragment != null)
            getFragmentManager().beginTransaction().remove(fragment).commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}