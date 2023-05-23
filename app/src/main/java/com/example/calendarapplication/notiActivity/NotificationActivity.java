package com.example.calendarapplication.notiActivity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.app.AlarmManager;
import com.example.calendarapplication.Task;
import com.example.calendarapplication.TaskDB;
import com.example.calendarapplication.TaskDao;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.calendarapplication.databinding.ActivityNotificationBinding;

import com.example.calendarapplication.R;

import java.util.List;
import java.util.Calendar;
public class NotificationActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "Channelid" ; //알림 채널 id
    private static final int NOTIFICATION_ID = 1; //알림ID

    public static void setDeadlineNotificaiton(Context context, Task task) {
        //current time
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();

        //Deadline date
        String[] deadlineArray = task.getDeadline().split("-");
        int year = Integer.parseInt(deadlineArray[0]);
        int month = Integer.parseInt(deadlineArray[1])-1 ;
        int day = Integer.parseInt(deadlineArray[2]);

        //Deadline 날짜로 캘린더 설정
        calendar.set(year, month, day);

        //Deadline 날짜 시간을 0시 0분 0초로 설정
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long notificationTime = calendar.getTimeInMillis();

        //오늘 날짜와 데드라인 날자가 같으면 알림
        if (currentTime >= notificationTime) {
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("task_name", task.getTaskName());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
            }
        }

    } //setDeadlineNotification }

    public static void setStartNotification(Context context, Task task) {
        //Deadline date
        String[] deadlineArray = task.getDeadline().split("-");
        int year = Integer.parseInt(deadlineArray[0]);
        int month = Integer.parseInt(deadlineArray[1])-1 ;
        int day = Integer.parseInt(deadlineArray[2]);

        //수행에 필요한 날짜
        int estimatedDays = Integer.parseInt(task.getEstimatedDay());

        //Deadline - estimatedDays
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        calendar.set(year,month,day);
        calendar.add(Calendar.DAY_OF_MONTH,-estimatedDays);
        long notificationTime = calendar.getTimeInMillis();

        //if Deadline - estimatedDays = 0
        if (notificationTime <= System.currentTimeMillis()) {
            showNotification(context,task.getTaskName() + "오늘 부터는 시작 하셔야 합니다");
        }
    }

    public static void showNotification (Context context, String taskName){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        //알림채널 생성 (android 8.0 이상에서 필요)
        NotificationCompat.Builder builder = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "MyChannel",NotificationManager.IMPORTANCE_DEFAULT);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        //알림 생성
        builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Task Deadline")
                    .setContentText(taskName + "deadline 입니다.")
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp);

        //알림 표시
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }

    }//showNotification }


    private AppBarConfiguration appBarConfiguration;
    private ActivityNotificationBinding binding;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_notification);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        }); //bindig }


    } //onCreate }




    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_notification);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}//NotificationActivity class }