package com.elan_droid.elandroid.database.view;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.entity.Trip;
import com.elan_droid.elandroid.database.entity.User;
import com.elan_droid.elandroid.database.relation.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith
 */

public class ProfileModel extends AndroidViewModel {

    private AppDatabase mDatabase;

    private final LiveData<List<Profile>> mProfiles;

    //private final LiveData<List<Profile>> mActiveProfiles;

    private final LiveData<List<Profile>> mActive;

    private long mProfileId = 0;

    public ProfileModel (Application application) {
        super (application);

        mDatabase = AppDatabase.getInstance(application);
        mProfiles = mDatabase.profileDao().getProfiles();

        /**
        mActive = Transformations.map(mProfiles, new Function<List<Profile>, List<Profile>>() {
            @Override
            public List<Profile> apply(List<Profile> input) {
                if (input == null) {
                    input = new ArrayList<Profile>();
                }
                else {
                    final long profileId = getActiveProfileId();
                    if (mProfileId != profileId) {
                        mProfileId = profileId;

                        for (Profile p : input) {
                            if (profileId == p.getProfileId()) {

                            }
                        }
                    }
                }
                return input;
            }
        });
        **/

        mActive = Transformations.map(mProfiles, new Function<List<Profile>, List<Profile>>() {
                    @Override
                    public List<Profile> apply(List<Profile> input) {
                        final long profileId = getActiveProfileId();

                        for (Profile p : input) {
                            p.setActive(profileId == p.getUser().getId());
                        }
                        return input;
                    }
                });

                //mActive = mDatabase.profileDao().getLiveProfile(getActiveProfileId());
    }

    public LiveData<List<Profile>> getRawProfiles() {
        return mProfiles;
    }

    public LiveData<List<Profile>> getProfiles() {
        return mActive;
    }

    public long getActiveProfileId() {
        return getApplication().getSharedPreferences
                (getApplication().getString(R.string.shared_prefs_profile), Context.MODE_PRIVATE)
                .getLong(getApplication().getString(R.string.saved_active_profile_id), 0);
    }


    public void setActiveProfileId(Profile profile) {
        long id = profile == null ? 0 : profile.getProfileId();

        if (id != this.mProfileId) {
            // Set and store the profile id
            this.mProfileId = id;
            getApplication().getSharedPreferences
                    (getApplication().getString(R.string.shared_prefs_profile), Context.MODE_PRIVATE).edit()
                    .putLong(getApplication().getString(R.string.saved_active_profile_id), this.mProfileId)
                    .apply();

            final List<Profile> profiles = getRawProfiles().getValue();
            if (profiles != null) {
                profiles.get(profiles.indexOf(profile)).setActive(true);
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
                mmDatabase.userVehicleDao().delete(params[0].getUser());

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

    public void updateProfile(Profile profile, boolean active, UpdateProfileCallback callback){
        new UpdateAsyncTask(this.getApplication(), mDatabase, callback).execute(profile);
    }

    public interface UpdateProfileCallback {

        void onProfileUpdated (boolean success);

    }

    /**
     * Task for populating the database with a new profile
     */
    private static class UpdateAsyncTask extends AsyncTask<Profile, Void, Boolean> {

        private Context mmContext;
        private AppDatabase mmDatabase;
        private UpdateProfileCallback mmCallback;

        UpdateAsyncTask (Context context, AppDatabase database, UpdateProfileCallback callback) {
            mmContext = context;
            mmDatabase = database;
            mmCallback = callback;
        }

        @SuppressWarnings("We should commit because it's already an Async task ")
        @Override
        protected Boolean doInBackground(Profile... params) {
            User vehicle;

            if(params.length > 0 && params[0] != null && (vehicle = params[0].getUser()) != null) {
                mmDatabase.userVehicleDao().update(vehicle);
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


    /**
     *
     * @param make
     * @param model
     * @param name
     * @param registration
     */
    public void newProfile (String name, String make, String model, String registration, final boolean active) {
        new PopulateAsyncTask(getApplication(), mDatabase, new InternalPopulateProfileCallback() {
            @Override
            public void onInsertion(Profile profile) {
                if (active && profile != null) {
                    setActiveProfileId(profile);
                }
            }
        }).execute(make, model, name, registration);

    }

    private interface InternalPopulateProfileCallback {
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
    }

    /**
     *
     * @param callback
     */
    public void fetchActiveProfile (final FetchProfileCallback callback) {
        fetchProfile(getActiveProfileId(), callback);
    }

    /**
     *
     * @param userId
     * @param callback
     */
    public void fetchProfile (long userId, final FetchProfileCallback callback) {
        new FetchAsyncTask(this.getApplication(), mDatabase, callback).execute(userId);
    }

    public interface FetchProfileCallback {
        void onProfileFetched (Profile profile);
    }

    /**
     *
     */
    private static class FetchAsyncTask extends AsyncTask<Long, Void, Profile> {

        private Context mmContext;
        private AppDatabase mmDatabase;
        private FetchProfileCallback mmCallback;

        FetchAsyncTask(Context context, AppDatabase database, FetchProfileCallback callback) {
            this.mmContext = context;
            this.mmDatabase = database;
            this.mmCallback = callback;
        }

        @Override
        protected Profile doInBackground(Long... params) {
            Profile profile = null;
            if(params.length > 0 && params[0] != 0) {
                profile = mmDatabase.profileDao().getProfile(params[0]);
                long id = mmContext.getSharedPreferences(mmContext.getString(R.string.shared_prefs_profile), Context.MODE_PRIVATE)
                        .getLong(mmContext.getString(R.string.saved_active_profile_id), 0);
                profile.setActive(profile.getUser().getId() == id);
            }
            return profile;
        }

        @Override
        protected void onPostExecute(Profile profile) {
            if (mmCallback != null) {
                mmCallback.onProfileFetched(profile);
            }
        }
    }


    public void fetchAllProfiles(FetchAllProfileCallback callback) {
        new FetchAllAsyncTask(this.getApplication(), mDatabase, callback).execute();
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

}
