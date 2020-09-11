package com.k.myfilterapp;

import android.net.Uri;

import com.k.myfilterapp.activities.MainScreenActivity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainScreenActivityTest
{
    MainScreenActivity mainScreenActivity = new MainScreenActivity();

    @Test
    public void uriToBitmapTest()
    {
        assertNull(mainScreenActivity.uriToBitmap(null));
        assertNull(mainScreenActivity.uriToBitmap(Uri.parse("randomPath")));
    }

    @Test
    public void scaleBitmapToTest()
    {
        try {
            assertNull(mainScreenActivity.scaleBitmapTo(20,20,null));
            fail("Exepction was thrown");
        }
        catch (NullPointerException e )
        {
            System.out.println(e.getMessage());
        }

        //assertNull(mainScreenActivity.uriToBitmap(Uri.parse("randomPath")));
    }

}