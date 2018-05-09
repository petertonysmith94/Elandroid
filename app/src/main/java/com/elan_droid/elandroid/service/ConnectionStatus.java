package com.elan_droid.elandroid.service;

import android.support.annotation.IdRes;
import android.support.annotation.MenuRes;

import com.elan_droid.elandroid.R;

/**
 * Created by Peter Smith
 */
public enum ConnectionStatus {

    DISCONNECTED(R.menu.service_disconnected, R.id.service_disconnected),
    CONNECTED(R.menu.service_connected, R.id.service_connected),
    LOGGING(R.menu.service_logging, R.id.service_logging);

    private int menuResource;
    private int groupContainer;

    ConnectionStatus (@MenuRes int menuResource, @IdRes int groupContainer) {
        this.menuResource = menuResource;
        this.groupContainer = groupContainer;
    }

    public int getMenuResource() {
        return menuResource;
    }

    public int getGroupContainerId() {
        return menuResource;
    }

}
