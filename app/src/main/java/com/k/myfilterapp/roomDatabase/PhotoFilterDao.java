package com.k.myfilterapp.roomDatabase;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PhotoFilterDao
{
    @Insert
    void insert(PhotoFilter filter);

    @Delete
    void delete(PhotoFilter filter);

    @Update
    void update(PhotoFilter filter);

    @Query("SELECT * FROM filterappdb")
    LiveData<List<PhotoFilter>> getAllFilters();
}
