package com.elan_droid.elandroid.ui.dashboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.entity.Page;
import com.elan_droid.elandroid.database.entity.PageItem;
import com.elan_droid.elandroid.database.embedded.Position;
import com.elan_droid.elandroid.database.embedded.Size;
import com.elan_droid.elandroid.database.entity.Parameter;
import com.elan_droid.elandroid.database.view_model.ParameterModel;
import com.elan_droid.elandroid.ui.widget.DisplaySize;
import com.elan_droid.elandroid.ui.widget.Widget;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Peter Smith
 */

public class PageItemDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private ParameterModel mParameterModel;

    private Page mPage;

    private EditText mName;

    private Spinner mParameterSpinner;
    private ArrayAdapter<Parameter> mParameterAdapter;

    private Spinner mTypeSpinner;
    private ArrayAdapter<Widget.Type> mTypeAdapter;

    private Spinner mSizeSpinner;
    private ArrayAdapter<DisplaySize> mSizeAdapter;

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

        mParameterModel = ViewModelProviders.of(getActivity()).get(ParameterModel.class);

        mParameterAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        mTypeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        mSizeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);

        // Handle argument first!
        if (handleBundle(getArguments())) {

        }
        else if (handleBundle(savedInstanceState)) {

        }
        // Todo: fail we don't have a page
        else {

        }

        mParameterModel.fetchParameters(mPage.getMessageId(), new ParameterModel.FetchParameterCallback() {
            @Override
            public void onFetch(List<Parameter> parameters) {
                mParameterAdapter.clear();
                mParameterAdapter.addAll(parameters);
            }
        });
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

        mParameterSpinner = (Spinner) view.findViewById(R.id.dashboard_add_widget_parameter);
        mParameterSpinner.setAdapter(mParameterAdapter);

        mTypeSpinner = (Spinner) view.findViewById(R.id.dashboard_add_widget_type);
        mTypeSpinner.setAdapter(mTypeAdapter);

        mSizeSpinner = (Spinner) view.findViewById(R.id.dashboard_add_widget_size);
        mSizeSpinner.setAdapter(mSizeAdapter);

        mParameterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Parameter parameter = mParameterAdapter.getItem(position);
                mName.setText(parameter.getName());

                mTypeAdapter.clear();
                mTypeAdapter.addAll(parameter.getWidgetTypes());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Widget.Type type = mTypeAdapter.getItem(position);

                mSizeAdapter.clear();
                mSizeAdapter.addAll(type.getDisplay().getSizes());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
        boolean error = false;

        String name = mName.getText().toString();
        if (name == "") {
            error = true;
        }

        Widget.Type type = mTypeAdapter.getItem(mTypeSpinner.getSelectedItemPosition());

        Parameter parameter = (Parameter) mParameterSpinner.getSelectedItem();

        Size size = new Size(mSizeAdapter.getItem(mSizeSpinner.getSelectedItemPosition()));

        return error ? null : new PageItem(mPage.getId(), parameter.getId(), name, type, new Position(0, 0), size);
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
