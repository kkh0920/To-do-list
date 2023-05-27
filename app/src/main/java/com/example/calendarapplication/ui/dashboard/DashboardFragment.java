package com.example.calendarapplication.ui.dashboard;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendarapplication.PopupActivity;
import com.example.calendarapplication.PopupDelete;
import com.example.calendarapplication.R;
import com.example.calendarapplication.Task;
import com.example.calendarapplication.TaskAdapter;
import com.example.calendarapplication.TaskDB;
import com.example.calendarapplication.databinding.FragmentDashboardBinding;
import com.example.calendarapplication.ui.home.HomeFragment;

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

    private int pos, curYear, curMonth, curDay;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initialized();

        int year = Integer.parseInt(dateFormat("yyyy"));
        int month = Integer.parseInt(dateFormat("MM"));
        int day = Integer.parseInt(dateFormat("dd"));

        curYear = year;
        curMonth = month - 1;
        curDay = day;
        loadTaskUnderCalendar(year, month - 1, day);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 달력에서 날짜 클릭 시 수행되는 부분
                curYear = year;
                curMonth = month;
                curDay = dayOfMonth;
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
    public void adapterInitializer(){
        adapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onCheckboxClick(int position, CompoundButton compoundButton, boolean isChecked) {
                Task task = taskArrayList.get(position);
                task.setIsChecked(isChecked);

                TaskDB updatedTask = TaskDB.getInstance(compoundButton.getContext());
                updatedTask.taskDao().update(task);
            }

            @Override
            public void onEditClick(View v, int position) {
                Task task = taskArrayList.get(position);

                Intent intent = new Intent(getContext(), PopupActivity.class);

                intent.putExtra("isEdit", true);

                intent.putExtra("name", task.getTaskName());

                intent.putExtra("year", task.getYear());
                intent.putExtra("month", task.getMonth());
                intent.putExtra("day", task.getDay());

                intent.putExtra("estimatedDay", task.getEstimatedDay());

                intent.putExtra("hour", task.getHour());
                intent.putExtra("minute", task.getMinute());

                pos = position;

                intent.setAction(Intent.ACTION_GET_CONTENT);
                launcher.launch(intent);
            }

            @Override
            public void onDeleteClick(View v, int position) {
                pos = position;

                Intent intent = new Intent(getContext(), PopupDelete.class);

                intent.putExtra("name", taskArrayList.get(position).getTaskName());

                intent.setAction(Intent.ACTION_GET_CONTENT);
                launcherDel.launch(intent);
            }
        });
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
        adapterInitializer();

        adapter.Sorting();

        rv_cal_task_list.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        // 데이터 받기오기
                        Intent intent = result.getData();

                        String name = intent.getStringExtra("name");

                        int year = intent.getIntExtra("year", 0);
                        int month = intent.getIntExtra("month", 0);
                        int day = intent.getIntExtra("day", 0);
                        month++;

                        String m = "0";
                        if(month < 10)
                            m += Integer.toString(month);
                        else
                            m = Integer.toString(month);

                        String d = "0";
                        if(day < 10)
                            d += Integer.toString(day);
                        else
                            d = Integer.toString(day);

                        String hour = intent.getStringExtra("hour");
                        String minute = intent.getStringExtra("minute");

                        String estimatedDay = intent.getStringExtra("estimatedDay");

                        // D-Day 계산
                        int deadline = calculateDeadline(year, month - 1, day);

                        Task task = adapter.getItem(pos);
                        TaskDB.getInstance(getContext()).taskDao().delete(task);

                        Task newTask = new Task(name,
                                Integer.toString(year), m, d, hour, minute,
                                Integer.toString(deadline), estimatedDay, false);

                        TaskDB.getInstance(getContext()).taskDao().insertAll(newTask);
                        loadTaskUnderCalendar(curYear, curMonth, curDay);
                    }
                }
            });

    ActivityResultLauncher<Intent> launcherDel = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK){
                        TaskDB deleteTask = TaskDB.getInstance(getContext());

                        deleteTask.taskDao().delete(taskArrayList.get(pos));

                        taskArrayList.remove(pos);

                        adapter.notifyItemRemoved(pos);
                    }
                }
            });

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            return;
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}