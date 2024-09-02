package com.calendar.fyp;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;


public class SettingFragment extends Fragment    {

    private String TAG = "Setting";
    private int EarlyMinute;
    private AlarmService alarmServiceInstance;


    // 创建ServiceConnection对象
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            // 当服务绑定成功时，此方法会被调用
            // 在此方法中可以获取服务实例并进行相应的操作
            Log.v(TAG, "Service connected");
            AlarmService.MyBinder alarmBinder = (AlarmService.MyBinder) binder;
//                Log.v(TAG, "Service: " + alarmBinder.getService());
            alarmServiceInstance = alarmBinder.getService();
//                Log.v(TAG, "testing 123: " + alarmServiceInstance);
//                Log.v(TAG, "testing 101: " + databaseChangedListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 当服务断开连接时，此方法会被调用
            // 在此方法中可以进行相应的处理
            Log.v(TAG, "Service disconnected");
            alarmServiceInstance = null;
        }
    };




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        NavigationView navigationView = view.findViewById(R.id.SettingMenu);












        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        navigationView.setNavigationItemSelectedListener(item -> {

            // 根據 itemId 做相應的處理
            switch (item.getItemId()) {
                case R.id.settingMenu_lang:
                    // 使用者點擊了項目1
                    // 創建對話框建構器
                    builder.setTitle(R.string.choose_language);

                    Locale currentLocale = Locale.getDefault();
                    String language = currentLocale.getLanguage();
                    int checkItem;
                    if (language == "en"){
                        checkItem = 0;
                    }else {
                        checkItem = 1;
                    }
                    builder.setSingleChoiceItems(new CharSequence[]{"English", "中文"}, checkItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 使用者選擇的選項處理
                            switch (which) {
                                case 0:
                                    setLocale("en");
                                    Toast.makeText(requireContext(), "User chosen English", Toast.LENGTH_LONG).show();
                                    break;
                                case 1:
                                    setLocale("zh");
                                    Toast.makeText(requireContext(), "使用者選擇了中文", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    }).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            // 在選項選擇時執行相應的操作
                            // 這裡可以添加您額外的邏輯
                            switch (position) {
                                case 0:
                                    setLocale("en");
                                    Toast.makeText(requireContext(), "User chosen English", Toast.LENGTH_LONG).show();
                                    break;
                                case 1:
                                    setLocale("zh");
                                    Toast.makeText(requireContext(), "使用者選擇了中文", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    builder.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            builder.setTitle(R.string.restart_your_app);
                            builder.setMessage(R.string.you_must_restart_the_app_to_complete_the_language_change_operation);
                            builder.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requireActivity().recreate();
                                    dialog.dismiss(); // 關閉對話框
                                }
                            });
                            // 顯示對話框
                            builder.create().show();
                        }
                    });

                    // 顯示對話框
                    builder.create().show();
                    break;
                case R.id.settingMenu_notification:
                    // 使用者點擊了項目2
                    // 創建對話框建構器
                    builder.setTitle(R.string.Notifcation_Setting);

                    int checkedItem = 0; // 預設選擇的選項索引
                    EarlyMinute = EarlyMinuteHelper.getEarlyMinutes(getContext());
                    switch (EarlyMinute){
                        case 0:
                            checkedItem = 0;
                            break;
                        case 5:
                            checkedItem = 1;
                            break;
                        case 10:
                            checkedItem = 2;
                            break;
                    }
                    builder.setSingleChoiceItems(new CharSequence[]{getString(R.string._0_min), getString(R.string._5_mins), getString(R.string._10_mins), }, checkedItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 使用者選擇的選項處理
                            switch (which) {
                                case 0:
                                    // 使用者選擇了 0 Min
                                    EarlyMinute = 0;
                                    break;
                                case 1:
                                    // 使用者選擇了 5 Mins
                                    EarlyMinute = 5;
                                    break;
                                case 2:
                                    // 使用者選擇了 10 Mins
                                    EarlyMinute = 10;
                                    break;
                            }
                        }
                    });
                    builder.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EarlyMinuteHelper.setEarlyMinutes(requireContext(), EarlyMinute);
                            setEarlyMinute(EarlyMinute);
                            dialog.dismiss(); // 關閉對話框
                        }
                    });

                    // 顯示對話框
                    builder.create().show();
                    break;
                // 其他項目的處理
            }

            // 返回 true 表示已處理該項目的點擊事件
            return true;
        });


        return view;
    }

    private void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);

        LanguageHelper.setLanguage(getContext(), lang);

        LanguageHelper.applyLanguage(getContext());
//        getContext().getResources().updateConfiguration(config, getContext().getResources().getDisplayMetrics());


    }


    private void setEarlyMinute(int min) {



    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // 初始化代码可以放在这里

        Intent serviceIntent = new Intent(getContext(), AlarmService.class);

        // 绑定AlarmService
        getContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unbindService(serviceConnection);
    }
}
