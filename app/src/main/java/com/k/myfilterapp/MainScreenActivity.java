package com.k.myfilterapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.k.myfilterapp.roomDatabase.PhotoFilter;
import com.k.myfilterapp.roomDatabase.PhotoFilterViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainScreenActivity extends AppCompatActivity
{

    private static final String TAG = "MAIN_SCREEN_LOG_TAG";
    ImageView inputImage;
    RecyclerView recyclerView;
    PhotoFilterViewModel filterViewModel;
    FilterAdapter filterAdapter;
    Bitmap mainBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        System.loadLibrary("NativeImageProcessor");

        inputImage = findViewById(R.id.photo_to_filter);
        mainBitmap = getResizedImageBitmapFromIntent();
        inputImage.setImageBitmap(mainBitmap);

        ImageButton addButton = findViewById(R.id.btn_add_filter);
        addButton.setBackground(new ShapeDrawable(new ButtonShape()));

        initRecycleViewThings();
    }

    private Bitmap getResizedImageBitmapFromIntent()
    {
        Intent intent = getIntent();
        Uri imageUri = intent.getParcelableExtra(GalleryActivity.EXTRA_IMAGE_URI);
        Bitmap originalBitmap = uriToBitmap(imageUri);
        return resizeBitmap(originalBitmap);
    }

    private Bitmap uriToBitmap(Uri imageUri) {

        Bitmap image = null;
        try {
            image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    private Bitmap resizeBitmap(Bitmap bitmap)
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

    private Bitmap scaleBitmapTo(int destinationHeight, int destinationWidth, Bitmap bitmap)
    {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, destinationWidth, destinationHeight, false);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outStream);
        return scaledBitmap;
    }

    private void initRecycleViewThings()
    {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        filterAdapter = new FilterAdapter();
        recyclerView.setAdapter(filterAdapter);

        filterViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(PhotoFilterViewModel.class);

        filterViewModel.getAllFilters().observe(this, new Observer<List<PhotoFilter>>()
        {
            @Override
            public void onChanged(List<PhotoFilter> photoFilters)
            {
                /*Drawable drawableImage = inputImage.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) drawableImage).getBitmap();*/

                if (mainBitmap != null) {
                    for (PhotoFilter photoFilter : photoFilters) {
                        photoFilter.setFilteredBitmap(photoFilter.getFilteredBitmapFrom(mainBitmap, MainScreenActivity.this));
                    }
                }

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