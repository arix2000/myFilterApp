package com.k.myfilterapp.roomDatabase;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PhotoFilterViewModel extends AndroidViewModel
{
    private PhotoFilterRepository filterRepository;
    private LiveData<List<PhotoFilter>> allFilters;

    public PhotoFilterViewModel(@NonNull Application application)
    {
        super(application);
        filterRepository = new PhotoFilterRepository(application);
        allFilters = filterRepository.getAllFilters();
    }

    public LiveData<List<PhotoFilter>> getAllFilters()
    {
        return allFilters;
    }

    public void insert(PhotoFilter filter)
    {
        filterRepository.insert(filter);
    }

    public void delete(PhotoFilter filter)
    {
        filterRepository.delete(filter);
        Log.d("TAG_REPOSITORY", "run: SUCCESFULLY");
    }

    public void update(PhotoFilter filter)
    {
        filterRepository.update(filter);
    }
}
