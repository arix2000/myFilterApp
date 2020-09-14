package com.k.myfilterapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.k.myfilterapp.R;
import com.k.myfilterapp.SeekBarOptions;
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
    public static final int VIGNETTE_POSITION = 3;


    Bitmap previewBitmap, bufferBitmap;
    private Bitmap mainBitmap = MainScreenActivity.mainImage;
    ImageView preview;
    SeekBar brightness, contrast, depth, red, green, blue;
    TextView brightnessValue, contrastValue, depthValue, redValue, greenValue, blueValue;
    BrightnessSubFilter brightnessSubFilter;
    ContrastSubFilter contrastSubFilter;
    VignetteSubFilter vignetteSubFilter;
    ColorOverlaySubFilter colorOverlaySubFilter;
    Filter filter;
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

        redFloat = 0; greenFloat = 0; blueFloat = 0; depthInt = 0;


        filter = new Filter();
        InitSubFilters();

        preview.setImageBitmap(mainBitmap);

        setSeekBarAndCounters();

        setBrightnessListener();
        setContrastListener();
        setDepthListener();
        setRGBListener();
    }

    private void InitSubFilters()
    {
        brightnessSubFilter = new BrightnessSubFilter(0);
        contrastSubFilter = new ContrastSubFilter(1);


        vignetteSubFilter = new VignetteSubFilter(this, 0);
        colorOverlaySubFilter = new ColorOverlaySubFilter(0, 0f, 0f, 0f);


        subFilters = new ArrayList<>();
        subFilters.add(contrastSubFilter);
        subFilters.add(brightnessSubFilter);
        subFilters.add(colorOverlaySubFilter);

    }

    private void setSeekBarAndCounters()
    {
        brightness = findViewById(R.id.brightness_seek_bar);
        brightnessValue = findViewById(R.id.brightness_value);
        setSeekBar(brightness, new SeekBarOptions(-100, 0, 100));

        contrast = findViewById(R.id.contrast_seek_bar);
        contrastValue = findViewById(R.id.contrast_value);
        setSeekBar(contrast, new SeekBarOptions(20, 100, 220));
        /*saturation = findViewById(R.id.saturation_seek_bar);
        saturationValue = findViewById(R.id.saturation_value);
        setSeekBar(saturation, new SeekBarOptions(20, 100, 220));*/

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
                brightnessSubFilter.setBrightness(Integer.parseInt(brightnessValue.getText().toString()));
                filter.clearSubFilters();
            }
        });
    }

    private void setBrightnessOnPreview(int i)
    {
        previewBitmap = Bitmap.createBitmap(mainBitmap);
        brightnessSubFilter.setBrightness(i);
        subFilters.set(BRIGHTNESS_POSITION, brightnessSubFilter);
        filter.addSubFilters(subFilters);
        previewBitmap = filter.processFilter(previewBitmap);
        preview.setImageBitmap(previewBitmap);
        filter.clearSubFilters();
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
                contrastSubFilter.setContrast(Float.parseFloat(contrastValue.getText().toString()));
                filter.clearSubFilters();
            }
        });
    }

    private void setContrastOnPreview(float value)
    {
        previewBitmap = Bitmap.createBitmap(mainBitmap);
        contrastSubFilter.setContrast(value / 100);
        subFilters.set(CONTRAST_POSITION, contrastSubFilter);
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
        previewBitmap = Bitmap.createBitmap(mainBitmap);
        colorOverlaySubFilter = new ColorOverlaySubFilter(i, redFloat, greenFloat, blueFloat);
        subFilters.set(COLOR_OVERLAY_POSITION, colorOverlaySubFilter);
        filter.addSubFilters(subFilters);
        previewBitmap = filter.processFilter(previewBitmap);
        filter.clearSubFilters();
        preview.setImageBitmap(previewBitmap);
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

                switch (seekBar.getId()) {
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

    private void redSeekBarChange(float value)
    {
        redValue.setText(String.valueOf(value));

        previewBitmap = Bitmap.createBitmap(mainBitmap);
        colorOverlaySubFilter = new ColorOverlaySubFilter(depthInt, value, greenFloat, blueFloat);
        subFilters.set(COLOR_OVERLAY_POSITION, colorOverlaySubFilter);
        filter.addSubFilters(subFilters);
        previewBitmap = filter.processFilter(previewBitmap);
        filter.clearSubFilters();
        preview.setImageBitmap(previewBitmap);
        redFloat = value;
    }

    private void greenSeekBarChange(float value)
    {
        greenValue.setText(String.valueOf(value));

        previewBitmap = Bitmap.createBitmap(mainBitmap);
        colorOverlaySubFilter = new ColorOverlaySubFilter(depthInt, redFloat, value, blueFloat);
        subFilters.set(COLOR_OVERLAY_POSITION, colorOverlaySubFilter);
        filter.addSubFilters(subFilters);
        previewBitmap = filter.processFilter(previewBitmap);
        filter.clearSubFilters();
        preview.setImageBitmap(previewBitmap);
        greenFloat = value;
    }

    private void blueSeekBarChange(float value)
    {
        blueValue.setText(String.valueOf(value));

        previewBitmap = Bitmap.createBitmap(mainBitmap);
        colorOverlaySubFilter = new ColorOverlaySubFilter(depthInt, redFloat, greenFloat, value);
        subFilters.set(COLOR_OVERLAY_POSITION, colorOverlaySubFilter);
        filter.addSubFilters(subFilters);
        previewBitmap = filter.processFilter(previewBitmap);
        filter.clearSubFilters();
        preview.setImageBitmap(previewBitmap);
        blueFloat = value;
    }


}