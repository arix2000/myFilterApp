package com.k.myfilterapp;

public class SeekBarOptions
{
    private int min;
    private int max;
    private int defaultProgress;

    public SeekBarOptions(int min, int defaultProgress, int max)
    {
        this.min = min;
        this.max = max;
        this.defaultProgress = defaultProgress;
    }

    public int getMin()
    {
        return min;
    }

    public int getMax()
    {
        return max;
    }

    public int getDefaultProgress()
    {
        return defaultProgress;
    }


}
