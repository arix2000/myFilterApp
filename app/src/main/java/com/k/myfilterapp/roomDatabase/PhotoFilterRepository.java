package com.k.myfilterapp.roomDatabase;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Database;

import com.k.myfilterapp.R;

import java.util.List;

public class PhotoFilterRepository
{
    private PhotoFilterDao filterDao;
    private LiveData<List<PhotoFilter>> allFilters;

    public PhotoFilterRepository(Application application)
    {
        PhotoFilterDatabase filterDatabase = PhotoFilterDatabase.getInstance(application);
        filterDao = filterDatabase.filterDao();
        allFilters = filterDao.getAllFilters();
    }

    public LiveData<List<PhotoFilter>> getAllFilters()
    {
        return allFilters;
    }

    public void insert(final PhotoFilter filter)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                filterDao.insert(filter);
            }
        }).start();
    }

    public void delete(final PhotoFilter filter)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                filterDao.delete(filter);

            }
        }).start();
    }

    public void update(final PhotoFilter filter)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                filterDao.update(filter);
            }
        }).start();
    }
}
