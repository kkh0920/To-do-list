package com.example.calendarapplication.ui.popup;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.calendarapplication.R;
import com.example.calendarapplication.ui.home.HomeFragment;
import com.google.android.material.snackbar.Snackbar;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// 팝업창 기능 구현
public class PopupActivity extends Activity {
    private TextView tv_popup_title;
    private Button bt_deadline, bt_estimated_day, bt_time;
    private EditText et_task_name;
    private int year = 0, month = -1, day = 0;
    private String hour = "-1", minute = "-1";
    private String estimatedDay = "-1";
    private DatePickerDialog.OnDateSetListener callbackMethod;

    private boolean isEdit;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 타이틀 바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_popup);

        // 변수 초기화
        initialized();

        bt_time.setVisibility(View.GONE);

        checkEditOrAdd();

        // 달력에서 확인 버튼 클릭
        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                year = i;
                month = i1;
                day = i2;

                String m = "0";
                if(month < 9)
                    m += Integer.toString(month + 1);
                else
                    m = Integer.toString(month + 1);

                String d = "0";
                if(day < 10)
                    d += Integer.toString(day);
                else
                    d = Integer.toString(day);

                bt_deadline.setText(i + " / " + m + " / " + d);
            }
        };
    }

    public void initialized() {
        tv_popup_title = (TextView) findViewById(R.id.tv_popup_title);
        et_task_name = (EditText) findViewById(R.id.et_task_name);
        bt_deadline = (Button) findViewById(R.id.bt_deadline);
        bt_estimated_day = (Button) findViewById(R.id.bt_estimated_day);
        bt_time = (Button) findViewById(R.id.bt_time);
    }
    public void checkEditOrAdd(){
        Intent intent = getIntent();
        isEdit = intent.getBooleanExtra("isEdit", false);
        if(!isEdit)
            return;

        tv_popup_title.setText("수정");

        et_task_name.setText(intent.getStringExtra("name"));

        String syear = intent.getStringExtra("year");
        String smonth = intent.getStringExtra("month");
        String sday = intent.getStringExtra("day");
        bt_deadline.setText(syear + " / " + smonth + " / " + sday);

        year = Integer.parseInt(syear);
        month = Integer.parseInt(smonth) - 1;
        day = Integer.parseInt(sday);

        String estimate = intent.getStringExtra("estimatedDay");
        estimatedDay = estimate;

        if(estimate.equals("0")){
            bt_estimated_day.setText("약속");

            bt_time.setVisibility(View.VISIBLE);

            String h = intent.getStringExtra("hour");
            String m = intent.getStringExtra("minute");

            hour = h;
            minute = m;

            bt_time.setText(hour + " : " + minute);
        }
        else{
            bt_estimated_day.setText(intent.getStringExtra("estimatedDay") + "일 간 수행");
            hour = "00";
            minute = "00";
        }
    }

    public String dateFormat(String pattern) {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        return new SimpleDateFormat(pattern).format(date);
    }


    // 오늘 부터 사용자가 선택한 날 까지 남은 일 수
    public int calculateDeadline(){
        Calendar todayCalendar = Calendar.getInstance();
        Calendar estimateCalendar = Calendar.getInstance();

        estimateCalendar.set(Calendar.YEAR, year);
        estimateCalendar.set(Calendar.MONTH, month);
        estimateCalendar.set(Calendar.DAY_OF_MONTH, day);

        long diff = estimateCalendar.getTimeInMillis() - todayCalendar.getTimeInMillis();

        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    public void onClickOKButton(View v) {
        String name = et_task_name.getText().toString();

        // 데이터 입력 여부 체크
        if(estimatedDay.equals("-1") || year == 0 || month  == -1 || day == 0){
            Snackbar.make(v.getRootView(), "값을 입력하지 않으셨나요?", Snackbar.LENGTH_SHORT).show();
            return;
        }

        int e = Integer.parseInt(estimatedDay);
        int deadline = calculateDeadline();

        // 예상 수행 기간이 마감일 보다 큰 경우 인지 체크
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
        intent.putExtra("hour", hour);
        intent.putExtra("minute", minute);
        intent.putExtra("estimatedDay", estimatedDay);

        if(isEdit)
            intent.putExtra("isEdit", true);
        else
            intent.putExtra("isEdit", false);

        setResult(RESULT_OK, intent);

        // 액티비티(팝업) 닫기
        finish();
    }
    public void onClickCancelButton(View v) {
        setResult(RESULT_CANCELED);
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

    // 달력 팝업창
    public void onClickDeadlineButton(View v){
        int year = Integer.parseInt(dateFormat("yyyy"));
        int month = Integer.parseInt(dateFormat("MM"));
        int day = Integer.parseInt(dateFormat("dd"));

        DatePickerDialog dialog;

        if(year == 0 || month == 0 || day == 0)
            dialog = new DatePickerDialog(this, R.style.DialogTheme, callbackMethod, year, month - 1, day);
        else
            dialog = new DatePickerDialog(this, R.style.DialogTheme, callbackMethod, this.year, this.month, this.day);

        // 오늘 이전 날짜는 선택 불가
        Calendar minDate = Calendar.getInstance();

        minDate.set(year, month - 1, day);

        dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        dialog.show();
    }

    // 예상수행기간 선택 팝업창
    public void showEstimatedDayPicker(View v){
        final Dialog numberPickerDialog = new Dialog(this);
        numberPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        numberPickerDialog.setContentView(R.layout.number_picker);

        numberPickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button okBtn = (Button) numberPickerDialog.findViewById(R.id.btn_ok_numberpicker);
        Button cancelBtn = (Button) numberPickerDialog.findViewById(R.id.btn_cancel_numberpicker);

        final NumberPicker np = (NumberPicker) numberPickerDialog.findViewById(R.id.estimated_day_picker);

        np.setMinValue(0);
        np.setMaxValue(999);

        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            np.setSelectionDividerHeight(0);
        }

        np.setWrapSelectorWheel(false);

        if(estimatedDay.equals("-1"))
            np.setValue(0);
        else
            np.setValue(Integer.parseInt(estimatedDay));

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                estimatedDay = Integer.toString(np.getValue());

                if(estimatedDay.equals("0")) {
                    bt_estimated_day.setText("약속");
                    bt_time.setVisibility(View.VISIBLE);
                    hour = "12";
                }
                else {
                    bt_estimated_day.setText(estimatedDay + "일 간 수행");
                    bt_time.setVisibility(View.GONE);
                    hour = "00";
                }
                minute = "00";

                numberPickerDialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberPickerDialog.dismiss();
            }
        });

        numberPickerDialog.show();
    }

    public void showTimePicker(View v){
        final Dialog timePickerDialog = new Dialog(this);
        timePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        timePickerDialog.setContentView(R.layout.time_picker);

        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button okBtn = (Button) timePickerDialog.findViewById(R.id.btn_ok_timepicker);
        Button cancelBtn = (Button) timePickerDialog.findViewById(R.id.btn_cancel_timepicker);

        final TimePicker tp = (TimePicker) timePickerDialog.findViewById(R.id.timepicker);

        tp.setIs24HourView(true);

        if(hour.equals("-1") || minute.equals("-1")){
            tp.setHour(12);
            tp.setMinute(0);
        }
        else{
            tp.setHour(Integer.parseInt(hour));
            tp.setMinute(Integer.parseInt(minute));
        }

        tp.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tp.getHour() < 10) {
                    hour = "0";
                    hour += Integer.toString(tp.getHour());
                }
                else{
                    hour = Integer.toString(tp.getHour());
                }

                if(tp.getMinute() < 10){
                    minute = "0";
                    minute += Integer.toString(tp.getMinute());
                }
                else{
                    minute = Integer.toString(tp.getMinute());
                }

                bt_time.setText(hour + " : " + minute);

                timePickerDialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.dismiss();
            }
        });

        timePickerDialog.show();
    }
}
