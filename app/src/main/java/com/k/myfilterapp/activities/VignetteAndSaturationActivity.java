package com.k.myfilterapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.k.myfilterapp.R;
import com.k.myfilterapp.SeekBarOptions;
import com.k.myfilterapp.roomDatabase.PhotoFilter;
import com.k.myfilterapp.roomDatabase.PhotoFilterViewModel;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.SubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ColorOverlaySubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.VignetteSubFilter;

import java.util.ArrayList;
import java.util.List;

public class VignetteAndSaturationActivity extends AppCompatActivity
{
    public static final int SATURATION_POSITION = 0;
    public static final int VIGNETTE_POSITION = 1;

    public static Bitmap mainImage;
    private Bitmap previewBitmap;
    private Bitmap mainBitmap;
    private ImageView preview;
    private SeekBar saturation, vignette;
    private TextView saturationValue, vignetteValue;
    private List<SubFilter> subFilters;
    private SaturationSubFilter saturationSubFilter;
    private VignetteSubFilter vignetteSubFilter;
    private Filter filter;
    private PhotoFilterViewModel viewModel;
    private Bundle previousFilterInfo;
    private EditText filterName;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vignette_and_saturation);

        preview = findViewById(R.id.vignette_and_saturation_preview);
        filter = new Filter();
        mainBitmap = Bitmap.createBitmap(mainImage);
        preview.setImageBitmap(mainBitmap);
        viewModel = new PhotoFilterViewModel(getApplication());
        filterName = findViewById(R.id.created_filter_name);

        setSaveBtnListener();
        initSubFilters();
        initSeekBarAndCounter();
        if (AddSetFilterActivity.filterToEdit != null) {
            restoreFilterProperties();
        }
        setListener();
    }

    private void setSaveBtnListener()
    {
        saveBtn = findViewById(R.id.btn_save_filter);
        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                decideAndSaveFilter();
            }
        });
    }

    private void decideAndSaveFilter()
    {
        if (filterName.getText().toString().trim().isEmpty()) {
            TextView nameFilterTextView = findViewById(R.id.created_filter_name_text_view);
            Toast.makeText(this, "Pole z nazwą musi być wypełnione!", Toast.LENGTH_SHORT).show();
            filterName.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            nameFilterTextView.setTextColor(Color.RED);
        } else {
            saveFilter();
        }

    }

    private void saveFilter()
    {
        Bundle bundle = getIntent().getExtras();
        int brightness = bundle.getInt(AddSetFilterActivity.EXTRA_BRIGHTNESS, 0);
        int vignette = Integer.parseInt(vignetteValue.getText().toString());
        int colorDepth = bundle.getInt(AddSetFilterActivity.EXTRA_DEPTH_INT, 0);
        float red = bundle.getFloat(AddSetFilterActivity.EXTRA_RED_FLOAT, 0);
        float green = bundle.getFloat(AddSetFilterActivity.EXTRA_GREEN_FLOAT, 0);
        float blue = bundle.getFloat(AddSetFilterActivity.EXTRA_BLUE_FLOAT, 0);
        float saturation = Float.parseFloat(saturationValue.getText().toString());
        float contrast = bundle.getFloat(AddSetFilterActivity.EXTRA_CONTRAST, 0);

        PhotoFilter createdFilter = new PhotoFilter(filterName.getText().toString(),
                brightness, vignette, colorDepth, red, green, blue, saturation, contrast);

        if (AddSetFilterActivity.filterToEdit != null) {
            int id = AddSetFilterActivity.filterToEdit.getId();
            createdFilter.setId(id);
            viewModel.update(createdFilter);
        } else viewModel.insert(createdFilter);

        backToMainScreen();
        AddSetFilterActivity.filterToEdit = null;
    }

    private void backToMainScreen()
    {
        Intent intent = new Intent(this, MainScreenActivity.class);
        startActivity(intent);
    }

    private void initSubFilters()
    {
        subFilters = new ArrayList<>();
        saturationSubFilter = new SaturationSubFilter(1);
        vignetteSubFilter = new VignetteSubFilter(this, 0);

        fillSubFiltersBy(saturationSubFilter, vignetteSubFilter);
    }

    private void fillSubFiltersBy(SaturationSubFilter saturationSubFilter, VignetteSubFilter vignetteSubFilter)
    {
        subFilters.clear();
        subFilters.add(saturationSubFilter);
        subFilters.add(vignetteSubFilter);
    }

    private void initSeekBarAndCounter()
    {
        saturation = findViewById(R.id.saturation_seek_bar);
        saturationValue = findViewById(R.id.saturation_value);
        setSeekBar(saturation, new SeekBarOptions(20, 100, 220));

        vignette = findViewById(R.id.vignette_seek_bar);
        vignetteValue = findViewById(R.id.vignette_value);
        setSeekBar(vignette, new SeekBarOptions(0, 0, 255));
    }

    private void setSeekBar(SeekBar seekBar, SeekBarOptions seekBarOptions)
    {
        seekBar.setMin(seekBarOptions.getMin());
        seekBar.setProgress(seekBarOptions.getDefaultProgress());
        seekBar.setMax(seekBarOptions.getMax());
    }

    private void restoreFilterProperties()
    {
        PhotoFilter filter = AddSetFilterActivity.filterToEdit;
        previewBitmap = Bitmap.createBitmap(mainBitmap);
        filterName.setText(filter.getFilterName());
        saveBtn.setText("edytuj filtr");

        float saturationFloat = filter.getSaturation();
        int vignetteInt = filter.getVignetteAlpha();
        vignetteSubFilter.setAlpha(vignetteInt);
        saturationSubFilter.setLevel(saturationFloat);

        fillSubFiltersBy(saturationSubFilter, vignetteSubFilter);

        vignette.setProgress(vignetteInt);
        vignetteValue.setText(String.valueOf(vignetteInt));
        saturation.setProgress((int) (saturationFloat * 100));
        saturationValue.setText(String.valueOf(saturationFloat));

        this.filter.addSubFilters(subFilters);
        previewBitmap = this.filter.processFilter(previewBitmap);
        preview.setImageBitmap(previewBitmap);
        this.filter.clearSubFilters();
    }

    private void setListener()
    {
        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener()
        {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                chooseSeekBar(i, seekBar.getId());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        };

        saturation.setOnSeekBarChangeListener(listener);
        vignette.setOnSeekBarChangeListener(listener);

    }

    private void chooseSeekBar(int i, int id)
    {
        switch (id) {
            case R.id.saturation_seek_bar:
                saturationChange(convertHundredToFloatFrom(i));
                break;
            case R.id.vignette_seek_bar:
                vignetteChange(i);
                break;
        }
    }

    private void saturationChange(float value)
    {
        saturationValue.setText(String.valueOf(value));

        saturationSubFilter.setLevel(value);
        subFilters.set(SATURATION_POSITION, saturationSubFilter);
        applyFilterOnMainBitmap();
    }

    private void vignetteChange(int i)
    {
        vignetteValue.setText(String.valueOf(i));

        vignetteSubFilter.setAlpha(i);
        subFilters.set(VIGNETTE_POSITION, vignetteSubFilter);
        applyFilterOnMainBitmap();
    }

    private void applyFilterOnMainBitmap()
    {
        previewBitmap = Bitmap.createBitmap(mainBitmap);
        filter.addSubFilters(subFilters);
        previewBitmap = filter.processFilter(previewBitmap);
        filter.clearSubFilters();
        preview.setImageBitmap(previewBitmap);
    }

    protected float convertHundredToFloatFrom(int value)
    {
        float converted = (float) value;
        return converted / 100;
    }
}