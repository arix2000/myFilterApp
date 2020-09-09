package com.k.myfilterapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentProvider;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.k.myfilterapp.roomDatabase.PhotoFilter;
import com.k.myfilterapp.roomDatabase.PhotoFilterViewModel;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.ColorOverlaySubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.VignetteSubFilter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    ImageView inputImage;
    RecyclerView recyclerView;
    PhotoFilterViewModel filterViewModel;
    List<PhotoFilter> localFilters = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.loadLibrary("NativeImageProcessor");

        inputImage = findViewById(R.id.input_image_view);
        Bitmap inputBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.pool_party);
        inputImage.setImageBitmap(inputBitmap);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        final FilterAdapter filterAdapter = new FilterAdapter();
        recyclerView.setAdapter(filterAdapter);

        filterViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(PhotoFilterViewModel.class);

        filterViewModel.getAllFilters().observe(this, new Observer<List<PhotoFilter>>()
        {
            @Override
            public void onChanged(List<PhotoFilter> photoFilters)
            {
                filterAdapter.setFilters(photoFilters);
            }
        });

        filterAdapter.setOnItemClickListener(new FilterAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(PhotoFilter filter)
            {
                inputImage.setImageBitmap(filter.getFilteredBitmap());
            }
        });

    }
}