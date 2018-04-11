package com.elan_droid.elandroid.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.ui.profile.ProfileActivity;

/**
 * Created by Peter Smith
 */

public class ProfileSelectDialog extends DialogFragment implements DialogInterface.OnClickListener {


    private Button mActionButton;


    public static DialogFragment instance() {
        DialogFragment dialog = new ProfileSelectDialog();
        return dialog;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_profile_manage, null);
        //view.findViewById(R.id.profile_list_fragment);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle("Select a profile");
        builder.setPositiveButton("Manage", this);
        builder.setNegativeButton("Cancel", this);

        return builder.create();
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
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
