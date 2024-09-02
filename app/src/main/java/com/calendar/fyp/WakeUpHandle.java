package com.calendar.fyp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WakeUpHandle extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private Button disable_alarm_button;
    private String TAG = "WakeUpHandle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_notification);
        disable_alarm_button = findViewById(R.id.disable_alarm_button);
        Bundle extras = getIntent().getExtras();
        long eventID = extras.getLong("eventID");


        Log.v(TAG, "EventID: " + eventID);
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        Event event = dbHelper.getEventById(eventID);

        TextView eventDate = findViewById(R.id.event_date);
        TextView eventTime = findViewById(R.id.event_time_text);
        TextView eventTitle = findViewById(R.id.event_title);

        eventDate.setText(event.getStartTime().toLocalDate().toString());
        if (event.isTimePeriod()){
            eventTime.setText(event.getStartTime().toLocalTime().toString() + " - " + event.getEndTime().toLocalTime().toString());
        }else {
            eventTime.setText(event.getStartTime().toLocalTime().toString());
        }

        eventTitle.setText(event.getTitle());


        disable_alarm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "test button click");
                Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.cancel();
                if (mediaPlayer != null){
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;

            }
        });


        View rootView = findViewById(android.R.id.content);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    // 在這裡執行您的程式碼
                    // 檢測到用戶點擊後退按鈕後的操作
                    Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.cancel();
                    if (mediaPlayer != null){
                        mediaPlayer.stop();
                    }
                    mediaPlayer.release();
                    mediaPlayer = null;
                    return true; // 如果您處理了後退按鈕事件，返回true；否則返回false。
                }
                return false;
            }
        });
    }
}
