package com.example.calendarapplication.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendarapplication.Task;
import com.example.calendarapplication.TaskAdapter;
import com.example.calendarapplication.TaskDB;
import com.example.calendarapplication.databinding.FragmentDashboardBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private CalendarView calendarView;
    public TextView tv_calendar_title;

    public RecyclerView rv_cal_task_list;
    private ArrayList<Task> taskArrayList;
    private TaskAdapter adapter;
    private TaskDB taskDB = null;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initialized();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 달력에서 날짜 클릭 시 수행되는 부분
                loadData(year, month, dayOfMonth);
            }
        });

        return root;
    }

    // 변수 초기화 부분
    public void initialized(){
        calendarView = binding.calendarView;
        tv_calendar_title = binding.tvCalendarTitle;
        rv_cal_task_list = binding.rvCalTaskList;
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        rv_cal_task_list.setLayoutManager(layoutManager);
    }

    public int getCalculatedDeadline(int year, int month, int day){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar todayCalendar = Calendar.getInstance();
        Calendar estimateCalendar = Calendar.getInstance();

        estimateCalendar.set(Calendar.YEAR, year);
        estimateCalendar.set(Calendar.MONTH, month);
        estimateCalendar.set(Calendar.DAY_OF_MONTH, day);

        long diff = estimateCalendar.getTimeInMillis() - todayCalendar.getTimeInMillis();

        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    public void loadData(int y, int m, int d){
        /* 원래 데이터베이스는 메인 스레드에서 접근하면 안되지만, 간단한 구현을 위해
           allowMainThreadQueries() 구문을 사용*/
        int diff = getCalculatedDeadline(y, m, d);

        taskDB = TaskDB.getInstance(getContext());

        taskArrayList = (ArrayList<Task>) taskDB.taskDao().getAll();

        // 모든 일정을 탐색, 현재 선택된 날짜가 각각의 일정 마감일 안에 속하면 달력 하단에 표시
        if(diff < 0){
            taskArrayList.clear();
        }

        int i = 0;
        while(i < taskArrayList.size()){
            Task task = taskArrayList.get(i);
            int deadline = Integer.parseInt(task.getDeadline());
            if(diff > deadline)
                taskArrayList.remove(task);
            else
                i++;
        }

        adapter = new TaskAdapter(taskArrayList);

        adapter.Sorting();

        rv_cal_task_list.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}