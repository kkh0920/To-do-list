package com.example.calendarapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.calendarapplication.R;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// 팝업창 기능 구현
public class PopupActivity extends Activity {

    private Button bt_deadline;
    private EditText et_task_name, et_estimated_day;
    private int year = 0, month = 0, day = 0;
    private DatePickerDialog.OnDateSetListener callbackMethod;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_popup);

        initialized();

        // 달력 확인 버튼 클릭 이벤트
        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                year = i;
                month = i1;
                day = i2;

                bt_deadline.setText(i + " / " + (i1 + 1) + " / " + i2);
            }
        };
    }

    public void initialized() {
        et_task_name = (EditText) findViewById(R.id.et_task_name);
        et_estimated_day = (EditText) findViewById(R.id.et_estimated_day);
        bt_deadline = (Button) findViewById(R.id.bt_deadline);
    }

    public String dateFormat(String pattern) {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        return new SimpleDateFormat(pattern).format(date);
    }

    public int getCalculatedDeadline(){
        Calendar todayCalendar = Calendar.getInstance();
        Calendar estimateCalendar = Calendar.getInstance();

        estimateCalendar.set(Calendar.YEAR, year);
        estimateCalendar.set(Calendar.MONTH, month);
        estimateCalendar.set(Calendar.DAY_OF_MONTH, day);

        long diff = estimateCalendar.getTimeInMillis() - todayCalendar.getTimeInMillis();

        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    public void onClickDeadlineButton(View v){
        int year = Integer.parseInt(dateFormat("yyyy"));
        int month = Integer.parseInt(dateFormat("MM"));
        int day = Integer.parseInt(dateFormat("dd"));

        DatePickerDialog dialog = new DatePickerDialog(this, R.style.DialogTheme, callbackMethod, year, month - 1, day);

        // 오늘 이전 날짜는 선택 불가
        Calendar minDate = Calendar.getInstance();
        minDate.set(year, month - 1, day);
        dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        dialog.show();
    }

    public void onClickOKButton(View v) {
        String name = et_task_name.getText().toString();
        String estimatedDay = et_estimated_day.getText().toString();

        if(estimatedDay.length() == 0 || year == 0 || month  == 0 || day == 0){
            Snackbar.make(v.getRootView(), "값을 입력하지 않으셨나요?", Snackbar.LENGTH_SHORT).show();
            return;
        }

        int e = Integer.parseInt(estimatedDay);

        int deadline = getCalculatedDeadline();

        if(deadline + 1 < e){
            Snackbar.make(v.getRootView(), "수행 기간이 너무 길어요!", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // 데이터 전달하기
        Intent intent = new Intent();

        intent.putExtra("name", name);
        intent.putExtra("year", year);
        intent.putExtra("month", month);
        intent.putExtra("day", day);
        intent.putExtra("estimatedDay", estimatedDay);

        setResult(RESULT_OK, intent);

        // 액티비티(팝업) 닫기
        finish();
    }

    public void onClickCancelButton(View v) {
        setResult(RESULT_CANCELED);
        // 액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 바깥 레이어 클릭 시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }
}
