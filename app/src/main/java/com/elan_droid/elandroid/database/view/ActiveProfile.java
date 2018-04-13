package com.elan_droid.elandroid.database.view;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.dao.ProfileDao;
import com.elan_droid.elandroid.database.entity.User;
import com.elan_droid.elandroid.database.relation.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith
 */

public class ActiveProfile extends AndroidViewModel {

    private AppDatabase mDatabase;

    private final MutableLiveData<Profile> mActive;

    private final LiveData<List<Profile>> mProfiles;

    private final MediatorLiveData<List<Profile>> mActiveProfiles;

    public ActiveProfile (Application application) {
        super (application);

        mDatabase = AppDatabase.getInstance(application);

        mActive = new MutableLiveData<>();
        mProfiles = mDatabase.profileDao().getProfiles();

        mActiveProfiles = new MediatorLiveData<>();
        mActiveProfiles.addSource(mProfiles, new Observer<List<Profile>>() {
            @Override
            public void onChanged(@Nullable List<Profile> profiles) {
                if (profiles == null) {
                    profiles = new ArrayList<Profile>();
                }
                else {
                    long activeId = getActiveProfileId();

                    for (Profile p : profiles) {
                        p.setActive(activeId == p.getProfileId());
                    }
                }
                mActiveProfiles.setValue(profiles);
            }
        });

        mActiveProfiles.addSource(getActiveProfile(), new Observer<Profile>() {
            @Override
            public void onChanged(@Nullable Profile profile) {
                List<Profile> profiles = mActiveProfiles.getValue();

                if (profiles == null) {
                    profiles = new ArrayList<Profile>();
                }
                if (profile != null) {
                    for (Profile p : profiles) {
                        p.setActive(p.getProfileId() == profile.getProfileId());
                    }
                }
                mActiveProfiles.setValue(profiles);
            }
        });

        updateActiveProfile();
    }

    public MutableLiveData<Profile> getActiveProfile() {
        return mActive;
    }

    public LiveData<List<Profile>> getProfiles() {
        return mProfiles;
    }

    public MediatorLiveData<List<Profile>> getActiveProfiles() {
        return mActiveProfiles;
    }

    public void setActiveProfile (Profile profile) {
        long activeId = getActiveProfileId();

        // If profile is null, then set active profile to 0
        if (profile == null) {
            setActiveProfileId(0);
        }
        // Otherwise check if we need to update
        else if (profile.getProfileId() != activeId) {
            long newActiveId = profile.getProfileId();

            setActiveProfileId(newActiveId);
            updateActiveProfile();
        }
    }

    private void updateActiveProfile() {
        new FetchAsyncTask(mDatabase, new FetchActiveProfileCallback() {
            @Override
            public void onFetch(Profile profile) {
                mActive.postValue(profile);
            }
        }).execute(getActiveProfileId());
    }

    private long getActiveProfileId() {
        return getApplication()
                .getSharedPreferences(getApplication().getString(R.string.shared_prefs_profile), Context.MODE_PRIVATE)
                .getLong(getApplication().getString(R.string.saved_active_profile_id), 0);
    }

    private void setActiveProfileId(long id) {
        getApplication()
            .getSharedPreferences (getApplication().getString(R.string.shared_prefs_profile), Context.MODE_PRIVATE)
            .edit()
            .putLong(getApplication().getString(R.string.saved_active_profile_id), id)
            .apply();
    }





    private interface FetchActiveProfileCallback {
        void onFetch (Profile profile);
    }

    private static class FetchAsyncTask extends AsyncTask<Long, Void, Profile> {

        private AppDatabase mmDatabase;
        private FetchActiveProfileCallback mmCallback;

        FetchAsyncTask(AppDatabase database, FetchActiveProfileCallback callback) {
            this.mmDatabase = database;
            this.mmCallback = callback;
        }

        @Override
        protected Profile doInBackground(Long... params) {
            Profile profile = null;
            if(params[0] != 0) {
                profile = mmDatabase.profileDao().getProfile(params[0]);
            }
            return profile;
        }

        @Override
        protected void onPostExecute(Profile profile) {
            mmCallback.onFetch(profile);
        }
    }

    private void fetchAllProfiles(FetchAllProfileCallback callback) {
        new FetchAllAsyncTask(getApplication(), mDatabase, callback).execute();
    }

    interface FetchAllProfileCallback {
        void onFetchProfiles (List<Profile> profiles);
    }

    /**
     *
     */
    private static class FetchAllAsyncTask extends AsyncTask<Void, Void, List<Profile>> {

        private Context mmContext;
        private AppDatabase mmDatabase;
        private FetchAllProfileCallback mmCallback;

        FetchAllAsyncTask(Context context, AppDatabase database, FetchAllProfileCallback callback) {
            this.mmContext = context;
            this.mmDatabase = database;
            this.mmCallback = callback;
        }

        @Override
        protected List<Profile> doInBackground(Void... params) {
            List<Profile> profiles = mmDatabase.profileDao().getAllProfiles();

            if (profiles == null) {
                profiles = new ArrayList<>();
            }
            else {
                long id = mmContext.getSharedPreferences(mmContext.getString(R.string.shared_prefs_profile), Context.MODE_PRIVATE)
                        .getLong(mmContext.getString(R.string.saved_active_profile_id), 0);
                for (Profile profile : profiles) {
                    if (profile.getProfileId() == id) {
                        profile.setActive(true);
                        break;
                    }
                }
            }
            return profiles;
        }

        @Override
        protected void onPostExecute(List<Profile> profiles) {
            if (mmCallback != null) {
                mmCallback.onFetchProfiles(profiles);
            }
        }
    }

    /**
     *
     * @param make
     * @param model
     * @param name
     * @param registration
     */
    public void newProfile (String name, String make, String model,
                            String registration, final boolean active, final InternalPopulateProfileCallback listener) {
        new PopulateAsyncTask(getApplication(), mDatabase, new InternalPopulateProfileCallback() {
            @Override
            public void onInsertion(Profile profile) {
                if (active && profile != null) {
                    setActiveProfile(profile);
                }
                if (listener != null) {
                    listener.onInsertion(profile);
                }
            }
        }).execute(make, model, name, registration);

    }

    public interface InternalPopulateProfileCallback {
        void onInsertion (Profile profile);
    }

    /**
     * Task for populating the database with a new profile
     */
    private static class PopulateAsyncTask extends AsyncTask<String, Void, Profile> {

        private Context mmContext;
        private AppDatabase mmDatabase;
        private InternalPopulateProfileCallback mmCallback;

        PopulateAsyncTask (Context context, AppDatabase database, InternalPopulateProfileCallback callback) {
            mmContext = context;
            mmDatabase = database;
            mmCallback = callback;
        }

        @SuppressWarnings("We should commit because it's already an Async task ")
        @Override
        protected Profile doInBackground(String... params) {
            if(params.length >= 4) {
                final long vehicleId = mmDatabase.vehicleDao().getId(params[0], params[1]);
                final long userId = mmDatabase.userVehicleDao().insert(new User(vehicleId, params[2], params[3]));
                return mmDatabase.profileDao().getProfile(userId);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Profile profile) {
            mmCallback.onInsertion(profile);
        }
    }

}
