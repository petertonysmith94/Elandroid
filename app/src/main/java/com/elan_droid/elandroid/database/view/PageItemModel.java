package com.elan_droid.elandroid.database.view;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.entity.PageItem;

/**
 * Created by Peter Smith
 */

public class PageItemModel extends AndroidViewModel {

    private AppDatabase mDatabase;

    public PageItemModel(@NonNull Application application) {
        super(application);

        this.mDatabase = AppDatabase.getInstance(application);
    }




}
