package com.elan_droid.elandroid.ui.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.elan_droid.elandroid.R;

/**
 * Created by Peter Smith
 *
 *
 */
public class ProfileSelectDialog extends DialogFragment implements DialogInterface.OnClickListener {

    public static DialogFragment getInstance() {
        return new ProfileSelectDialog();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_profile_manage, null);
        //view.findViewById(R.id.profile_list_fragment);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle(R.string.profile_select_dialog_title);
        builder.setPositiveButton(R.string.profile_select_dialog_manage, this);
        builder.setNegativeButton(R.string.profile_select_dialog_cancel, this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
                dismiss();
                break;

            case Dialog.BUTTON_NEGATIVE:
                dismiss();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        FragmentManager manager = getActivity().getSupportFragmentManager();
        final Fragment fragment = manager.findFragmentById(R.id.profile_list_fragment);
        if (fragment != null) {
            manager.beginTransaction().remove(fragment).commit();
        }
    }

}
