package com.k.myfilterapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.k.myfilterapp.roomDatabase.PhotoFilter;

import java.util.List;
import java.util.zip.Inflater;

public class FiltersAdapter extends RecyclerView.Adapter<FiltersAdapter.FilterHolder>
{
    private List<PhotoFilter> filters;
    private Context context;

    @NonNull
    @Override
    public FilterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_filter_item_view, parent, false);

        context = parent.getContext();

        return new FilterHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterHolder holder, int position)
    {
        PhotoFilter filter = filters.get(position);
        holder.filterName.setText(filter.getFilterName());
        Drawable photo = holder.filterPreview.getDrawable();
        holder.filterPreview.setImageBitmap(filter.getFilteredBitmapFrom(((BitmapDrawable)photo).getBitmap(),context));

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
        }
    }
}
