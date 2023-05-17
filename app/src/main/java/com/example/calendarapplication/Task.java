package com.example.calendarapplication;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Task 클래스
@Entity
public class Task implements Comparable<Task> {
    @PrimaryKey(autoGenerate = true) // 기본적으로 키 값을 가지고 있어야 함.
    private int id;
    @ColumnInfo(name="task_name")
    private String taskName;
    @ColumnInfo(name="month")
    private String month;
    @ColumnInfo(name="day")
    private String day;
    @ColumnInfo(name="is_checked")
    private Boolean isChecked;

    public Task(String taskName, String month, String day, Boolean isChecked) {
        this.taskName = taskName;
        this.month = month;
        this.day = day;
        this.isChecked = isChecked;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setIsChecked(Boolean isChecked){
        this.isChecked = isChecked;
    }

    public Boolean isChecked(){
        return isChecked;
    }

    @Override
    public int compareTo(Task task) {
        int m1 = Integer.parseInt(getMonth());
        int m2 = Integer.parseInt(task.getMonth());

        int d1 = Integer.parseInt(getDay());
        int d2 = Integer.parseInt(task.getDay());

        if(m1 == m2){
            return d1 - d2;
        }
        return m1 - m2;
    }
}
