package com.example.calendarapplication;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

// Data Access Object(Dao) : 데이터를 처리할 행위를 모아 놓은것.
@Dao
public interface TaskDao {
    @Query("SELECT * FROM task")
    List<Task> getAll();

    @Insert
    void insertAll(Task... tasks);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);
}
