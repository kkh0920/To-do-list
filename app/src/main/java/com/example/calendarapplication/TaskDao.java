package com.example.calendarapplication;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

// Data Access Object(Dao) : 데이터를 처리할 행위를 모아 놓은것.
@Dao
public interface TaskDao {
    @Query("SELECT * FROM task") // 불러오기
    List<Task> getAll();

    @Query("UPDATE task SET is_checked = :isChecked WHERE ID = :sID") // 체크박스 수정
    void updateCheckBox(int sID, Boolean isChecked);

    @Insert // 삽입
    void insertAll(Task... tasks);

    @Delete // 삭제
    void delete(Task task);
}
