package com.elan_droid.elandroid.ui.dashboard;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.elan_droid.elandroid.database.entity.Page;
import com.elan_droid.elandroid.database.relation.DetailedPage;
import com.elan_droid.elandroid.ui.page.PacketListPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith
 */

public class PageAdapter extends FragmentStatePagerAdapter  {


    private List<DetailedPage> mPages;

    public PageAdapter(@NonNull FragmentManager fm) {
        this (fm, new ArrayList<DetailedPage>());
    }

    public PageAdapter(FragmentManager fm, @NonNull List<DetailedPage> pages) {
        super(fm);
        this.mPages = pages;
    }

    public void addPage(DetailedPage page) {
        mPages.add(page);
        notifyDataSetChanged();
    }

    public void updatePages(List<DetailedPage> pages) {
        mPages.clear();

        if (pages != null) {
            mPages.addAll(pages);
        }
        notifyDataSetChanged();
    }

    public Page getPage (int position) {
        return mPages.size() > 0 ? mPages.get(position).getPage() : null;
    }

    public DetailedPage getDetailedPage (int position) {
        return mPages.get(position);
    }

    @Override
    public int getCount() {
        return mPages.size();
    }


    @Override
    public Fragment getItem(int position) {
        final DetailedPage page = mPages.get(position);
        Fragment fragment = PacketListPage.getInstance(page);
        fragment.setRetainInstance(false);
        page.setTarget(fragment);
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        final Page page = mPages.get(position).getPage();
        String title = "Dashboard";

        if (page != null) {
            title = page.getTitle();
        }
        return title;
    }

}
