package com.elan_droid.elandroid.ui.generic;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.ui.generic.BaseFragment;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Peter Smith
 */

public abstract class BaseDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final int ADD_NEW_PAGE = 0;
    private static final int EDIT_NEW_PAGE = 1;

    public static final String CONTENT_TAG = "com.elan_droid.elandroid.ui.generic.DIALOG_CONTENT_TAG";
    public static final String INITAL_CONTENT_TAG = "com.elan_droid.elandroid.ui.generic.DIALOG_INITAL_CONTENT_TAG";

    private FrameLayout mContent;

    public abstract BaseFragment getFragment(LayoutInflater inflater);

    public void setDialogContent (BaseFragment fragment) {
        setDialogContent(fragment, null);
    }

    public void setDialogContent (BaseFragment fragment, String backStackName) {
        FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.dialog_content, fragment, CONTENT_TAG);

        if (backStackName != null) {
            transaction.addToBackStack(backStackName);
        }
        transaction.commit();
    }

    public AlertDialog.Builder setupBuilder() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_base, null);
        setDialogContent (getFragment(inflater), INITAL_CONTENT_TAG);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder;
    }

    @Override
    public abstract Dialog onCreateDialog(Bundle savedInstanceState);

}
