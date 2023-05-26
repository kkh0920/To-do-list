package com.example.calendarapplication;

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

    // 마감일
    @ColumnInfo(name="year")
    private String year;
    @ColumnInfo(name="month")
    private String month;
    @ColumnInfo(name="day")
    private String day;

    // 시간
    private String hour;
    private String minute;

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
    public Task(String taskName, String year, String month, String day,
                String hour, String minute,
                String deadline, String estimatedDay, Boolean isChecked) {
        this.taskName = taskName;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
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


    // 마감일(연도, 월, 일)
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

    // 시간(시, 분)
    public String getHour() {
        return hour;
    }
    public void setHour(String hour) {
        this.hour = hour;
    }
    public String getMinute() {
        return minute;
    }
    public void setMinute(String minute) {
        this.minute = minute;
    }


    // D-day (n일, n >= 0, 정수)
    public String getDeadline() {
        return deadline;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }


    // 예상 수행 시간 (n일, n >= 1, 자연수)
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

    // 우선순위 정렬
    @Override
    public int compareTo(Task task) {
        int deadline1 = Integer.parseInt(getDeadline());
        int deadline2 = Integer.parseInt(task.getDeadline());

        int estimatedDay1 = Integer.parseInt(getEstimatedDay());
        int estimatedDay2 = Integer.parseInt(task.getEstimatedDay());

        int time1 = (Integer.parseInt(getHour()) * 100) + Integer.parseInt(getMinute());
        int time2 = (Integer.parseInt(task.getHour()) * 100) + Integer.parseInt(task.getMinute());

        if(estimatedDay1 != 0 && estimatedDay2 != 0) {
            if ((deadline1 - estimatedDay1) == (deadline2 - estimatedDay2)) {
                if (deadline1 == deadline2) {
                    return time1 - time2; // 3. 시간 비교
                }
                return deadline1 - deadline2; // 2. 데드라인 비교
            }
            return (deadline1 - estimatedDay1) - (deadline2 - estimatedDay2); // 1. 데드라인과 예상수행기간 비교
        }

        if(estimatedDay1 == 0 && estimatedDay2 == 0){
            if (deadline1 == deadline2) {
                return time1 - time2;
            }
            return deadline1 - deadline2;
        }

        return estimatedDay2 - estimatedDay1;
    }
}
