package com.elan_droid.elandroid.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by Peter Smith
 */

public abstract class BaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ListItem> mItems;

    public BaseAdapter(List<ListItem> items) {
        this.mItems = items;
    }

    @Override
    public int getItemViewType (int position) {
        return mItems.get(position).getListItemType();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

}
