package com.example.calendarapplication.ui.home;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_NO_LOCALIZED_COLLATORS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendarapplication.PopupActivity;
import com.example.calendarapplication.Task;
import com.example.calendarapplication.TaskAdapter;
import com.example.calendarapplication.TaskDB;
import com.example.calendarapplication.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.io.Console;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ArrayList<Task> taskArrayList;
    private TaskAdapter adapter;
    private TextView tv_temp_text;
    private TaskDB taskDB = null;

    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 초기값 세팅
        initialized();

        // 데이터 로딩
        loadDataAll();


        // "일정을 추가해 보세요!" 텍스트 표시 유무
        if(adapter.getItemCount() > 0)
            tv_temp_text.setVisibility(View.INVISIBLE);
        else
            tv_temp_text.setVisibility(View.VISIBLE);


        // + 버튼 클릭 이벤트
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnPopupClick();
            }
        });

        return root;
    }

    public void initialized(){
        tv_temp_text = binding.tvTempText;
        recyclerView = binding.rvTaskList;
        fab = binding.fab;

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
    }

    // 홈 화면에 사용자가 입력한 모든 일정을 시각화
    public void loadDataAll(){
        /* 원래 데이터베이스는 메인 스레드에서 접근하면 안되지만, 간단한 구현을 위해
           allowMainThreadQueries() 구문을 사용*/
        taskDB = TaskDB.getInstance(getContext());

        taskArrayList = (ArrayList<Task>) taskDB.taskDao().getAll();

        updateDeadline();

        adapter = new TaskAdapter(taskArrayList);

        recyclerView.setAdapter(adapter);

        adapter.Sorting();

        adapter.notifyDataSetChanged();
    }


    // 팝업 화면 호출
    public void mOnPopupClick() {
        Intent intent = new Intent(getContext(), PopupActivity.class);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);
    }


    // 마감일 까지 남은 날짜 계산
    public int calculateDeadline(int year, int month, int day){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar todayCalendar = Calendar.getInstance();
        Calendar estimateCalendar = Calendar.getInstance();

        estimateCalendar.set(Calendar.YEAR, year);
        estimateCalendar.set(Calendar.MONTH, month - 1);
        estimateCalendar.set(Calendar.DAY_OF_MONTH, day);

        long diff = estimateCalendar.getTimeInMillis() - todayCalendar.getTimeInMillis();

        return (int) (diff / (24 * 60 * 60 * 1000));
    }


    // 데드라인 갱신
    public void updateDeadline(){
        int i = 0;
        while(i < taskArrayList.size()){
            Task task = taskArrayList.get(i);

            int year = Integer.parseInt(task.getYear());
            int month = Integer.parseInt(task.getMonth());
            int day = Integer.parseInt(task.getDay());

            int updatedDeadline = calculateDeadline(year, month, day);

            if(updatedDeadline < 0) {
                taskArrayList.remove(task);
                taskDB.taskDao().delete(task);
                continue;
            }

            task.setDeadline(Integer.toString(updatedDeadline));
            taskDB.taskDao().update(task);
            i++;
        }
    }

    // 팝업창에 입력된 내용, 마감일을 받아오기 위해,
    // Activity Result API에서 제공하는 registerForActivityResult() API를 사용하여 값을 받아옴.
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        tv_temp_text.setVisibility(View.INVISIBLE);

                        // 데이터 받기오기
                        Intent intent = result.getData();

                        String name = intent.getStringExtra("name");

                        int year = intent.getIntExtra("year", 0);
                        int month = intent.getIntExtra("month", 0);
                        int day = intent.getIntExtra("day", 0);
                        month++;

                        String estimatedDay = intent.getStringExtra("estimatedDay");

                        // D-Day 계산
                        int deadline = calculateDeadline(year, month, day);

                        // 데이터 추가
                        Task task = new Task(name,
                                Integer.toString(year), Integer.toString(month), Integer.toString(day),
                                Integer.toString(deadline), estimatedDay, false);

                        TaskDB.getInstance(getContext()).taskDao().insertAll(task);

                        // 데이터 로딩
                        loadDataAll();
                    }
                }
            });

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        TaskDB.destroyInstance();
        taskDB = null;
    }
}