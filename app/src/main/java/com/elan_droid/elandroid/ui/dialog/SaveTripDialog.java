package com.elan_droid.elandroid.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.service.BaseService;
import com.elan_droid.elandroid.ui.generic.BaseActivity;
import com.elan_droid.elandroid.ui.generic.ServiceActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by BorisJohnson on 4/23/2018.
 */

public class SaveTripDialog extends DialogFragment implements DialogInterface.OnClickListener {

    public static final int RESULT_SAVE_TRIP = 0;
    public static final int RESULT_DELETE_TRIP = 1;

    private EditText mNameText;

    public static final String EXTRA_TRIP_NAME = "com.elan_droid.elandroid.ui.dialog.EXTRA_TRIP_NAME";

    public static DialogFragment getInstance() {
        return new SaveTripDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_dashboard_add_page, null);
        mNameText = (EditText) view.findViewById(R.id.dialog_dashboard_add_page_name);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle("Create a new page");
        builder.setPositiveButton("Create", this);
        builder.setNeutralButton("No", this);
        builder.setNegativeButton("Cancel", this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        Intent intent;

        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                ((ServiceActivity)getActivity()).saveTrip (mNameText.getText().toString());
                break;

            case Dialog.BUTTON_NEUTRAL:
                ((ServiceActivity)getActivity()).deleteTrip ();
                break;

            case Dialog.BUTTON_NEGATIVE:
                dismiss();
                break;
        }
    }

}
