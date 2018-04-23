package com.elan_droid.elandroid.ui.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elan_droid.elandroid.database.entity.Trip;

/**
 * Created by BorisJohnson on 4/21/2018.
 */
public abstract class BasePage extends Fragment {



    public boolean startTrip (Trip trip) {
        return false;
    }

}
