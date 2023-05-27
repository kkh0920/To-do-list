package com.example.calendarapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.calendarapplication.ui.home.HomeFragment;

public class PopupDelete extends AppCompatActivity {
    Button okBtn, cancelBtn;
    TextView tv_task_name_del;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_popup_delete);

        tv_task_name_del = (TextView) findViewById(R.id.tv_task_name_del);
        okBtn = (Button) findViewById(R.id.btn_ok_del);
        cancelBtn = (Button) findViewById(R.id.btn_cancel_del);

        Intent intent = getIntent();

        tv_task_name_del.setText(intent.getStringExtra("name"));
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}