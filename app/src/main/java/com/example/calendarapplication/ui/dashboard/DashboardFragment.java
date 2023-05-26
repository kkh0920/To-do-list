package com.example.calendarapplication.ui.dashboard;

import android.app.DatePickerDialog;
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

import com.example.calendarapplication.R;
import com.example.calendarapplication.Task;
import com.example.calendarapplication.TaskAdapter;
import com.example.calendarapplication.TaskDB;
import com.example.calendarapplication.databinding.FragmentDashboardBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private TextView tv_calendar_title;
    private CalendarView calendarView;
    private RecyclerView rv_cal_task_list;
    private ArrayList<Task> taskArrayList;
    private TaskAdapter adapter;
    private TaskDB taskDB = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initialized();

        int year = Integer.parseInt(dateFormat("yyyy"));
        int month = Integer.parseInt(dateFormat("MM"));
        int day = Integer.parseInt(dateFormat("dd"));

        loadTaskUnderCalendar(year, month - 1, day);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 달력에서 날짜 클릭 시 수행되는 부분
                loadTaskUnderCalendar(year, month, dayOfMonth);
            }
        });

        return root;
    }

    // 변수 초기화 부분
    public void initialized(){
        tv_calendar_title = binding.tvCalendarTitle;
        calendarView = binding.calendarView;
        rv_cal_task_list = binding.rvCalTaskList;

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        rv_cal_task_list.setLayoutManager(layoutManager);
    }

    public String dateFormat(String pattern) {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        return new SimpleDateFormat(pattern).format(date);
    }

    public int calculateDeadline(int year, int month, int day) {
        Calendar todayCalendar = Calendar.getInstance();
        Calendar estimateCalendar = Calendar.getInstance();

        estimateCalendar.set(Calendar.YEAR, year);
        estimateCalendar.set(Calendar.MONTH, month);
        estimateCalendar.set(Calendar.DAY_OF_MONTH, day);

        long diff = estimateCalendar.getTimeInMillis() - todayCalendar.getTimeInMillis();

        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    public void loadTaskUnderCalendar(int y, int m, int d){
        int diff = calculateDeadline(y, m, d);
        if(diff < 0) {
            taskArrayList.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        taskDB = TaskDB.getInstance(getContext());

        taskArrayList = (ArrayList<Task>) taskDB.taskDao().getAll();

        // 모든 일정을 탐색, 현재 선택된 날짜가 각 일정의 데드라인 안에 속하면 달력 하단에 표시
        int index = 0;
        while(index < taskArrayList.size()){
            Task task = taskArrayList.get(index);
            int deadline = Integer.parseInt(task.getDeadline());
            int month = Integer.parseInt(task.getMonth());
            int day = Integer.parseInt(task.getDay());
            int estimatedDay = Integer.parseInt(task.getEstimatedDay());

            if(estimatedDay == 0 && (m + 1 != month || d != day))
                taskArrayList.remove(task);
            else if(diff > deadline)
                taskArrayList.remove(task);
            else{
                task.setDeadline(Integer.toString(deadline - diff));
                index++;
            }
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