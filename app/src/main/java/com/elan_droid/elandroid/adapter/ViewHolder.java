package com.elan_droid.elandroid.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Peter Smith
 */
public abstract class ViewHolder extends RecyclerView.ViewHolder {

    public ViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bindType(ListItem item);

}