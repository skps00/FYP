package com.calendar.fyp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.calendar.fyp.databinding.HomepageBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity{
    HomepageBinding binding;
    private String TAG = "MainActivity";
    CalendarFragment calendarFragment = new CalendarFragment();;
    SettingFragment settingFragment = new SettingFragment();
    PieChartFragment piechartFragment= new PieChartFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageHelper.applyLanguage(this);
        super.onCreate(savedInstanceState);
        binding = HomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(calendarFragment);

        binding.bottomNavigationView.setBackground(null);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout);

        if (currentFragment instanceof CalendarFragment) {
            binding.bottomNavigationView.setSelectedItemId(R.id.menu_calendar);
        } else if (currentFragment instanceof PieChartFragment) {
            binding.bottomNavigationView.setSelectedItemId(R.id.menu_pieChart);
        }else if (currentFragment instanceof SettingFragment) {
            binding.bottomNavigationView.setSelectedItemId(R.id.menu_setting);
        }


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_setting:
                    replaceFragment(settingFragment);
                    break;
                case R.id.menu_pieChart:
                    replaceFragment(piechartFragment);
                    break;
                case R.id.menu_calendar:
                    replaceFragment(calendarFragment);
                    break;
            }

            return true;
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomDialog bottomDialog = new BottomDialog(MainActivity.this);
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout);

                if (currentFragment instanceof CalendarFragment) {
                    // 当前Fragment是YourFragmentClass类型的
                    bottomDialog.setOnDialogUpdateListener(calendarFragment);
                    // 在这里可以执行您需要的操作
                }


                bottomDialog.showBottomDialog();

            }
        });





    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void setFragment(int type){
        switch (type){
            case 1:         //setting
                replaceFragment(settingFragment);
                break;
            case 2:         //calendar
                replaceFragment(calendarFragment);
                break;
            case 3:         //piechart
                replaceFragment(piechartFragment);
                break;
        }
    }

}
