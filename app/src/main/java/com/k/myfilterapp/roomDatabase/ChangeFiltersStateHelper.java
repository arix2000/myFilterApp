package com.k.myfilterapp.roomDatabase;

import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TextView;

public abstract class ChangeFiltersStateHelper
{
    public static void removePreviousTextViewTypeface(TextView previousTextView)
    {
        if(previousTextView!=null) {
            previousTextView.setTypeface(Typeface.DEFAULT);
            previousTextView.setTextColor(Color.parseColor("#C6C6C6"));
            previousTextView.setTextSize(12);
        }
    }

    public static void setCurrentTextViewTypeface(TextView currentTextView)
    {
        currentTextView.setTypeface(Typeface.DEFAULT_BOLD);
        currentTextView.setTextColor(Color.WHITE);
        currentTextView.setTextSize(13);
    }
}
