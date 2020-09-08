package com.k.myfilterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ColorOverlaySubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.VignetteSubFilter;

public class MainActivity extends AppCompatActivity
{

    ImageView inputImage;
    com.zomato.photofilters.imageprocessors.Filter filter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.loadLibrary("NativeImageProcessor");

        inputImage = findViewById(R.id.input_image_view);
        filter = new Filter();

        Bitmap inputBitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.pool_party);
        Bitmap outputBitmap = inputBitmap.copy(inputBitmap.getConfig(),true);
        filter.addSubFilter(new ColorOverlaySubFilter(100, .2f, .2f, .0f));
        filter.addSubFilter(new VignetteSubFilter(this,100));
        filter.addSubFilter(new ContrastSubFilter(2f));

        outputBitmap = filter.processFilter(outputBitmap);
        inputImage.setImageBitmap(outputBitmap);

    }
}