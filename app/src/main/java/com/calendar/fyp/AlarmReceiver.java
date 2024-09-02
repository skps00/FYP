package com.calendar.fyp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;

import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.widget.Toast;

import java.time.format.DateTimeFormatter;

public class AlarmReceiver extends BroadcastReceiver{
    private String TAG = "AlarmReceiver";
    private DBHelper dbHelper;
    private Event event;
    public static final String CHANNEL_ID = "my_channel_id";
    private static final int NOTIFICATION_ID = 1;
    private int notificationId;
    private Context context;
    private MediaPlayer mediaPlayer;



    @Override
    public void onReceive(Context context, Intent intent) {
        dbHelper = new DBHelper(context); // 使用正确的上下文初始化DBHelper对象
        // 从意图中获取事件ID
        Long eventId = intent.getLongExtra("event_id", -1);
        notificationId = intent.getIntExtra("requestCode", -1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        dbHelper.deleteAlarm(notificationId);
        this.context = context;
        // 根据事件ID从数据库中检索相应的事件信息
        if (eventId != -1) {
            // 执行所需的操作，如显示通知、播放声音等
            event = dbHelper.getEventById(eventId);
            event.show(TAG);

            boolean hasVibratePermission = hasPermission(context, Manifest.permission.VIBRATE);
            boolean hasPolicyPermission = hasPermission(context, Manifest.permission.ACCESS_NOTIFICATION_POLICY);
            boolean hasNOTIFICATIONSPermission = hasPermission(context, Manifest.permission.POST_NOTIFICATIONS);

            if (hasVibratePermission && hasPolicyPermission && hasNOTIFICATIONSPermission) {
                // 权限已授予，继续发送通知



                // 喚醒設備
                PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
                wakeLock.acquire(10 * 60 * 1000L); // 喚醒設備10分鐘


                // 播放鬧鐘聲音



                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                if (mediaPlayer == null){

                    mediaPlayer = MediaPlayer.create(context, alarmSound);
                    mediaPlayer.setLooping(true);

                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
                    audioManager.setMode(AudioManager.MODE_RINGTONE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build();

                        mediaPlayer.setAudioAttributes(audioAttributes);
                    } else {
                        
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                    }


                    mediaPlayer.start();
                }



                sendNotification(context, event.getTitle(), event.getStartTime().format(formatter));
                dbHelper.deleteAlarm(notificationId);
            } else {
                Log.v(TAG, "权限未授予");
                Toast.makeText(context, "权限未授予", Toast.LENGTH_LONG);
            }
        } else {
            Log.v(TAG, "未找到事件ID");
            Toast.makeText(context, "未找到事件ID", Toast.LENGTH_LONG);
        }
    }

    private boolean hasPermission(Context context, String permission) {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission);
    }

    @SuppressLint("MissingPermission")
    private void sendNotification(Context context, String title, String content) {
        if (arePermissionsGranted(context)) {

            Intent nextActivity = new Intent(context, WakeUpHandle.class);

            Log.v(TAG, "start Time: " + event.getStartTime());
            Log.v(TAG, "end Time: " + event.getEndTime());

            nextActivity.putExtra("eventID", event.getId());

            PendingIntent pendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(context, notificationId, nextActivity, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                pendingIntent = PendingIntent.getActivity(context, notificationId, nextActivity, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.img_bell)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)  // 设置点击通知时要启动的Activity
                    .setPriority(NotificationCompat.PRIORITY_MAX);

            Notification notification = builder.build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(NOTIFICATION_ID, notification);
            // 创建 Vibrator 实例
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            // 检查设备是否支持振动 and 通知是否被启用
            if (vibrator.hasVibrator()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.v(TAG, "Vibration start");
                    long[] pattern = {1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000}; // 自定义的振动模式，以毫秒为单位
                    int repeatIndex = 0; // 指定重复振动的索引，-1 表示不重复
                    int[] amplitudes = {255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0};
                    VibrationEffect vibrationEffect = VibrationEffect.createWaveform(pattern, amplitudes, repeatIndex);
                    vibrator.vibrate(vibrationEffect);
                } else {
                    // 旧版本的处理方法
                    Log.v(TAG, "Vibration start (old)");
                    vibrator.vibrate(-1); // 在旧版本中直接指定振动持续时间
                }
            }
            Log.v(TAG, notificationManager.areNotificationsEnabled() + "");
        } else {
            Log.v(TAG, "权限未授予");
        }
    }


    private boolean arePermissionsGranted(Context context) {
        String[] permissions = new String[]{
                Manifest.permission.VIBRATE,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
        };

        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }





}