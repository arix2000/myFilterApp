package com.k.myfilterapp.roomDatabase;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ColorOverlaySubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.VignetteSubFilter;

@Entity(tableName = "filterappdb")
public class PhotoFilter
{
    @PrimaryKey(autoGenerate = true)
    int id;
    String filterName;
    int brightness;
    int vignetteAlpha;
    int colorDepth;
    float red;
    float green;
    float blue;
    float saturation;
    float contrast;
    @Ignore
    Bitmap filteredBitmap;

    @Ignore
    boolean isWasClicked;


    public PhotoFilter(String filterName, int brightness, int vignetteAlpha, int colorDepth, float red, float green, float blue, float saturation, float contrast)
    {
        this.filterName = filterName;
        this.brightness = brightness;
        this.vignetteAlpha = vignetteAlpha;
        this.colorDepth = colorDepth;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.saturation = saturation;
        this.contrast = contrast;
    }
    @Ignore
    public PhotoFilter(String filterName, int brightness, int vignetteAlpha, float saturation, float contrast)
    {
        this.filterName = filterName;
        this.brightness = brightness;
        this.saturation = saturation;
        this.contrast = contrast;
        this.vignetteAlpha = vignetteAlpha;
        this.colorDepth = 0;
        this.red = 0;
        this.green = 0;
        this.blue = 0;

    }
    @Ignore
    public PhotoFilter(String filterName, int brightness, float saturation, float contrast)
    {
        this.filterName = filterName;
        this.brightness = brightness;
        this.saturation = saturation;
        this.contrast = contrast;
        this.vignetteAlpha = 0;
        this.colorDepth = 0;
        this.red = 0;
        this.green = 0;
        this.blue = 0;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public String getFilterName()
    {
        return filterName;
    }

    public int getBrightness()
    {
        return brightness;
    }

    public int getVignetteAlpha()
    {
        return vignetteAlpha;
    }

    public int getColorDepth()
    {
        return colorDepth;
    }

    public float getRed()
    {
        return red;
    }

    public float getGreen()
    {
        return green;
    }

    public float getBlue()
    {
        return blue;
    }

    public float getSaturation()
    {
        return saturation;
    }

    public float getContrast()
    {
        return contrast;
    }

    public Bitmap getFilteredBitmapFrom(Bitmap bitmap, Context context)
    {

        Filter filter = new Filter();
        Bitmap outputBitmap = bitmap.copy(bitmap.getConfig(), true);

        filter.addSubFilter(new ContrastSubFilter(contrast));
        filter.addSubFilter(new BrightnessSubFilter(brightness));
        filter.addSubFilter(new ColorOverlaySubFilter(colorDepth, red, green, blue));
        filter.addSubFilter(new SaturationSubFilter(saturation));
        filter.addSubFilter(new VignetteSubFilter(context, vignetteAlpha));


        outputBitmap = filter.processFilter(outputBitmap);

        return outputBitmap;
    }


    public Bitmap getFilteredBitmap()
    {
        return filteredBitmap;
    }

    public void setFilteredBitmap(Bitmap filteredBitmap)
    {
        this.filteredBitmap = filteredBitmap;
    }

    public boolean isWasClicked()
    {
        return isWasClicked;
    }

    public void setWasClicked(boolean wasClicked)
    {
        isWasClicked = wasClicked;
    }
}
