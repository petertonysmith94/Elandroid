package com.elan_droid.elandroid.ui.generic;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Created by Peter Smith
 *
 * Responsible for providing a generic RecyclerView.Adapter for use with all list.
 *
 * TODO: implement this an replace current system
 */
public abstract class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.ViewHolder> {

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


    public interface ListItem {

        int TYPE_PROFILE_MINIMAL = 0;
        int TYPE_PROFILE_DETAILED = 1;

        int getListItemType ();



    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindType(ListItem item);

    }

}
