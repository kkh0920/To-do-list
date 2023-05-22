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

    @ColumnInfo(name="year")
    private String year;
    @ColumnInfo(name="month")
    private String month;
    @ColumnInfo(name="day")
    private String day;
    @ColumnInfo(name="deadline")
    private String deadline;
    @ColumnInfo(name="estimated_day")
    private String estimatedDay;

    @ColumnInfo(name="is_checked")
    private Boolean isChecked;

    public Task(String taskName, String year, String month, String day, String deadline, String estimatedDay, Boolean isChecked) {
        this.taskName = taskName;
        this.year = year;
        this.month = month;
        this.day = day;
        this.deadline = deadline;
        this.estimatedDay = estimatedDay;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
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

    public String getDeadline() {
        return deadline;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
    public void setEstimatedDay(String estimatedDay) {
        this.estimatedDay = estimatedDay;
    }
    public String getEstimatedDay() {
        return estimatedDay;
    }

    public void setIsChecked(Boolean isChecked){
        this.isChecked = isChecked;
    }

    public Boolean isChecked(){
        return isChecked;
    }

    @Override
    public int compareTo(Task task) {
        int dl1 = Integer.parseInt(getDeadline());
        int dl2 = Integer.parseInt(task.getDeadline());

        int ed1 = Integer.parseInt(getEstimatedDay());
        int ed2 = Integer.parseInt(task.getEstimatedDay());

        if((dl1 - ed1) == (dl2 - ed2)){
            return dl1 - dl2;
        }
        return (dl1 - ed1) - (dl2 - ed2);
    }
}
