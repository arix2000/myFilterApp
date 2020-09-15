package com.k.myfilterapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.k.myfilterapp.ButtonShape;
import com.k.myfilterapp.FilterAdapter;
import com.k.myfilterapp.R;
import com.k.myfilterapp.roomDatabase.ChangeFiltersStateHelper;
import com.k.myfilterapp.roomDatabase.PhotoFilter;
import com.k.myfilterapp.roomDatabase.PhotoFilterViewModel;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;

import java.util.List;

public class MainScreenActivity extends AppCompatActivity
{

    private static final String TAG = "MAIN_SCREEN_LOG_TAG";
    public static Bitmap mainImage; //he should not changing
    private Bitmap mainBitmap; // he can changing
    private ImageView inputImage;
    private PhotoFilterViewModel filterViewModel;
    private FilterAdapter filterAdapter;
    private ProgressBar progressBar;
    private PhotoFilter currentOnClickFilter;
    private PhotoFilter previousOnClickFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        System.loadLibrary("NativeImageProcessor");
        progressBar = findViewById(R.id.recycler_view_progress_bar);

        inputImage = findViewById(R.id.photo_to_filter);
        mainBitmap = mainImage;
        Filter filter = new Filter();
        mainBitmap = filter.processFilter(mainBitmap);
        inputImage.setImageBitmap(mainBitmap);

        filterViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(PhotoFilterViewModel.class);

        initAddButtonListener();
        initRecycleViewThings();
    }


    private void initAddButtonListener()
    {
        ImageButton addButton = findViewById(R.id.btn_add_filter);
        addButton.setBackground(new ShapeDrawable(new ButtonShape()));
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                openSendToAddActivity();
            }
        });
    }

    private void openSendToAddActivity()
    {
        Intent intent = new Intent(this, AddSetFilterActivity.class);
        startActivity(intent);
    }

    private void initRecycleViewThings()
    {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        filterAdapter = new FilterAdapter();
        filterAdapter.setProgressBar(progressBar);
        setRecycleViewSettings(filterAdapter, recyclerView);

        initFiltersObserver(filterViewModel);
        initOnItemClickListener();
    }

    private void initFiltersObserver(PhotoFilterViewModel filterViewModel)
    {
        filterViewModel.getAllFilters().observe(this, new Observer<List<PhotoFilter>>()
        {
            @Override
            public void onChanged(List<PhotoFilter> photoFilters)
            {
                for (PhotoFilter photoFilter : photoFilters) {
                    photoFilter.setFilteredBitmap(photoFilter.getFilteredBitmapFrom(mainBitmap, MainScreenActivity.this));
                }
                filterAdapter.setFilters(photoFilters);
            }
        });
    }

    private void initOnItemClickListener()
    {
        filterAdapter.setOnItemClickListener(new FilterAdapter.OnItemClickListener()
        {
            TextView previousTextView;
            @Override
            public void onItemClick(PhotoFilter filter, TextView currentTextView)
            {
                currentOnClickFilter = filter;
                chooseFilter(previousTextView,currentTextView);
                previousTextView = currentTextView;
                previousOnClickFilter = currentOnClickFilter;
            }
        });
    }

    private void setRecycleViewSettings(FilterAdapter filterAdapter, RecyclerView recyclerView)
    {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        recyclerView.setAdapter(filterAdapter);
    }

    private void chooseFilter(TextView previousTextView, TextView currentTextView)
    {
        ChangeFiltersStateHelper.removePreviousTextViewTypeface(previousTextView);
        if(previousOnClickFilter != null) previousOnClickFilter.setWasClicked(false);

        ChangeFiltersStateHelper.setCurrentTextViewTypeface(currentTextView);
        currentOnClickFilter.setWasClicked(true);

        inputImage.setImageBitmap(currentOnClickFilter.getFilteredBitmap());
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);

    }
}