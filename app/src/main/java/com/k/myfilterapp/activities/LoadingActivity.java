package com.k.myfilterapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.k.myfilterapp.R;

public class LoadingActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Thread welcomeThread = new Thread()
        {
            @Override
            public void run()
            {
                try {
                    sleep(1000);
                } catch (Exception e) {
                    Log.d("LOADING_ERROR", "run: " + e.getMessage());
                }

                Intent intent = getIntent();
                intent.setClass(LoadingActivity.this, MainScreenActivity.class);
                startActivity(intent);
                finish();
            }
        };
        welcomeThread.start();
    }

}