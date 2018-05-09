package com.elan_droid.elandroid.database.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.data.entity.PageItem;

import java.util.List;

/**
 * Created by Peter Smith
 */

public class PageItemModel extends AndroidViewModel {

    private AppDatabase mDatabase;

    private MutableLiveData<List<PageItem>> items;

    public PageItemModel(@NonNull Application application) {
        super(application);

        this.mDatabase = AppDatabase.getInstance(application);
        this.items = new MutableLiveData<>();
    }

    public MutableLiveData<List<PageItem>> getItems(long pageId) {
        fetchItems(pageId, new FetchPageItemCallback() {
            @Override
            public void onFetch(List<PageItem> result) {
                items.postValue(result);
            }
        });
        return items;
    }

    public void fetchItems (long pageId, FetchPageItemCallback callback) {
        new FetchAsyncTask(mDatabase, callback).execute(pageId);
    }

    public interface FetchPageItemCallback {
        void onFetch(List<PageItem> items);
    }

    /**
     *
     */
    private static class FetchAsyncTask extends AsyncTask<Long, Void, List<PageItem>> {

        private AppDatabase mmDatabase;
        private FetchPageItemCallback mmCallback;

        FetchAsyncTask(AppDatabase database, FetchPageItemCallback callback) {
            this.mmDatabase = database;
            this.mmCallback = callback;
        }

        @Override
        protected List<PageItem> doInBackground(Long... params) {
            List<PageItem> items = null;
            if (params.length > 0 && params[0] != null) {
                items = mmDatabase.pageItemDao().getPageItems(params[0]);
            }
            return items;
        }

        @Override
        protected void onPostExecute(List<PageItem> items) {
            if (mmCallback != null) {
                mmCallback.onFetch(items);
            }
        }

    }

    /**
     * Updates a given page item
     * @param item
     * @param callback
     */
    public void update(PageItem item, UpdatePagesCallback callback) {
        new UpdateAsyncTask(mDatabase, callback).execute(item);
    }

    public interface UpdatePagesCallback {
        void onUpdate(boolean success);
    }

    /**
     *
     */
    private static class UpdateAsyncTask extends AsyncTask<PageItem, Void, Boolean> {

        private AppDatabase mmDatabase;
        private UpdatePagesCallback mmCallback;

        UpdateAsyncTask(AppDatabase database, UpdatePagesCallback callback) {
            this.mmDatabase = database;
            this.mmCallback = callback;
        }

        @Override
        protected Boolean doInBackground(PageItem... params) {
            if (params.length > 0 && params[0] != null) {
                mmDatabase.pageItemDao().update(params[0]);
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (mmCallback != null) {
                mmCallback.onUpdate(success);
            }
        }

    }



}
