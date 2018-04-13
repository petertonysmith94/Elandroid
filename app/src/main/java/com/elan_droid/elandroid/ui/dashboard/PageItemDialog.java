package com.elan_droid.elandroid.ui.dashboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.entity.Page;
import com.elan_droid.elandroid.database.entity.PageItem;
import com.elan_droid.elandroid.database.embedded.Position;
import com.elan_droid.elandroid.database.embedded.Size;

import java.util.Random;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Peter Smith
 */

public class PageItemDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private Page mPage;

    private EditText mName;

    public static DialogFragment getInstance(Page page) {
        Bundle params = new Bundle();
        params.putParcelable(Page.EXTRA, page);

        DialogFragment fragment = new PageItemDialog();
        fragment.setArguments(params);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handle argument first!
        if (handleBundle(getArguments())) {

        }
        else if (handleBundle(savedInstanceState)) {

        }
        // Todo: fail we don't have a page
        else {

        }


    }

    private boolean handleBundle(Bundle bundle) {
        if (bundle != null) {
            mPage = bundle.getParcelable(Page.EXTRA);
            return true;
        }
        return false;
    }

    private View createView (LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.dashboard_add_widget, container);

        mName = (EditText) view.findViewById(R.id.dashboard_add_widget_name);


        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Add new widget");
        builder.setPositiveButton("Create", this);
        builder.setNegativeButton("Cancel", this);

        View view = createView(getActivity().getLayoutInflater(), null);
        builder.setView(view);

        return builder.create();
    }




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    // TODO: add error focuses on fields
    private PageItem parse() {
        PageItem item = new PageItem();
        boolean error = false;

        item.setName(mName.getText().toString());
        if (item.getName() == "") {
            error = true;
        }

        Random random = new Random();

        item.setPosition(new Position(random.nextInt(300), random.nextInt(500)));
        item.setSize(new Size(random.nextInt(100), random.nextInt(100)));

        item.setPageId(mPage.getId());

        return error ? null : item;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                PageItem item = parse();

                Intent intent = getActivity().getIntent();
                intent.putExtra(PageItem.EXTRA, item);
                getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                break;

            case Dialog.BUTTON_NEGATIVE:
                dismiss();
                break;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //getDialog().setTitle("Add a new page");
        //getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }
}
