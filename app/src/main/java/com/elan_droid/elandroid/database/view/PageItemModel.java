package com.elan_droid.elandroid.database.view;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.elan_droid.elandroid.database.AppDatabase;
import com.elan_droid.elandroid.database.entity.Page;
import com.elan_droid.elandroid.database.entity.PageItem;
import com.elan_droid.elandroid.database.relation.DetailedPage;

/**
 * Created by Peter Smith
 */

public class PageItemModel extends AndroidViewModel {

    private AppDatabase mDatabase;

    public PageItemModel(@NonNull Application application) {
        super(application);

        this.mDatabase = AppDatabase.getInstance(application);
    }


    public void newPageItem (PageItem item) {
        new PopulateAsyncTask(mDatabase).execute(item);
    }

    /**
     * Task for populating the database with a new profile
     */
    private static class PopulateAsyncTask extends AsyncTask<PageItem, Void, PageItem> {

        private AppDatabase mmDatabase;
        private PageModel.NewPageCallback mmCallback;

        PopulateAsyncTask (AppDatabase database) {
            mmDatabase = database;
            //mmCallback = callback;
        }

        @Override
        protected PageItem doInBackground(PageItem... params) {
            if(params.length > 0) {
                final PageItem item = params[0];
                final long id = mmDatabase.pageItemDao().insert(item);

                if (id != 0) {
                    item.setId(id);
                    return item;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(PageItem page) {
            if (mmCallback != null) {
                //mmCallback.onPageCreated(new DetailedPage(page));
            }
        }
    }

}
