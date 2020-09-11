package com.k.myfilterapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.k.myfilterapp.roomDatabase.ChangeFiltersStateHelper;
import com.k.myfilterapp.roomDatabase.PhotoFilter;
import com.k.myfilterapp.roomDatabase.PhotoFilterViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
        inputImage.setImageBitmap(mainBitmap);

        filterViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(PhotoFilterViewModel.class);

        initAddButtonListener();

        initRecycleViewThings();
    }

    /*private Bitmap getResizedImageBitmapFromIntent()
    {
        Intent intent = getIntent();
        Uri imageUri = intent.getParcelableExtra(GalleryActivity.EXTRA_IMAGE_URI);
        Bitmap originalBitmap = uriToBitmap(imageUri);
        return resizeBitmap(originalBitmap);
    }*/

    /*protected Bitmap uriToBitmap(Uri imageUri) {

        Bitmap image = null;
        try {
            image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    protected Bitmap resizeBitmap(Bitmap bitmap)
    {
        Bitmap scaledBitmap;

        int origWidth = bitmap.getWidth();
        int origHeight = bitmap.getHeight();
        int destHeight;
        int destWidth;

        if (origWidth > 3000) {
            destWidth = origWidth / 4;
            destHeight = origHeight / 4;
            scaledBitmap = scaleBitmapTo(destHeight, destWidth, bitmap);
        }
        else if(origWidth > 1000)
        {
            destHeight = origHeight / 2;
            destWidth = origWidth / 2;
            scaledBitmap = scaleBitmapTo(destHeight, destWidth, bitmap);
        }
        else
            scaledBitmap = bitmap; // we don't have to scale

        return scaledBitmap;
    }

    protected Bitmap scaleBitmapTo(int destinationHeight, int destinationWidth, Bitmap bitmap)
    {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, destinationWidth, destinationHeight, false);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outStream);
        return scaledBitmap;
    } */

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
        //TODO open add activity and Send our bitmap there
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
}