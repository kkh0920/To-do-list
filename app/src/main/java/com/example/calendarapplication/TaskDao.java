package com.example.calendarapplication;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Data Access Object(Dao) : 데이터를 처리할 행위를 모아 놓은것.
@Dao
public interface TaskDao {
    @Query("SELECT * FROM task") // 불러오기
    List<Task> getAll();

    @Insert // 삽입
    void insertAll(Task... tasks);

    @Update
    public void update(Task task);

    @Delete // 삭제
    void delete(Task task);

    @Query("select * FROM task WHERE deadline > : currentTime")
    List<Task> getTaskAfterTime(long currentTime);

    LiveData<List<Task>> getTasksAfterTime(Date currentTime);
}
