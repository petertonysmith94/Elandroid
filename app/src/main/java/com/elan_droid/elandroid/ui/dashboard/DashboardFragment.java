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
import android.widget.Toast;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.data.entity.Page;
import com.elan_droid.elandroid.database.data.entity.PageItem;
import com.elan_droid.elandroid.database.data.relation.DetailedPage;
import com.elan_droid.elandroid.database.data.relation.Profile;
import com.elan_droid.elandroid.database.view_model.PageModel;
import com.elan_droid.elandroid.ui.dashboard.page.PageAdapter;
import com.elan_droid.elandroid.ui.dashboard.page.AddPageItemDialog;
import com.elan_droid.elandroid.ui.dialog.AddPageDialog;
import com.elan_droid.elandroid.ui.generic.BaseFragment;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.elan_droid.elandroid.activity.BaseActivity.NEW_PAGE_ITEM_REQUEST_CODE;
import static com.elan_droid.elandroid.activity.BaseActivity.NEW_PAGE_REQUEST_CODE;

/**
 * Created by Peter Smith
 *
 * The responsibility of the DashboardFragment, is to provide a dynamic display of pages for a given user.
 *
 *
 * TODO: Convert into an activity which extends ServiceActivity
 * TODO: Implement multiple page type support, for dynamic views
 */
public class DashboardFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "DashboardFragment";

    // Page database model, page adapter, and view pager
    private PageModel mPageModel;
    private PageAdapter mPageAdapter;
    private ViewPager mPager;

    /**
     * Get's the instance of the DashboardFragment
     * @param userId    userId
     * @return
     */
    public static Fragment getInstance(long userId) {
        Bundle args = createArguments(userId);

        Fragment fragment = new DashboardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPageModel = ViewModelProviders.of(getActivity()).get(PageModel.class);
        mPageAdapter = new PageAdapter(getActivity().getSupportFragmentManager());
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(R.string.dashboard_title);

        mPageModel.getPages().observe(this, new Observer<List<DetailedPage>>() {
            @Override
            public void onChanged(@Nullable List<DetailedPage> detailedPages) {
                //mPager.removeAllViews();
                mPageAdapter.updatePages(detailedPages);
            }
        });

        getProfileModel().getActiveProfile().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(@Nullable Profile profile) {
                if (profile != null) {
                    mPageModel.setUserId(profile.getProfileId());
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu(menu, inflater) called");
        //inflater.inflate(R.menu.dashboard, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_widget_add:
                Page page = mPageAdapter.getPage (mPager.getCurrentItem());

                if (page != null) {
                    DialogFragment dialog = AddPageItemDialog.getInstance(page);
                    dialog.setTargetFragment(this, NEW_PAGE_ITEM_REQUEST_CODE);
                    displayDialog(dialog);
                }
                else {
                    Toast.makeText(getContext(), "You need to add a page before adding widgets", Toast.LENGTH_LONG).show();
                }
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

        // Sets the view pager and tab layout
        mPager = view.findViewById(R.id.dashboard_view_pager);
        mPager.setAdapter(mPageAdapter);
        TabLayout tabLayout = view.findViewById(R.id.area_tabs);
        tabLayout.setupWithViewPager(mPager, true);

        // Sets the new page button click listener
        ImageButton newPageButton = view.findViewById(R.id.add_new_page_button);
        newPageButton.setOnClickListener(this);

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
                    final Profile profile = getActiveProfile();
                    Page page = new Page(profile.getProfileId(), profile.getDefaultMessageId(), name);
                    mPageModel.createPage(page, new PageModel.CreatePageCallback() {
                        @Override
                        public void onCreate(DetailedPage page) {
                            mPageAdapter.addPage(page);
                        }
                    });
                }
                break;

            case NEW_PAGE_ITEM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    PageItem item = data.getExtras().getParcelable(PageItem.EXTRA);

                    if (item != null) {
                        mPageModel.newPageItem(item);
                    }
                }
                break;

            default :
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Get's the current selected page
     * @return  the current page
     */
    public Page getCurrentPage() {
        return mPageAdapter.getPage(mPager.getCurrentItem());
    }


}
