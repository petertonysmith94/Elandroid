package com.elan_droid.elandroid.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

import com.elan_droid.elandroid.R;

/**
 * Created by Peter Smith on 4/23/2018.
 *
 *
 */
public class SaveTripDialog extends DialogFragment implements DialogInterface.OnClickListener {

    public static final String TAG = "SaveTripDialog";

    public static final String EXTRA_ = "";

    private OnTripActionListener mListener;
    private EditText mNameText;

    public static DialogFragment getInstance() {
        return new SaveTripDialog();
    }

    /**
     * The OnTripActionListener provides a callback to the context with user input
     */
    public interface OnTripActionListener {

        /**
         * Save the trip
         * @param tripName
         */
        void onSaveCurrentTrip(String tripName);

        /**
         * Delete the trip
         */
        void onDeleteCurrentTrip();
        
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Ensures the context has implemented the listener
        try {
            mListener = (OnTripActionListener) getActivity();
        }
        catch (ClassCastException e) {
            // For development to know what the bug is.
            throw new ClassCastException (context.toString() + "must implement SaveTripDialog.OnTripActionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_dashboard_add_page, null, false);
        mNameText = view.findViewById(R.id.dialog_dashboard_add_page_name);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.save_trip_dialog_title)

                // Note: if change, change onClick listener
                .setPositiveButton(R.string.save_trip_dialog_save, this)
                .setNegativeButton(R.string.save_trip_dialog_resume, this)
                .setNeutralButton(R.string.save_trip_dialog_delete, this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        switch (which) {
            // SAVE
            case Dialog.BUTTON_POSITIVE:
                mListener.onSaveCurrentTrip(mNameText.getText().toString());
                break;

            // RESUME
            case Dialog.BUTTON_NEGATIVE:
                dismiss();
                break;

            // DELETE
            case Dialog.BUTTON_NEUTRAL:
                mListener.onDeleteCurrentTrip();
                break;
        }
    }

}
