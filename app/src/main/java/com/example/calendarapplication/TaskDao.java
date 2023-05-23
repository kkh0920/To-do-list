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
    @Query("SELECT * FROM task") // 불러오기
    List<Task> getAll();

    @Insert // 삽입
    void insertAll(Task... tasks);
    @Insert
    void insert(Task task); // 단일 객체 삽입

    @Update
    public void update(Task task);

    @Delete // 삭제
    void delete(Task task);
    
    //@Query("SELECT * FROM task")
    //Task getTaskById(int taskId);


}
