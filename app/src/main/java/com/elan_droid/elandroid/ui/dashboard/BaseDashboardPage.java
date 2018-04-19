package com.elan_droid.elandroid.ui.dashboard;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.entity.PageItem;
import com.elan_droid.elandroid.database.embedded.Position;
import com.elan_droid.elandroid.database.embedded.Size;
import com.elan_droid.elandroid.database.relation.DetailedPage;

import java.util.List;

/**
 * Created by Peter Smith
 */

public class BaseDashboardPage extends Fragment implements View.OnTouchListener, View.OnLongClickListener {

    private DetailedPage mPage;

    private FrameLayout mLayout;

    private int deltaX, deltaY;

    public static Fragment getInstance(DetailedPage page) {
        Bundle params = new Bundle();
        params.putParcelable(DetailedPage.EXTRA, page);

        Fragment fragment = new BaseDashboardPage();
        fragment.setArguments(params);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!handleBundle(savedInstanceState)) {
            handleBundle(getArguments());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_relative_page, container, false);

        mLayout = (FrameLayout) view.findViewById(R.id.dashboard_page_root);
        mLayout.removeAllViews();

        parseItems(mPage.getItems());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        for (PageItem item : mPage.getItems()) {
            View view = mLayout.findViewWithTag(item);

            if (view != null) {
                mLayout.removeView(view);
            }
        }
    }

    public boolean handleBundle(final Bundle bundle) {
        final boolean condition = (bundle != null);

        if (condition) {
            mPage = bundle.getParcelable(DetailedPage.EXTRA);
        }
        return condition;
    }

    public void parseItems (List<PageItem> items) {
        if (items != null && items.size() > 0) {
            FrameLayout.LayoutParams params;
            Context context = mLayout.getContext();

            for (PageItem item : items) {
                //Create view
                Button view = new Button(context);
                view.setBackgroundColor(Color.RED);
                view.setOnTouchListener(this);
                view.setText(item.getName());
                view.setTag(item);

                //Parses the layout and position information
                params = parseSize(item.getSize());
                parsePosition(params, item.getPosition());

                mLayout.addView(view, params);
            }
        }
    }



    private FrameLayout.LayoutParams parseSize (final Size size) {
        return new FrameLayout.LayoutParams(size.getWidth(), size.getHeight());
    }

    private void parsePosition (FrameLayout.LayoutParams params, Position position) {
        params.leftMargin = position.getX();
        params.topMargin = position.getY();
    }

    @Override
    public boolean onLongClick(View view) {
        //view.setOnTouchListener(this);
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        FrameLayout.LayoutParams params;
        final int x = (int) e.getRawX();
        final int y = (int) e.getRawY();
        Log.d("onTouch", String.format("Coords: X %d, Y %d", x, y));

        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                params = (FrameLayout.LayoutParams) v.getLayoutParams();
                deltaX = x - params.leftMargin;
                deltaY = y - params.topMargin;
                Log.d("onTouch", String.format("ACTION_DOWN: deltaX %d, deltaY %d", deltaX, deltaY));
                break;

            case MotionEvent.ACTION_MOVE:
                params = (FrameLayout.LayoutParams) v.getLayoutParams();
                params.leftMargin = x - deltaX;
                params.topMargin = y - deltaY;
                Log.d("onTouch", String.format("ACTION_MOVE: leftMargin %d, topMargin %d", params.leftMargin, params.topMargin));
                //params.rightMargin = -250;
                //params.bottomMargin = -250
                v.setLayoutParams(params);
                break;

            case MotionEvent.ACTION_UP:
                final PageItem item = (PageItem) v.getTag();

                params = (FrameLayout.LayoutParams) v.getLayoutParams();

                Position position = item.getPosition();
                position.setX(params.leftMargin);
                position.setY(params.topMargin);

                mPage.setItemPosition(item, position);

                //v.setOnTouchListener(null);
                break;

            default:
                return false;
        }
        v.invalidate();
        return true;
    }



}
