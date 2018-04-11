package com.elan_droid.elandroid.ui.dialog;


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

import static android.app.Activity.RESULT_OK;

/**
 * Created by Peter Smith
 */

public class AddPageDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final int ADD_NEW_PAGE = 0;
    private static final int EDIT_NEW_PAGE = 1;

    public static final String PAGE_NAME_EXTRA = "com.elan_droid.elandroid.ui.dialog.AddPageDialog";

    private EditText mNameText;

    public static DialogFragment getInstance() {
        return new AddPageDialog();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_dashboard_add_page, null);
        mNameText = (EditText) view.findViewById(R.id.dialog_dashboard_add_page_name);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle("Create a new page");
        builder.setPositiveButton("Create", this);
        builder.setNegativeButton("Cancel", this);

        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                Intent intent = getActivity().getIntent();
                intent.putExtra(PAGE_NAME_EXTRA, mNameText.getText().toString());
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
