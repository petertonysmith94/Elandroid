package com.elan_droid.elandroid.ui.dashboard;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.elan_droid.elandroid.database.entity.Page;
import com.elan_droid.elandroid.database.relation.PageContent;
import com.elan_droid.elandroid.database.view.PageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith
 */

public class DashboardPagerAdapter extends FragmentStatePagerAdapter  {


    private List<Page> mPages;

    public DashboardPagerAdapter(@NonNull FragmentManager fm) {
        this (fm, new ArrayList<Page>());
    }

    public DashboardPagerAdapter(FragmentManager fm, @NonNull List<Page> pages) {
        super(fm);
        this.mPages = pages;
    }

    public void addPage(Page page) {
        mPages.add(page);
        notifyDataSetChanged();
    }

    public void updatePages(List<Page> pages) {
        mPages.clear();
        mPages.addAll(pages);
        notifyDataSetChanged();
    }

    public Page getPage (int position) {
        return mPages.get(position);
    }

    @Override
    public int getCount() {
        return mPages.size();
    }

    @Override
    public Fragment getItem(int position) {
        final Page page = mPages.get(position);
        return BaseDashboardPage.getInstance(page);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        final Page page = mPages.get(position);
        String title = "Dashboard";

        if (page != null) {
            title = page.getTitle();
        }
        return title;
    }

}
