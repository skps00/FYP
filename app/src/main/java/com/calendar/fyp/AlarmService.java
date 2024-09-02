package com.calendar.fyp;

import static android.app.PendingIntent.getActivity;
import static com.calendar.fyp.AlarmReceiver.CHANNEL_ID;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class AlarmService extends Service implements DBHelper.OnDatabaseChangedListener{

    private AlarmManager alarmManager;
    private final String TAG = "AlarmService";



    private DBHelper dbHelper;







    //binder

    private final IBinder binder = new MyBinder();

    @Override
    public void onDatabaseChanged() {
        Log.v(TAG, "service detected change of the database");
        dbHelper = new DBHelper(getApplicationContext());
        handleAlarms();
    }

    public class MyBinder extends Binder {
        public AlarmService getService() {
            Log.v(TAG, "fuck it: " + AlarmService.this);
            return AlarmService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }



    private void handleAlarms(){

        // 获取闹钟信息列表
        ArrayList<Alarm> alarms = dbHelper.getAllAlarm();

        if (alarms != null) {
            for (int i = 0; i < alarms.size(); i++) {
                Alarm alarm = alarms.get(i);

                Instant instant = Instant.ofEpochMilli(alarm.getTimeInMillis());
                LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());


                // 设置闹钟
                Log.v(TAG, localDateTime.toLocalTime() + "");
                setAlarm(alarm);
            }
        }
    }



    private void setAlarm(Alarm alarm) {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        // 創建鬧鐘的意圖
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("event_id", alarm.getEventId()); // 傳遞事件ID，用於識別事件
        alarmIntent.putExtra("requestCode", alarm.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarm.getId(), alarmIntent, PendingIntent.FLAG_IMMUTABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            alarm.show(TAG);
//            dbHelper.getEventById(alarm.getEventId()).show(TAG);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis() - (EarlyMinuteHelper.getEarlyMinutes(getApplicationContext()) * 60 * 1000), pendingIntent);
//            Log.v(TAG, "early mins: " + earlyMinute);
            Log.v(TAG, Build.VERSION.SDK_INT + " >= " + Build.VERSION_CODES.M);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), pendingIntent);
            Log.v(TAG, Build.VERSION.SDK_INT + " < " + Build.VERSION_CODES.M);
        }

    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v(TAG, "service created");
        try {

            handleAlarms();
        }catch (Exception e){

        }



        createNotificationChannel(getApplicationContext());

        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Event Channel";
            String channelDescription = "这是我的事件通知通道";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
