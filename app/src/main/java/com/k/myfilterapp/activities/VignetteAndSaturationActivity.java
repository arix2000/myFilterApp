package com.k.myfilterapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.k.myfilterapp.R;
import com.k.myfilterapp.SeekBarOptions;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.SubFilter;
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
    private Bundle previousFilterInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vignette_and_saturation);

        preview = findViewById(R.id.vignette_and_saturation_preview);
        filter = new Filter();
        mainBitmap = Bitmap.createBitmap(mainImage);
        preview.setImageBitmap(mainBitmap);

        setSaveBtnListener();
        initSubFilters();
        initSeekBarAndCounter();
        setListener();
    }

    private void setSaveBtnListener()
    {
        Button saveBtn = findViewById(R.id.btn_save_filter);
        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                saveFilter();
            }
        });
    }

    private void saveFilter()
    {
    }

    private void initSubFilters()
    {
        subFilters= new ArrayList<>();
        saturationSubFilter = new SaturationSubFilter(1);
        vignetteSubFilter = new VignetteSubFilter(this,0);

        subFilters.add(saturationSubFilter);
        subFilters.add(vignetteSubFilter);
    }

    private void initSeekBarAndCounter()
    {
        saturation = findViewById(R.id.saturation_seek_bar);
        saturationValue = findViewById(R.id.saturation_value);
        setSeekBar(saturation, new SeekBarOptions(20,100,220));

        vignette = findViewById(R.id.vignette_seek_bar);
        vignetteValue = findViewById(R.id.vignette_value);
        setSeekBar(vignette, new SeekBarOptions(0,0,255));
    }

    private void setSeekBar(SeekBar seekBar, SeekBarOptions seekBarOptions)
    {
        seekBar.setMin(seekBarOptions.getMin());
        seekBar.setProgress(seekBarOptions.getDefaultProgress());
        seekBar.setMax(seekBarOptions.getMax());
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
            public void onStartTrackingTouch(SeekBar seekBar){}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){}
        };

        saturation.setOnSeekBarChangeListener(listener);
        vignette.setOnSeekBarChangeListener(listener);

    }

    private void chooseSeekBar(int i, int id)
    {
        switch (id)
        {
            case R.id.saturation_seek_bar: saturationChange(convertHundredToFloatFrom(i)); break;
            case R.id.vignette_seek_bar: vignetteChange(i); break;
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
        subFilters.set(VIGNETTE_POSITION,vignetteSubFilter);
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