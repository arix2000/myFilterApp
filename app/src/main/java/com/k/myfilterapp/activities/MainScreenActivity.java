package com.k.myfilterapp.activities;

import androidx.annotation.LongDef;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.k.myfilterapp.ButtonShape;
import com.k.myfilterapp.FilterAdapter;
import com.k.myfilterapp.R;
import com.k.myfilterapp.roomDatabase.ChangeFiltersStateHelper;
import com.k.myfilterapp.roomDatabase.PhotoFilter;
import com.k.myfilterapp.roomDatabase.PhotoFilterViewModel;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;

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

        initSaveButtonListener();
        initAddButtonListener();
        initRecycleViewThings();
    }

    private void initSaveButtonListener()
    {
        ImageButton button = findViewById(R.id.btn_save_filter);
        button.setBackground(new ShapeDrawable(new ButtonShape()));
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                saveImageToDevice();
            }
        });
    }

    private void saveImageToDevice()
    {
        // mainBitmap <--- bitmap to save
        ContentResolver resolver = getApplicationContext().getContentResolver();
        ContentValues values = createContentValues();
        Uri url = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        addImageToMediaStore(resolver, url);

    }

    private ContentValues createContentValues()
    {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "image" + SystemClock.elapsedRealtime());
        values.put(MediaStore.Images.Media.DESCRIPTION, "savedImage");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values;
    }

    private void addImageToMediaStore(ContentResolver resolver, @Nullable Uri url)
    {
        try {
            if(url != null) {
                OutputStream imageOut = resolver.openOutputStream(url);
                mainBitmap.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
                imageOut.close();
                Toast.makeText(this, "Zdjęcie zostało zapisane!", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(this, "Nie udało się zapisać zdjęcia :(", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.d(TAG, "something wrong: " + e.getMessage());
            Toast.makeText(this, "Nie udało się zapisać zdjęcia :(", Toast.LENGTH_SHORT).show();
        }
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
        initOnLongItemClickListener();
    }

    private void setRecycleViewSettings(FilterAdapter filterAdapter, RecyclerView recyclerView)
    {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        recyclerView.setAdapter(filterAdapter);
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
                chooseFilter(previousTextView, currentTextView);
                previousTextView = currentTextView;
                previousOnClickFilter = currentOnClickFilter;
            }
        });
    }

    private void chooseFilter(TextView previousTextView, TextView currentTextView)
    {
        ChangeFiltersStateHelper.removePreviousTextViewTypeface(previousTextView);
        if (previousOnClickFilter != null) previousOnClickFilter.setWasClicked(false);

        ChangeFiltersStateHelper.setCurrentTextViewTypeface(currentTextView);
        currentOnClickFilter.setWasClicked(true);

        mainBitmap = currentOnClickFilter.getFilteredBitmap();
        inputImage.setImageBitmap(mainBitmap);
    }

    private void initOnLongItemClickListener()
    {
        filterAdapter.setOnLongItemClickListener(new FilterAdapter.OnLongItemClickListener()
        {
            @Override
            public void onLongItemClick(PhotoFilter filter)
            {
                showDialogToChoose(filter);
            }
        });
    }

    private void showDialogToChoose(PhotoFilter filter)
    {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_delete);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        initImageViewAndFilterName(dialog, filter);
        initContainer(dialog);
        initEditBtnListener(dialog, filter);
        initDeleteBtnListener(dialog, filter);

    }

    private void initImageViewAndFilterName(Dialog dialog, PhotoFilter filter)
    {
        ImageView imageView = dialog.findViewById(R.id.dialog_image_preview);
        imageView.setImageBitmap(filter.getFilteredBitmap());
        TextView textView = dialog.findViewById(R.id.dialog_filter_name);
        textView.setText(filter.getFilterName());
    }

    private void initEditBtnListener(Dialog dialog, PhotoFilter filter)
    {
        Button btnEdit = dialog.findViewById(R.id.btn_edit_filter);
        btnEdit.setBackground(new ShapeDrawable(new ButtonShape()));
        btnEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
                makeEdit(filter);
            }
        });
    }

    private void makeEdit(PhotoFilter filter)
    {
        Intent intent = new Intent(this, AddSetFilterActivity.class);
        AddSetFilterActivity.filterToEdit = filter;
        startActivity(intent);
    }

    private void initDeleteBtnListener(Dialog dialog, PhotoFilter filter)
    {
        Button btnDelete = dialog.findViewById(R.id.btn_delete_filter);
        btnDelete.setBackground(new ShapeDrawable(new ButtonShape()));
        btnDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
                makeDelete(filter);
            }
        });
    }

    private void makeDelete(PhotoFilter filter)
    {
        filterViewModel.delete(filter);
        Toast.makeText(this, "Usunięto element", Toast.LENGTH_SHORT).show();
    }

    private void initContainer(Dialog dialog)
    {
        ButtonShape buttonShapeForDialogBg = new ButtonShape();
        buttonShapeForDialogBg.setColour(Color.parseColor("#3E3E3E"));
        RelativeLayout relativeLayout = dialog.findViewById(R.id.dialog_container);
        relativeLayout.setBackground(new ShapeDrawable(buttonShapeForDialogBg));
    }


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);

    }
}