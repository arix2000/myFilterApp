package com.k.myfilterapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.k.myfilterapp.ButtonShape;
import com.k.myfilterapp.R;
import com.k.myfilterapp.SeekBarOptions;
import com.k.myfilterapp.roomDatabase.PhotoFilter;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.SubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ColorOverlaySubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.VignetteSubFilter;

import java.util.ArrayList;
import java.util.List;

public class AddSetFilterActivity extends AppCompatActivity
{
    public static final int CONTRAST_POSITION = 0;
    public static final int COLOR_OVERLAY_POSITION = 2;
    public static final int BRIGHTNESS_POSITION = 1;

    public static final String EXTRA_BRIGHTNESS = "com.k.myfilterapp.activities.EXTRA_BRIGHTNESS";
    public static final String EXTRA_CONTRAST = "com.k.myfilterapp.activities.EXTRA_CONTRAST";
    public static final String EXTRA_DEPTH_INT = "com.k.myfilterapp.activities.EXTRA_DEPTH_INT";
    public static final String EXTRA_RED_FLOAT = "com.k.myfilterapp.activities.EXTRA_RED_FLOAT";
    public static final String EXTRA_GREEN_FLOAT = "com.k.myfilterapp.activities.EXTRA_GREEN_FLOAT";
    public static final String EXTRA_BLUE_FLOAT = "com.k.myfilterapp.activities.EXTRA_BLUE_FLOAT";
    public static PhotoFilter filterToEdit;

    Bitmap previewBitmap;
    private Bitmap mainBitmap = MainScreenActivity.mainImage;
    ImageView preview;
    SeekBar brightness, contrast, depth, red, green, blue;
    TextView brightnessValue, contrastValue, depthValue, redValue, greenValue, blueValue;
    BrightnessSubFilter brightnessSubFilter;
    ContrastSubFilter contrastSubFilter;
    ColorOverlaySubFilter colorOverlaySubFilter;
    Filter filter;
    Button btnVignetteAndSaturation;
    List<SubFilter> subFilters;
    float redFloat, greenFloat, blueFloat;
    int depthInt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_set_filter);

        preview = findViewById(R.id.add_filter_preview);
        previewBitmap = Bitmap.createBitmap(mainBitmap);
        btnVignetteAndSaturation = findViewById(R.id.btn_vignette_and_saturation);

        initColorOverlayValues();

        filter = new Filter();

        initBtnOnClickListener();
        initSubFilters();
        initSeekBarAndCounters();

        preview.setImageBitmap(mainBitmap);
        if (filterToEdit != null) {
            restoreFilterProperties();
        }

        setBrightnessListener();
        setContrastListener();
        setDepthListener();
        setRGBListener();
    }


    private void initColorOverlayValues()
    {
        redFloat = 0;
        greenFloat = 0;
        blueFloat = 0;
        depthInt = 0;
    }

    private void initBtnOnClickListener()
    {
        btnVignetteAndSaturation.setBackground(new ShapeDrawable(new ButtonShape()));
        btnVignetteAndSaturation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                openNextActivity();
            }
        });
    }

    private void openNextActivity()
    {
        VignetteAndSaturationActivity.mainImage = Bitmap.createBitmap(previewBitmap);
        Intent intent = new Intent(this, VignetteAndSaturationActivity.class);
        Bundle bundle = createBundleFromFiltersValue();
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private Bundle createBundleFromFiltersValue()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_BRIGHTNESS, brightnessSubFilter.getBrightness());
        bundle.putInt(EXTRA_DEPTH_INT, depthInt);
        bundle.putFloat(EXTRA_CONTRAST, contrastSubFilter.getContrast());
        bundle.putFloat(EXTRA_RED_FLOAT, redFloat);
        bundle.putFloat(EXTRA_GREEN_FLOAT, greenFloat);
        bundle.putFloat(EXTRA_BLUE_FLOAT, blueFloat);
        return bundle;
    }

    private void initSubFilters()
    {
        brightnessSubFilter = new BrightnessSubFilter(0);
        contrastSubFilter = new ContrastSubFilter(1);
        colorOverlaySubFilter = new ColorOverlaySubFilter(0, 0f, 0f, 0f);

        subFilters = new ArrayList<>();
        subFilters.add(contrastSubFilter);
        subFilters.add(brightnessSubFilter);
        subFilters.add(colorOverlaySubFilter);

    }

    private void initSeekBarAndCounters()
    {
        brightness = findViewById(R.id.brightness_seek_bar);
        brightnessValue = findViewById(R.id.brightness_value);
        setSeekBar(brightness, new SeekBarOptions(-100, 0, 100));

        contrast = findViewById(R.id.contrast_seek_bar);
        contrastValue = findViewById(R.id.contrast_value);
        setSeekBar(contrast, new SeekBarOptions(20, 100, 250));

        depth = findViewById(R.id.color_depth_seek_bar);
        depthValue = findViewById(R.id.color_depth_value);
        setSeekBar(depth, new SeekBarOptions(0, 0, 255));

        red = findViewById(R.id.red_seek_bar);
        redValue = findViewById(R.id.red_value);
        setSeekBar(red, new SeekBarOptions(0, 0, 100));

        green = findViewById(R.id.green_seek_bar);
        greenValue = findViewById(R.id.green_value);
        setSeekBar(green, new SeekBarOptions(0, 0, 100));

        blue = findViewById(R.id.blue_seek_bar);
        blueValue = findViewById(R.id.blue_value);
        setSeekBar(blue, new SeekBarOptions(0, 0, 100));
    }

    private void setSeekBar(SeekBar seekBar, SeekBarOptions seekBarOptions)
    {
        seekBar.setMin(seekBarOptions.getMin());
        seekBar.setProgress(seekBarOptions.getDefaultProgress());
        seekBar.setMax(seekBarOptions.getMax());
    }

    private void restoreFilterProperties()
    {
        PhotoFilter filter = filterToEdit;
        previewBitmap = Bitmap.createBitmap(mainBitmap);

        int brightnessInt = filter.getBrightness();
        float contrastFloat = filter.getContrast();
        int colorDepth = filter.getColorDepth();
        float red = filter.getRed();
        float green = filter.getGreen();
        float blue = filter.getBlue();

        depthInt = colorDepth;
        redFloat = red;
        greenFloat = green;
        blueFloat = blue;
        brightnessSubFilter.setBrightness(brightnessInt);
        contrastSubFilter.setContrast(contrastFloat);
        colorOverlaySubFilter = new ColorOverlaySubFilter(colorDepth, red, green, blue);

        subFilters.clear();
        subFilters.add(contrastSubFilter);
        subFilters.add(brightnessSubFilter);
        subFilters.add(colorOverlaySubFilter);

        brightness.setProgress(brightnessInt);
        brightnessValue.setText(String.valueOf(brightnessInt));
        contrast.setProgress((int) (contrastFloat*100));
        contrastValue.setText(String.valueOf(contrastFloat));
        depth.setProgress(colorDepth);
        depthValue.setText(String.valueOf(colorDepth));
        this.red.setProgress((int) (red*100));
        redValue.setText(String.valueOf(red));
        this.green.setProgress((int) (green*100));
        greenValue.setText(String.valueOf(green));
        this.blue.setProgress((int) (blue*100));
        blueValue.setText(String.valueOf(blue));

        this.filter.addSubFilters(subFilters);
        previewBitmap = this.filter.processFilter(previewBitmap);
        preview.setImageBitmap(previewBitmap);
        this.filter.clearSubFilters();


    }

    private void setBrightnessListener()
    {
        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                brightnessValue.setText(String.valueOf(i));
                setBrightnessOnPreview(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });
    }

    private void setBrightnessOnPreview(int i)
    {
        brightnessSubFilter.setBrightness(i);
        subFilters.set(BRIGHTNESS_POSITION, brightnessSubFilter);
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

    private void setContrastListener()
    {
        contrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                float value = convertHundredToFloatFrom(i);
                contrastValue.setText(String.valueOf(value));
                setContrastOnPreview(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });
    }

    private void setContrastOnPreview(float value)
    {
        contrastSubFilter.setContrast(value / 100);
        subFilters.set(CONTRAST_POSITION, contrastSubFilter);
        applyFilterOnMainBitmap();
    }

    protected float convertHundredToFloatFrom(int value)
    {
        float converted = (float) value;
        return converted / 100;
    }

    private void setDepthListener()
    {
        depth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                depthValue.setText(String.valueOf(i));
                setDepthOnPreview(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });
    }

    private void setDepthOnPreview(int i)
    {
        colorOverlaySubFilter = new ColorOverlaySubFilter(i, redFloat, greenFloat, blueFloat);
        subFilters.set(COLOR_OVERLAY_POSITION, colorOverlaySubFilter);
        applyFilterOnMainBitmap();
        depthInt = i;
    }

    private void setRGBListener()
    {
        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                float value = convertHundredToFloatFrom(i);
                chooseSeekBar(value, seekBar.getId());
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

        red.setOnSeekBarChangeListener(listener);
        green.setOnSeekBarChangeListener(listener);
        blue.setOnSeekBarChangeListener(listener);
    }

    private void chooseSeekBar(float value, int id)
    {
        switch (id) {
            case R.id.red_seek_bar:
                redSeekBarChange(value);
                break;
            case R.id.green_seek_bar:
                greenSeekBarChange(value);
                break;
            case R.id.blue_seek_bar:
                blueSeekBarChange(value);
                break;
        }
    }

    private void redSeekBarChange(float value)
    {
        redValue.setText(String.valueOf(value));

        colorOverlaySubFilter = new ColorOverlaySubFilter(depthInt, value, greenFloat, blueFloat);
        subFilters.set(COLOR_OVERLAY_POSITION, colorOverlaySubFilter);
        applyFilterOnMainBitmap();
        redFloat = value;
    }

    private void greenSeekBarChange(float value)
    {
        greenValue.setText(String.valueOf(value));

        colorOverlaySubFilter = new ColorOverlaySubFilter(depthInt, redFloat, value, blueFloat);
        subFilters.set(COLOR_OVERLAY_POSITION, colorOverlaySubFilter);
        applyFilterOnMainBitmap();
        greenFloat = value;
    }

    private void blueSeekBarChange(float value)
    {
        blueValue.setText(String.valueOf(value));

        colorOverlaySubFilter = new ColorOverlaySubFilter(depthInt, redFloat, greenFloat, value);
        subFilters.set(COLOR_OVERLAY_POSITION, colorOverlaySubFilter);
        applyFilterOnMainBitmap();
        blueFloat = value;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        filterToEdit = null;
    }
}