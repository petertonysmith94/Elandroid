package com.elan_droid.elandroid.ui.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.entity.Page;

/**
 * Created by Peter Smith
 */

    public class BaseDashboardPage extends Fragment {

    private static final String EXTRA_PAGE = "com.elan_droid.elandroid.ui.dashboard.EXTRA_PAGE";

    private ViewPager mPager;


    public static Fragment getInstance(Page page) {
        Bundle params = new Bundle();
        params.putParcelable(EXTRA_PAGE, page);

        Fragment fragment = new BaseDashboardPage();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_page, container, false);



        return view;
    }

}
