package com.elan_droid.elandroid.database.view;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.entity.Page;
import com.elan_droid.elandroid.database.relation.DetailedPage;

import java.util.List;

/**
 * Created by Peter Smith
 */

public class PageModel extends PageItemModel {

    private AppDatabase mDatabase;

    public PageModel(@NonNull Application application) {
        super(application);

        this.mDatabase = AppDatabase.getInstance(application);
    }



    public void newPage (long userId, String name, NewPageCallback callback) {
        new PopulateAsyncTask(mDatabase, callback).execute(new Page(0, userId, name, 0));
    }

    public interface NewPageCallback {

        public void onPageCreated (DetailedPage page);

    }

    /**
     * Task for populating the database with a new profile
     */
    private static class PopulateAsyncTask extends AsyncTask<Page, Void, Page> {

        private AppDatabase mmDatabase;
        private NewPageCallback mmCallback;

        PopulateAsyncTask (AppDatabase database, NewPageCallback callback) {
            mmDatabase = database;
            mmCallback = callback;
        }

        @Override
        protected Page doInBackground(Page... params) {
            if(params.length > 0) {
                final Page page = params[0];
                final long id = mmDatabase.pageDao().insert(page);
                if (id != 0) {
                    page.setId(id);
                    return page;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Page page) {
            if (mmCallback != null) {
                mmCallback.onPageCreated(new DetailedPage(page));
            }
        }
    }

    public void fetchDetailedPages(long userId, FetchDetailedPagesCallback callback) {
        new FetchDetailedAsyncTask(mDatabase, callback).execute(userId);
    }

    public interface FetchDetailedPagesCallback {

        public void onFetch (List<DetailedPage> pages);

    }

    /**
     *
     */
    private static class FetchDetailedAsyncTask extends AsyncTask<Long, Void, List<DetailedPage>> {

        private AppDatabase mmDatabase;
        private FetchDetailedPagesCallback mmCallback;

        FetchDetailedAsyncTask(AppDatabase database, FetchDetailedPagesCallback callback) {
            this.mmDatabase = database;
            this.mmCallback = callback;
        }

        @Override
        protected List<DetailedPage> doInBackground(Long... params) {
            List<DetailedPage> pages = null;
            if (params.length > 0 && params[0] != 0) {
                pages = mmDatabase.pageDao().getDetailedPages(params[0]);
            }
            return pages;
        }

        @Override
        protected void onPostExecute(List<DetailedPage> pages) {
            if (mmCallback != null) {
                mmCallback.onFetch(pages);
            }
        }

    }

    public void fetchPages(long userId, FetchPagesCallback callback) {
        new FetchAsyncTask(mDatabase, callback).execute(userId);
    }

    public interface FetchPagesCallback {

        public void onFetch (List<Page> pages);

    }

    /**
     *
     */
    private static class FetchAsyncTask extends AsyncTask<Long, Void, List<Page>> {

        private AppDatabase mmDatabase;
        private FetchPagesCallback mmCallback;

        FetchAsyncTask(AppDatabase database, FetchPagesCallback callback) {
            this.mmDatabase = database;
            this.mmCallback = callback;
        }

        @Override
        protected List<Page> doInBackground(Long... params) {
            List<Page> pages = null;
            if (params.length > 0 && params[0] != 0) {
                pages = mmDatabase.pageDao().getPages(params[0]);
            }
            return pages;
        }

        @Override
        protected void onPostExecute(List<Page> pages) {
            if (mmCallback != null) {
                mmCallback.onFetch(pages);
            }
        }

    }



}
