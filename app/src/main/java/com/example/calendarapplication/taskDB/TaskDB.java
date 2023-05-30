package com.example.calendarapplication.taskDB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.calendarapplication.Task;

@Database(entities = {Task.class}, version = 1)
public abstract class TaskDB extends RoomDatabase {
    public abstract TaskDao taskDao();
    public static TaskDB INSTANCE = null;

    public static TaskDB getInstance(Context context) {
        /* 메인 스레드에서 DB에 접근하는 것은 권장되지 않지만,
                간단한 구현을 위해 allowMainThreadQueries()를 사용 */
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TaskDB.class, "task.db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
