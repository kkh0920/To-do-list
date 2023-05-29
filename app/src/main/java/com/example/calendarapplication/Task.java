package com.example.calendarapplication;

import android.widget.LinearLayout;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Task 클래스
@Entity
public class Task implements Comparable<Task> {
    @PrimaryKey(autoGenerate = true) // 기본적으로 키 값을 가지고 있어야 함.
    private int id;

    // 일정 내용
    @ColumnInfo(name="task_name")
    private String taskName;

    // 마감일 날짜
    @ColumnInfo(name="year")
    private String year;
    @ColumnInfo(name="month")
    private String month;
    @ColumnInfo(name="day")
    private String day;

    // D-day 일 수(HomeFragment 의 updateDeadline 함수를 통해 매번 갱신)
    @ColumnInfo(name="deadline")
    private String deadline;

    // 예상 수행 시간
    @ColumnInfo(name="estimated_day")
    private String estimatedDay;

    // 체크박스 체크 유무
    @ColumnInfo(name="is_checked")
    private Boolean isChecked;


    // 생성자
    public Task(String taskName, String year, String month, String day, String deadline, String estimatedDay, Boolean isChecked) {
        this.taskName = taskName;
        this.year = year;
        this.month = month;
        this.day = day;
        this.deadline = deadline;
        this.estimatedDay = estimatedDay;
        this.isChecked = isChecked;
    }

    // ID
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }


    // 일정 내용
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    // 마감일(년, 월, 일)
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


    // D-day (n일, n >= 0, 정수)
    public String getDeadline() {
        return deadline;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }


    // 예상 수행 시간 (n일, n >= 0, 정수)
    public void setEstimatedDay(String estimatedDay) {
        this.estimatedDay = estimatedDay;
    }
    public String getEstimatedDay() {
        return estimatedDay;
    }


    // 체크박스
    public void setIsChecked(Boolean isChecked){
        this.isChecked = isChecked;
    }
    public Boolean isChecked(){
        return isChecked;
    }

    // 우선순위 정
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
