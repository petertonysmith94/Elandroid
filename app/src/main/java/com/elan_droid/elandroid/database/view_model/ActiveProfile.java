package com.elan_droid.elandroid.database.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.data.entity.Page;
import com.elan_droid.elandroid.database.data.entity.User;
import com.elan_droid.elandroid.database.data.entity.Vehicle;
import com.elan_droid.elandroid.database.data.relation.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith
 *
 *
 */
public class ActiveProfile extends AndroidViewModel {

    private AppDatabase mDatabase;



    private final MutableLiveData<Profile> mActive;


    // TODO: move to profile model, keep separated
    private final LiveData<List<Profile>> mAllProfiles;
    private final MediatorLiveData<List<Profile>> mActiveProfiles;

    public ActiveProfile (Application application) {
        super (application);

        mDatabase = AppDatabase.getInstance(application);

        mAllProfiles = mDatabase.profileDao().getProfiles();
        mActive = new MutableLiveData<>();


        mActiveProfiles = new MediatorLiveData<>();
        mActiveProfiles.addSource(mAllProfiles, new Observer<List<Profile>>() {
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
        return mAllProfiles;
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

    public long getActiveProfileId() {
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


    public void fetchProfile (long profileId, FetchActiveProfileCallback callback) {
        new FetchAsyncTask(mDatabase, callback).execute(profileId);
    }

    public interface FetchActiveProfileCallback {
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
            if (mmCallback != null) {
                mmCallback.onFetch(profile);
            }
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
        new PopulateAsyncTask(mDatabase, new InternalPopulateProfileCallback() {
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

        private AppDatabase mmDatabase;
        private InternalPopulateProfileCallback mmCallback;

        PopulateAsyncTask (AppDatabase database, InternalPopulateProfileCallback callback) {
            mmDatabase = database;
            mmCallback = callback;
        }

        @SuppressWarnings("We should commit because it's already an Async task ")
        @Override
        protected Profile doInBackground(String... params) {
            if(params.length >= 4) {
                final Vehicle vehicle = mmDatabase.vehicleDao().getVehicle(params[0], params[1]);
                final long userId = mmDatabase.userVehicleDao().baseInsert(new User(vehicle.getId(), params[2], params[3]));

                final Page page = new Page(userId, vehicle.getDefaultMessageId(), "Default");
                mmDatabase.pageDao().baseInsert(page);

                return mmDatabase.profileDao().getProfile(userId);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Profile profile) {
            mmCallback.onInsertion(profile);
        }
    }

    public void updateProfile(final Profile profile, final boolean active, final UpdateProfileCallback callback){
        new UpdateAsyncTask(mDatabase, new UpdateProfileCallback() {
            @Override
            public void onProfileUpdated(boolean success) {
                if (active) {
                    setActiveProfile(profile);
                }
                if (callback != null) {
                    callback.onProfileUpdated(success);
                }
            }
        }).execute(profile);
    }

    public interface UpdateProfileCallback {

        void onProfileUpdated (boolean success);

    }

    /**
     * Task for populating the database with a new profile
     */
    private static class UpdateAsyncTask extends AsyncTask<Profile, Void, Boolean> {

        private AppDatabase mmDatabase;
        private UpdateProfileCallback mmCallback;

        UpdateAsyncTask (AppDatabase database, UpdateProfileCallback callback) {
            mmDatabase = database;
            mmCallback = callback;
        }

        @SuppressWarnings("We should commit because it's already an Async task ")
        @Override
        protected Boolean doInBackground(Profile... params) {
            User user;

            if(params.length > 0 && params[0] != null && (user = params[0].getUser()) != null) {
                mmDatabase.userVehicleDao().update(user);
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (mmCallback != null) {
                mmCallback.onProfileUpdated(success);
            }
        }
    }

    public void deleteProfile(final Profile profile, final DeleteProfileCallback callback) {
        new DeleteAsyncTask(mDatabase, new InternalDeleteProfileCallback() {
            @Override
            public void onProfileDeleted(long profileId) {
                if (profileId != -1 && profile.getProfileId() != profileId) {
                    //setActiveProfileId(profile);
                }

                if (callback != null) {
                    callback.onProfileDeleted(profileId != -1);
                }
            }
        }).execute(profile);

    }

    public interface DeleteProfileCallback {

        void onProfileDeleted (boolean success);

    }

    private interface InternalDeleteProfileCallback {

        void onProfileDeleted (long profileId);

    }

    private static class DeleteAsyncTask extends AsyncTask<Profile, Void, Long> {
        private AppDatabase mmDatabase;
        private InternalDeleteProfileCallback mmCallback;

        DeleteAsyncTask (AppDatabase database, InternalDeleteProfileCallback callback) {
            this.mmDatabase = database;
            this.mmCallback = callback;
        }

        @Override
        protected Long doInBackground(Profile... params) {
            long id = -1;

            if (params.length > 0 && params[0] != null) {
                id = params[0].getProfileId();
                mmDatabase.userVehicleDao().baseDelete(params[0].getUser());

                if (params[0].isActive()) {
                    id = mmDatabase.userVehicleDao().fetchOne();
                }
            }
            return id;
        }

        @Override
        protected void onPostExecute(Long result) {
            mmCallback.onProfileDeleted(result);
        }
    }


}
