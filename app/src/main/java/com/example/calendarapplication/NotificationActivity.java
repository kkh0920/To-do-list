package com.example.calendarapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;


import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.calendarapplication.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;
import java.util.List;


public class NotificationActivity extends AppCompatActivity {

    private NotificationManager manager;
    private static final String CHANNEL_ID = "channel_id";
    private static final CharSequence CHANNEL_NAME = "Channel Name";

    //private AppBarConfiguration appBarConfiguration;

    private Notification NotificationManager;
    private TaskDao taskDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //알림 매니저 초기화
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        //데이터베이스 초기화
        TaskDB taskDatabase = TaskDB.getInstance(this);
        taskDao = taskDatabase.taskDao();

        //현재 시간 가져오기
        Date currentTime = new Date();

        //데이터베이스에서 현재 시간 이후의 일정을 가져옴
        taskDao.getTasksAfterTime(currentTime).observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                //가져온 일정 리스트를 순회하며 알림 보내기
                for (Task task : tasks) {
                    Date deadline = task.getDeadline();
                    //사용자가 입력한 DeadLine과 현재시간을 비교
                    if (deadline != null && deadline.compareTo(currentTime) <= 0) {
                        sendNotification(task);
                    }
                }
            }
        });
    }




    private void sendNotification(Task task) {
        //알림 클릭 시 실행될 인텐트
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        //알림 채널 생성
        createNotificationChannel();


        //알림 구성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        //intent = new Intent(this, MainActivity.class);
        //pendingIntent = PendingIntent.getActivity(this, 101, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
        builder.setContentTitle("DeadLine 알림");
        builder.setContentText(task.getTaskName() + "DeadLine");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);


        //알림 표시
        manager.notify(task.getId(), builder.build());


    }

    private void createNotificationChannel() {
        manager = (android.app.NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, android.app.NotificationManager.IMPORTANCE_DEFAULT));
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, android.app.NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
    }
}



