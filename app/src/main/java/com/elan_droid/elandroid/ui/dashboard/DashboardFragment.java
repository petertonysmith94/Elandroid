package com.elan_droid.elandroid.ui.dashboard;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.entity.Page;
import com.elan_droid.elandroid.database.relation.Profile;
import com.elan_droid.elandroid.database.view.PageModel;
import com.elan_droid.elandroid.ui.dialog.AddPageDialog;
import com.elan_droid.elandroid.ui.generic.BaseFragment;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.elan_droid.elandroid.ui.generic.BaseActivity.NEW_PAGE_REQUEST_CODE;

/**
 * Created by Peter Smith
 *
 * @Purpose
 *
 */
public class DashboardFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "DashboardFragment";

    //
    private PageModel mPageModel;


    private DashboardPagerAdapter mPageAdapter;


    private ViewPager mPager;


    private TabLayout tabLayout;


    private ImageButton mNewPageBtn;

    private long mProfileId;

    public static Fragment getInstance(long userId) {
        Bundle args = createArguments(userId);

        Fragment fragment = new DashboardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(savedInstanceState) called");
        setHasOptionsMenu(true);

        mPageAdapter = new DashboardPagerAdapter(getActivity().getSupportFragmentManager());

        mPageModel = ViewModelProviders.of(getActivity()).get(PageModel.class);


        getProfileModel().getActiveProfile().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(@Nullable Profile profile) {
                if (profile != null) {
                    fetchPages(profile.getProfileId());
                }
            }
        });

        getActivity().setTitle("Dashboard");
    }

    private void fetchPages(long profileId) {
        mPageModel.fetchPages(profileId, new PageModel.FetchPagesCallback() {
            @Override
            public void onFetch(List<Page> pages) {
                if (pages != null) {
                    mPageAdapter.updatePages(pages);
                }
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu(menu) called");
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu(menu, inflater) called");
        inflater.inflate(R.menu.dashboard, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_widget_add:
                Page page = mPageAdapter.getPage (mPager.getCurrentItem());
                displayDialog(WidgetDialog.getInstance(page));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        setHasOptionsMenu(true);
        getActivity().setTitle("Dashboard");

        mPager = (ViewPager) view.findViewById(R.id.dashboard_view_pager);
        mPager.setAdapter(mPageAdapter);

        tabLayout = (TabLayout) view.findViewById(R.id.area_tabs);
        tabLayout.setupWithViewPager(mPager, true);

        mNewPageBtn = (ImageButton) view.findViewById(R.id.add_new_page_button);
        mNewPageBtn.setOnClickListener(this);

        //getActivity().setTitle(getString(R.string.dashboard_title));

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_new_page_button) {
            // Display the dialog for adding a new page
            DialogFragment dialog = AddPageDialog.getInstance();
            dialog.setTargetFragment(this, NEW_PAGE_REQUEST_CODE);
            displayDialog(dialog);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEW_PAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    String name = data.getExtras().getString(AddPageDialog.PAGE_NAME_EXTRA);
                    mPageModel.newPage(getActiveProfileId(), name, new PageModel.NewPageCallback() {
                        @Override
                        public void onPageCreated(Page page) {
                            mPageAdapter.addPage(page);
                        }
                    });
                }
                break;

            default :
                super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
