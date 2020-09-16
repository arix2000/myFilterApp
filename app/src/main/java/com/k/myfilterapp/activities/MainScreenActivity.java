package com.k.myfilterapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.os.Parcelable;
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

import java.util.List;
//TODO solve issue -> when we choose photo that isn't BitmapDrawable then we get error
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
                chooseFilter(previousTextView,currentTextView);
                previousTextView = currentTextView;
                previousOnClickFilter = currentOnClickFilter;
            }
        });
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
        initDeleteBtnListener(dialog,filter);

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