package com.elan_droid.elandroid.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.relation.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith
 */

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnProfileSelectedListener {

        void onProfileSelected (Profile profile);

    }

    private List<Profile> mProfiles;
    private OnProfileSelectedListener mListener;

    public ProfileAdapter () {
        this (null, null);
    }

    public ProfileAdapter (OnProfileSelectedListener listener) {
        this (null, listener);
    }

    public ProfileAdapter (List<Profile> profiles, OnProfileSelectedListener listener) {
        this.mProfiles = (profiles == null) ? new ArrayList<Profile>() : profiles;
        this.mListener = listener;
    }

    public void update (List<Profile> profiles) {
        if (profiles != null) {
            this.mProfiles = profiles;
            notifyDataSetChanged();
        }
    }

    public void setActive (Profile profile) {
        for (Profile p : mProfiles) {
            p.setActive(profile.getProfileId() == p.getProfileId());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mProfiles.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.list_item_profile, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ProfileViewHolder) holder).bind(mProfiles.get(position), mListener);
    }

    private static class ProfileViewHolder extends RecyclerView.ViewHolder {

        private ImageView mmActiveGlow;
        private TextView mmProfileName;
        private TextView mmProfileMakeModel;
        private TextView mmProfileRegistration;

        ProfileViewHolder (View view) {
            super (view);

            mmActiveGlow = (ImageView) view.findViewById(R.id.list_item_profile_active);
            mmProfileName = (TextView) view.findViewById(R.id.list_item_profile_name);
            mmProfileMakeModel = (TextView) view.findViewById(R.id.list_item_profile_make_model);
            mmProfileRegistration = (TextView) view.findViewById(R.id.list_item_profile_registration);
        }

        void bind(final Profile profile, final OnProfileSelectedListener listener) {
            mmActiveGlow.setVisibility(profile.isActive() ? View.VISIBLE : View.GONE);
            mmProfileName.setText(profile.getUser().getName());
            mmProfileMakeModel.setText(profile.getMakeModel());
            mmProfileRegistration.setText(profile.getRegistration());

            if (listener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onProfileSelected(profile);
                    }
                });
            }
        }

    }
}
