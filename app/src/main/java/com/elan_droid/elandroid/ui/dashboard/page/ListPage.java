package com.elan_droid.elandroid.ui.dashboard.page;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.data.entity.Flag;
import com.elan_droid.elandroid.database.data.entity.Parameter;
import com.elan_droid.elandroid.database.data.entity.ParameterBitwise8;
import com.elan_droid.elandroid.database.data.entity.ParameterFormatted;
import com.elan_droid.elandroid.database.data.entity.Trip;
import com.elan_droid.elandroid.database.data.relation.DetailedPage;
import com.elan_droid.elandroid.database.data.relation.ParameterFlag;
import com.elan_droid.elandroid.database.view_model.ParameterModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Peter Smith on 4/21/2018.
 */

public class ListPage extends BasePage {

    private ParameterModel parameterModel;

    // UI variables
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private ParameterAdapter mAdapter;

    private DetailedPage mPage;


    public static Fragment getInstance(DetailedPage page) {
        Bundle params = new Bundle();
        params.putParcelable(DetailedPage.EXTRA, page);

        Fragment fragment = new ListPage();
        fragment.setArguments(params);
        return fragment;
    }

    public boolean handleBundle(final Bundle bundle) {
        final boolean condition = (bundle != null);

        if (condition) {
            mPage = bundle.getParcelable(DetailedPage.EXTRA);
        }
        return condition;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleBundle(getArguments());

        mAdapter = new ParameterAdapter(getContext(), new ParameterList(), null);


        parameterModel = ViewModelProviders.of(getActivity()).get(ParameterModel.class);
        parameterModel.fetchParameters(mPage.getPage().getMessageId(), new ParameterModel.FetchParameterCallback() {
            @Override
            public void onFetch(List<Parameter> parameters) {
                mAdapter.updateParameters(parameters);
            }
        });
    }

    @Override
    public void startTrip(Trip trip) {
        tripModel.getLatestFlags().removeObservers(this);
        tripModel.getLatestFlags().observe(this, new Observer<List<Flag>>() {
            @Override
            public void onChanged(@Nullable List<Flag> flags) {
                mAdapter.updateFlags(flags);
            }
        });
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parameter_list, container, false);

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView = view.findViewById(R.id.parameter_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public static class ParameterList extends ArrayList<ParameterFlag> {

        private Map<Long, ParameterFlag> parameterMap;

        public ParameterList() {
            super();

            parameterMap = new HashMap<>();
        }

        public void updateParameters (@NonNull List<Parameter> parameters) {
            clear();
            parameterMap.clear();
            ParameterFlag tmp;

            for (Parameter p : parameters) {
                tmp = new ParameterFlag(p);
                add(tmp);
                parameterMap.put(p.getId(), tmp);
            }
        }

        public void updateFlags (@Nullable List<Flag> flags) {
            if (flags != null) {
                for (Flag flag : flags) {
                    if (parameterMap.containsKey(flag.getParameterId())) {
                        parameterMap.get(flag.getParameterId()).setValue(flag.toString());
                    }
                }
            }
        }

    }

    public static class ParameterAdapter extends RecyclerView.Adapter<ParameterAdapter.BaseViewHolder> {

        private Context context;
        private ParameterList parameters;
        private View.OnClickListener listener;

        public ParameterAdapter (Context context, ParameterList parameters, View.OnClickListener listener) {
            this.context = context;
            this.parameters = parameters;
            this.listener = listener;
        }

        public void updateParameters (@NonNull List<Parameter> parameters) {
            this.parameters.updateParameters(parameters);
            notifyDataSetChanged();
        }

        public void updateFlags (@NonNull List<Flag> flags) {
            this.parameters.updateFlags(flags);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return parameters.get(position).getParameter().getType().getValue();
        }

        @Override
        public int getItemCount() {
            return parameters.size();
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);

            switch (viewType) {
                case Parameter.Type.BITWISE_VALUE:
                    return new ParameterBitwise8.ListViewHolder (
                            inflater.inflate(R.layout.list_item_bitwise, parent, false)
                    );

                case Parameter.Type.FORMATTED_VALUE:
                    return new ParameterFormatted.ListViewHolder (
                            inflater.inflate(R.layout.list_item_formatted, parent, false)
                    );

                default:
                    return new NothingViewHolder (
                            inflater.inflate(R.layout.list_item_nothing, parent, false)
                    );
            }
        }

        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {
            holder.bind(parameters.get(position), listener);
        }

        public static abstract class BaseViewHolder extends RecyclerView.ViewHolder {

            public BaseViewHolder (View itemView) {
                super (itemView);
            }

            public abstract void bind(ParameterFlag parameter, View.OnClickListener listener);

        }

        public static class NothingViewHolder extends BaseViewHolder {

            public NothingViewHolder (View itemView) {
                super (itemView);
            }

            public void bind (ParameterFlag parameter, View.OnClickListener listener) {

            }

        }



    }


}
