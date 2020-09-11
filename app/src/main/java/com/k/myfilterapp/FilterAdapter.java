package com.k.myfilterapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.k.myfilterapp.roomDatabase.ChangeFiltersStateHelper;
import com.k.myfilterapp.roomDatabase.PhotoFilter;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder>
{
    private List<PhotoFilter> filters = new ArrayList<>();
    private OnItemClickListener listener;
    private ProgressBar progressBar;

    @NonNull
    @Override
    public FilterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_filter_item_view, parent, false);
        return new FilterHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterHolder holder, int position)
    {
        PhotoFilter filter = filters.get(position);
        holder.filterName.setText(filter.getFilterName());
        holder.filterPreview.setImageBitmap(filter.getFilteredBitmap());
        Log.d("ADAPTER_TAG", "onCreate: Im done!");
        progressBar.setVisibility(View.GONE);
        checkWasClicked(holder, position);

    }

    private void checkWasClicked(FilterHolder holder, int position)
    {
        PhotoFilter filter = filters.get(position);
        if(filter.isWasClicked())
        {
            ChangeFiltersStateHelper.setCurrentTextViewTypeface(holder.filterName);
        }
        else
        {
            ChangeFiltersStateHelper.removePreviousTextViewTypeface(holder.filterName);
        }
    }



    @Override
    public int getItemCount()
    {
        return filters.size();
    }

    class FilterHolder extends RecyclerView.ViewHolder
    {
        TextView filterName;
        ImageView filterPreview;

        public FilterHolder(@NonNull View itemView)
        {
            super(itemView);
            filterName = itemView.findViewById(R.id.filter_name);
            filterPreview = itemView.findViewById(R.id.image_preview);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int position = getAdapterPosition();
                    if(listener!=null && position!= RecyclerView.NO_POSITION)
                    {
                        listener.onItemClick(filters.get(position), filterName);
                    }
                }
            });
        }
    }

    public void setFilters(List<PhotoFilter> filters)
    {
        this.filters = filters;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener
    {
        void onItemClick(PhotoFilter filter, TextView filterPreview);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }

    public void setProgressBar(ProgressBar progressBar)
    {
        this.progressBar = progressBar;
    }
}
