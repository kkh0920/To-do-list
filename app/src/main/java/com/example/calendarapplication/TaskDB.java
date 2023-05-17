package com.example.calendarapplication;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Task.class}, version = 1)
public abstract class TaskDB extends RoomDatabase {
    public abstract TaskDao taskDao();
    public static TaskDB INSTANCE = null;

    public static TaskDB getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TaskDB.class, "task.db")
                    .allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
