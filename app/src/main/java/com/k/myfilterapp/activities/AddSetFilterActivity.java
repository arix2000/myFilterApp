package com.k.myfilterapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.k.myfilterapp.R;

public class AddSetFilterActivity extends AppCompatActivity
{
    Bitmap mainBitmap;
    ImageView preview;
    SeekBar brightness;
    TextView brightnessValue;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_set_filter);

        preview = findViewById(R.id.add_filter_preview);
        mainBitmap = MainScreenActivity.mainImage;
        preview.setImageBitmap(mainBitmap);
        brightness = findViewById(R.id.brightness_seek_bar);
        brightnessValue = findViewById(R.id.brightness_value);
        brightness.setProgress(100);
        //TODO zastanowmy sie nad min max, trzebaby bylo wtedy robic dwie mozliwosci wpisywania wartosci w zaleznosci od poziomu api na ktorym jestesmy


        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                brightnessValue.setText(String.valueOf(i));
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
}