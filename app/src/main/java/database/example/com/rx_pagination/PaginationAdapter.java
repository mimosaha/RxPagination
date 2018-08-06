package database.example.com.rx_pagination;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import database.example.com.rx_pagination.databinding.ItemPropertyBinding;

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Mimo Saha on [02-Aug-2018 at 1:38 PM].
 * * Email: mimosaha@w3engineers.com
 * * ----------------------------------------------------------------------------
 * * Project: RxPagination.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [02-Aug-2018 at 1:38 PM].
 * * --> <Second Editor> on [02-Aug-2018 at 1:38 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [02-Aug-2018 at 1:38 PM].
 * * --> <Second Reviewer> on [02-Aug-2018 at 1:38 PM].
 * * ============================================================================
 **/
public class PaginationAdapter extends RecyclerView.Adapter<PaginationAdapter.CustomViewHolder> {

    private List<String> propertyList;

    PaginationAdapter() {
        propertyList = new ArrayList<>();
    }

    void setPropertyList(List<String> data) {
        propertyList.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPropertyBinding itemPropertyBinding = DataBindingUtil.inflate(LayoutInflater.
                from(parent.getContext()), R.layout.item_property, parent, false);
        return new CustomViewHolder(itemPropertyBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.bind(propertyList.get(position));
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private ItemPropertyBinding itemPropertyBinding;

        public CustomViewHolder(ItemPropertyBinding itemPropertyBinding) {
            super(itemPropertyBinding.getRoot());
            this.itemPropertyBinding = itemPropertyBinding;
        }

        public void bind(String data) {
            itemPropertyBinding.setProperty(data);
        }
    }
}
