package com.elan_droid.elandroid.ui.page;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.adapter.TripAdapter;
import com.elan_droid.elandroid.database.entity.Parameter;
import com.elan_droid.elandroid.database.entity.ParameterBitwise8;
import com.elan_droid.elandroid.database.entity.ParameterFormatted;
import com.elan_droid.elandroid.database.relation.DetailedPage;
import com.elan_droid.elandroid.database.view.ParameterModel;
import com.elan_droid.elandroid.ui.dashboard.BaseDashboardPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BorisJohnson on 4/21/2018.
 */

public class PacketListPage extends BasePage {

    private ParameterModel parameterModel;

    // UI variables
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private ParameterAdapter mAdapter;

    private DetailedPage mPage;


    public static Fragment getInstance(DetailedPage page) {
        Bundle params = new Bundle();
        params.putParcelable(DetailedPage.EXTRA, page);

        Fragment fragment = new PacketListPage();
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

        mAdapter = new ParameterAdapter(new ArrayList<Parameter>(), null);

        parameterModel = ViewModelProviders.of(getActivity()).get(ParameterModel.class);
        parameterModel.fetchParameters(mPage.getPage().getMessageId(), new ParameterModel.FetchParameterCallback() {
            @Override
            public void onFetch(List<Parameter> parameters) {
                updateParameters(parameters);
            }
        });
    }

    private void updateParameters (List<Parameter> parameters) {
        mAdapter.update(parameters);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parameter_list, container, false);

        mLayoutManager = new LinearLayoutManager(container.getContext());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.parameter_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }


    public static class ParameterAdapter extends RecyclerView.Adapter<ParameterAdapter.BaseViewHolder> {

        private List<Parameter> parameters;
        private View.OnClickListener listener;

        public ParameterAdapter (List<Parameter> parameters, View.OnClickListener listener) {
            this.parameters = parameters;
            this.listener = listener;
        }

        public void update (List<Parameter> parameters) {
            if (parameters != null && this.parameters != parameters) {
                this.parameters.clear();
                this.parameters.addAll(parameters);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getItemViewType(int position) {
            return parameters.get(position).getType().getValue();
        }

        @Override
        public int getItemCount() {
            return parameters.size();
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {
                case Parameter.Type.BITWISE_VALUE:
                    return new ParameterBitwise8.ListViewHolder (
                            inflater.inflate(R.layout.list_item_bitwise, null)
                    );

                case Parameter.Type.FORMATTED_VALUE:
                    return new ParameterFormatted.ListViewHolder (
                            inflater.inflate(R.layout.list_item_formatted, null)
                    );

                default:
                    return new NothingViewHolder (
                            inflater.inflate(R.layout.list_item_nothing, null)
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

            public abstract void bind(Parameter parameter, View.OnClickListener listener);

        }

        public static class NothingViewHolder extends BaseViewHolder {

            public NothingViewHolder (View itemView) {
                super (itemView);
            }

            public void bind (Parameter parameter, View.OnClickListener listener) {

            }

        }



    }


}
