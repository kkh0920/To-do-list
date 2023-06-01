package com.example.calendarapplication.ui.dashboard;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendarapplication.ToastMessage;
import com.example.calendarapplication.ui.home.HomeFragment;
import com.example.calendarapplication.ui.popup.PopupActivity;
import com.example.calendarapplication.ui.popup.PopupDelete;
import com.example.calendarapplication.Task;
import com.example.calendarapplication.TaskAdapter;
import com.example.calendarapplication.taskDB.TaskDB;
import com.example.calendarapplication.databinding.FragmentDashboardBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private TextView tv_calendar_title;
    private CalendarView calendarView;
    private RecyclerView rv_cal_task_list;
    private ArrayList<Task> taskArrayList;
    private TaskAdapter adapter;
    private TaskDB taskDB = null;
    private Toast sToast = null;

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

        Calendar minDate = Calendar.getInstance();

        minDate.set(curYear, curMonth, curDay);

        calendarView.setMinDate(minDate.getTimeInMillis());
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
            public Task onCheckboxClick(CheckBox checkBox, int position) {
                Task task = taskArrayList.get(position);

                int year = Integer.parseInt(dateFormat("yyyy"));
                int month = Integer.parseInt(dateFormat("MM"));
                int day = Integer.parseInt(dateFormat("dd"));
                if(year != curYear || month - 1 != curMonth || day != curDay){
                    ToastMessage tm = new ToastMessage(getActivity(),
                            "일정 탭이나 오늘 날짜(" + month + "월 " + day + "일" + ")에서 관리하세요.");
                    checkBox.setChecked(!checkBox.isChecked());
                    return task;
                }

                task.setIsChecked(checkBox.isChecked());

                TaskDB updatedTask = TaskDB.getInstance(getContext());
                updatedTask.taskDao().update(task);

                rv_cal_task_list.post(new Runnable() {
                    @Override
                    public void run() {
                        int currentPosition = taskArrayList.indexOf(task);

                        Collections.sort(taskArrayList);

                        int newPosition = taskArrayList.indexOf(task);

                        if(currentPosition != newPosition){
                            adapter.notifyItemMoved(currentPosition, newPosition);
                        }
                    }
                });

                return task;
            }

            @Override
            public void onEditClick(View v, int position) {
                int year = Integer.parseInt(dateFormat("yyyy"));
                int month = Integer.parseInt(dateFormat("MM"));
                int day = Integer.parseInt(dateFormat("dd"));
                if(year != curYear || month - 1 != curMonth || day != curDay){
                    ToastMessage tm = new ToastMessage(getActivity(),
                            "일정 탭이나 오늘 날짜(" + month + "월 " + day + "일" + ")에서 관리하세요.");
                    return;
                }

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
                int year = Integer.parseInt(dateFormat("yyyy"));
                int month = Integer.parseInt(dateFormat("MM"));
                int day = Integer.parseInt(dateFormat("dd"));
                if(year != curYear || month - 1 != curMonth || day != curDay){
                    ToastMessage tm = new ToastMessage(getActivity(),
                            "일정 탭이나 오늘 날짜(" + month + "월 " + day + "일" + ")에서 관리하세요.");
                    return;
                }

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
//                            TaskDB.getInstance(getContext()).taskDao().delete(task);
                        editTask(task, name, Integer.toString(year), m, d, hour, minute,
                                Integer.toString(deadline), estimatedDay);

                        TaskDB.getInstance(getContext()).taskDao().update(task);

                        int currentPosition = taskArrayList.indexOf(task);

                        Collections.sort(taskArrayList);

                        int newPosition = taskArrayList.indexOf(task);

                        adapter.notifyItemMoved(currentPosition, newPosition);
                        adapter.notifyItemChanged(newPosition);
                    }
                }
            });

    public void editTask(Task task, String name, String year, String month, String day,
                         String hour, String minute, String deadline, String estimatedDay){
        task.setTaskName(name);
        task.setYear(year);
        task.setMonth(month);
        task.setDay(day);
        task.setHour(hour);
        task.setMinute(minute);
        task.setDeadline(deadline);
        task.setEstimatedDay(estimatedDay);
    }

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