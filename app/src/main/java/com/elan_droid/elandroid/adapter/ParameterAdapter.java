package com.elan_droid.elandroid.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.elan_droid.elandroid.database.entity.Parameter;

import java.util.List;

/**
 * Created by Peter Smith
 */

public class ParameterAdapter extends ArrayAdapter<Parameter> {

    public ParameterAdapter(Context context, int resource, List<Parameter> objects) {
        super(context, resource, objects);
    }
}
