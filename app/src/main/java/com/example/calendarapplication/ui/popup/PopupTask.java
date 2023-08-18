package com.example.calendarapplication.ui.popup;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.calendarapplication.R;

public class PopupTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_popup_task);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView tv_task_name_detail = findViewById(R.id.tv_task_name_detail);

        Intent intent = getIntent();
        String task_name = intent.getStringExtra("name");

        tv_task_name_detail.setText(task_name);
    }

    public void onClickCancelButton_Task(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }
}