package com.elan_droid.elandroid.adapter;

/**
 * Created by Peter Smith
 */

public interface ListItem {

    int TYPE_PROFILE_MINIMAL = 0;
    int TYPE_PROFILE_DETAILED = 1;

    int getListItemType ();

}
