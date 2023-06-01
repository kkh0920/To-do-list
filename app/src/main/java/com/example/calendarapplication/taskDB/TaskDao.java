package com.example.calendarapplication.taskDB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.calendarapplication.Task;

import java.util.List;

// Data Access Object(Dao) : 데이터를 처리할 행위를 모아 놓은것.
@Dao
public interface TaskDao {
    @Query("SELECT * FROM task")
    List<Task> getAll();
    @Query("SELECT * FROM task WHERE id = :taskId")
    Task getTaskById(long taskId);

    @Insert
    void insertAll(Task... tasks);

    @Insert
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);
}
