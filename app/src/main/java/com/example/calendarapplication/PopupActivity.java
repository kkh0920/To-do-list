package com.example.calendarapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.calendarapplication.R;
import com.google.android.material.snackbar.Snackbar;

// 팝업창 기능 구현
public class PopupActivity extends Activity {

    EditText et_task_name, et_month, et_day;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_popup);

        et_task_name = (EditText) findViewById(R.id.et_task_name);
        et_month = (EditText) findViewById(R.id.et_month);
        et_day = (EditText) findViewById(R.id.et_day);
    }

    // 확인 버튼 클릭
    public void mOnClose(View v) {
        String name = et_task_name.getText().toString();
        String month = et_month.getText().toString();
        String day = et_day.getText().toString();

        // 데이터가 적합한지 체크(임시)
        if(isAdequateData(month, day)) {
            // 데이터 전달하기
            Intent intent = new Intent();

            intent.putExtra("name", name);
            intent.putExtra("month", month);
            intent.putExtra("day", day);

            setResult(RESULT_OK, intent);

            // 액티비티(팝업) 닫기
            finish();
        }
        else{
            // 적절하지 않은 데이터 입력 시 오류 메세지 출력
            Snackbar.make(v.getRootView(), "잘못된 입력입니다.", Snackbar.LENGTH_SHORT).show();
        }
    }

    // 취소 버튼 클릭
    public void mOnCancel(View v) {
        setResult(RESULT_CANCELED);

        // 액티비티(팝업) 닫기
        finish();
    }

    // 입력된 데이터가 적절한 수 인지 체크하는 함수
    public Boolean isAdequateData(String month, String day){
        // 날짜 입력이 잘못된 경우 오류 메세지 출력 후 false를 return.
        if(month.length() == 0 || day.length() == 0){
            return false;
        }

        int m = Integer.parseInt(month);
        int d = Integer.parseInt(day);
        if(m < 1 || m > 12 || d < 1 || d > 31){
            return false;
        }

        return true;
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
