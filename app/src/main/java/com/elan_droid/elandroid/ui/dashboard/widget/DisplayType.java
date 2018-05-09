package com.elan_droid.elandroid.ui.dashboard.widget;

import android.content.Context;
import android.view.View;

/**
 * Created by Peter Smith
 */

public interface DisplayType {

    int getId();

    String getName();

    DisplaySize[] getSizes();

    View createView (Context context);

}
