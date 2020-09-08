package com.k.myfilterapp.roomDatabase;

import android.widget.ImageView;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.zomato.photofilters.imageprocessors.Filter;

@Entity(tableName = "filterappdb")
public class PhotoFilter
{
    @PrimaryKey(autoGenerate = true)
    int id;
    String filterName;
    int brightness;
    int VignetteAlpha;
    int colorDepth;
    float red;
    float green;
    float blue;
    float saturation;
    float contrast;


    public PhotoFilter(String filterName, int brightness, int vignetteAlpha, int colorDepth, float red, float green, float blue, float saturation, float contrast)
    {
        this.filterName = filterName;
        this.brightness = brightness;
        VignetteAlpha = vignetteAlpha;
        this.colorDepth = colorDepth;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.saturation = saturation;
        this.contrast = contrast;
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
        return VignetteAlpha;
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
}
