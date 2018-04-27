package com.elan_droid.elandroid.database.view_model;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.entity.Page;
import com.elan_droid.elandroid.database.entity.PageItem;
import com.elan_droid.elandroid.database.relation.DetailedPage;

import java.util.List;

/**
 * Created by Peter Smith
 */

public class PageModel extends PageItemModel {

    private AppDatabase mDatabase;

    private MutableLiveData<List<DetailedPage>> mPages;

    private long mUserId;

    public PageModel(@NonNull Application application) {
        super(application);

        this.mDatabase = AppDatabase.getInstance(application);
        this.mPages = new MutableLiveData<>();
    }

    public MutableLiveData<List<DetailedPage>> getPages() {
        return mPages;
    }

    private void updatePages (long userId) {
        if (mPages.hasObservers()) {
            fetchDetailedPages(userId, new FetchDetailedPagesCallback() {
                @Override
                public void onFetch(List<DetailedPage> pages) {
                    mPages.postValue(pages);
                }
            });
        }
    }

    public void setUserId (long userId) {
        if (this.mUserId != userId) {
            this.mUserId = userId;
            updatePages(userId);
        }
    }

    /**
     * Creates a new page on the dashboard
     * @param page      the page object
     * @param callback  the callback for onCreate of page
     */
    public void createPage(Page page, CreatePageCallback callback) {
        new PopulateAsyncTask(mDatabase, callback).execute(page);
    }

    public interface CreatePageCallback {
        public void onCreate(DetailedPage page);
    }

    /**
     * Task for populating the database with a new profile
     */
    private static class PopulateAsyncTask extends AsyncTask<Page, Void, Page> {

        private AppDatabase mmDatabase;
        private CreatePageCallback mmCallback;

        PopulateAsyncTask (AppDatabase database, CreatePageCallback callback) {
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
                mmCallback.onCreate(new DetailedPage(page));
            }
        }
    }

    public void newPageItem (PageItem item) {
        new PopulatePageItemAsyncTask(mDatabase, new CreatePageItemCallback() {
            @Override
            public void onCreate(PageItem item) {
                updatePages(mUserId);
            }
        }).execute(item);
    }

    public interface CreatePageItemCallback {
        void onCreate (PageItem item);
    }

    /**
     * Task for populating the database with a new profile
     */
    private static class PopulatePageItemAsyncTask extends AsyncTask<PageItem, Void, PageItem> {

        private AppDatabase mmDatabase;
        private CreatePageItemCallback mmCallback;

        PopulatePageItemAsyncTask (AppDatabase database, CreatePageItemCallback callback) {
            mmDatabase = database;
            mmCallback = callback;
        }

        @Override
        protected PageItem doInBackground(PageItem... params) {
            PageItem result = null;

            if(params.length > 0) {
                result = params[0];
                final long id = mmDatabase.pageItemDao().insert(result);

                if (id != 0) {
                    result.setId(id);
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(PageItem item) {
            if (mmCallback != null) {
                mmCallback.onCreate(item);
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
