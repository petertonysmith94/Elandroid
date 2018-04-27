package com.elan_droid.elandroid.database.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.dao.BaseDao;

/**
 * Created by Peter Smith on 4/22/2018.
 *
 * TODO: Implement in the future, reducing the repeated code in view models
 */
public abstract class BaseViewModel<T> extends AndroidViewModel {

    private AppDatabase database;

    private BaseDao<T> baseDao;

    public BaseViewModel (@NonNull Application application) {
        super (application);
        database = AppDatabase.getInstance(application);
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public BaseDao<T> getDao() {
        return baseDao;
    }

}
