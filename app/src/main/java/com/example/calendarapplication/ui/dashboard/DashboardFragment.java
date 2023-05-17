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

import java.util.ArrayList;

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
                loadData(month, dayOfMonth);
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

    public void loadData(int m, int d){
        /* 원래 데이터베이스는 메인 스레드에서 접근하면 안되지만, 간단한 구현을 위해
           allowMainThreadQueries() 구문을 사용*/
        taskDB = TaskDB.getInstance(getContext());

        taskArrayList = (ArrayList<Task>) taskDB.taskDao().getAll();

        String month = Integer.toString(m + 1);
        String day = Integer.toString(d);

        int i = 0;
        while(i < taskArrayList.size()){
            Task task = taskArrayList.get(i);
            if(!task.getMonth().equals(month)|| !task.getDay().equals(day)) {
                taskArrayList.remove(i);
            }
            else{
                i++;
            }
        }

        adapter = new TaskAdapter(taskArrayList);

        rv_cal_task_list.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}