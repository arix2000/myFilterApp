package com.k.myfilterapp.roomDatabase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

@Database(entities = {PhotoFilter.class}, version = 1)
public abstract class PhotoFilterDatabase extends RoomDatabase
{
    private static PhotoFilterDatabase instance;
    public abstract PhotoFilterDao filterDao();

    public static synchronized PhotoFilterDatabase getInstance(Context context)
    {
        if (instance == null)
        {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    PhotoFilterDatabase.class, "photoFilterDatabase.db")
                    .addCallback(roomCallback)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback()
    {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db)
        {
            super.onCreate(db);
            initDefaultFilters();
        }
    };

    private static void initDefaultFilters()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                PhotoFilterDao filterDao = instance.filterDao();
                List<PhotoFilter> filters = createDefaultFilters();

                for (PhotoFilter filter : filters)
                {
                    filterDao.insert(filter);
                }
            }
        }).start();
    }

    private static List<PhotoFilter> createDefaultFilters()
    {
        List<PhotoFilter> outputFilters = new ArrayList<>();

        outputFilters.add(new PhotoFilter("example1",50,1.5f,1.5f));
        outputFilters.add(new PhotoFilter("example2",30,1.1f,1.2f));
        outputFilters.add(new PhotoFilter("example3",0,2f,0.7f));
        outputFilters.add(new PhotoFilter("example4",0,2f,0.7f));
        outputFilters.add(new PhotoFilter("example5",0,40f,0.7f));
        outputFilters.add(new PhotoFilter("example6",0,2f,2f));
        outputFilters.add(new PhotoFilter("example7",0,255,0.7f,1.5f));

        //in future i will add there more filters

        return outputFilters;
    }

}
