package com.calendar.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.File;


public class ActivityLoading extends AppCompatActivity {

    private String TAG = "Loading";
    private static final int PERMISSION_REQUEST_CODE = 1;

    Intent intent;

    public AlarmService alarmService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageHelper.applyLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Intent serviceIntent = new Intent(getApplicationContext(), AlarmService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(serviceIntent);
//            Log.v(TAG, "startForegroundService(serviceIntent)");
//        } else {
//            startService(serviceIntent);
//            Log.v(TAG, "startService(serviceIntent)");
//        }
        startService(serviceIntent);
        Log.v(TAG, "startService(serviceIntent)");

        intent = new Intent(this, MainActivity.class);


        boolean hasVibratePermission = hasPermission(this, Manifest.permission.VIBRATE);
        boolean hasPolicyPermission = hasPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY);
        boolean hasNOTIFICATIONSPermission = hasPermission(this, Manifest.permission.POST_NOTIFICATIONS);
        boolean hasWAKEUPLOCKPermission = hasPermission(this, Manifest.permission.WAKE_LOCK);
        if (hasVibratePermission && hasPolicyPermission && hasNOTIFICATIONSPermission && hasWAKEUPLOCKPermission) {
            // 权限已授予
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Code to be executed after the delay
                    // Place your desired actions or code here
                    startActivity(intent);
                    finish();
                }
            }, 1); // 3000 milliseconds = 3 seconds
        } else {
            Log.v(TAG, "权限未授予");
            Toast.makeText(this, "权限未授予", Toast.LENGTH_LONG);

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_NOTIFICATION_POLICY, Manifest.permission.VIBRATE}, PERMISSION_REQUEST_CODE);
        }


    }

    private boolean hasPermission(Context context, String permission) {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission);
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 權限已經被授予，可以執行需要權限的操作
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Code to be executed after the delay
                        // Place your desired actions or code here
                        startActivity(intent);
                        finish();
                    }
                }, 1); // 3000 milliseconds = 3 seconds
            } else {
                // 權限被拒絕，需要處理相應的邏輯
                Toast.makeText(this, "權限被拒絕", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}