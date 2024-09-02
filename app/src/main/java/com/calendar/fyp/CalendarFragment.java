package com.calendar.fyp;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener, BottomDialog.OnDialogUpdateListener {



    // 实现接口方法
    @Override
    public void onUpdateData(String newData) {
        // 在这里处理来自BottomDialog的更新数据，可以更新CalendarFragment的视图或执行其他操作
        setEventView(LocalDate.parse(newData));
        setMonthView();
    }
    private DBHelper dbHelper;
    private TextView MonthText;
    private TextView YearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private ImageButton previousButton;
    private ImageButton nextButton;
    private String TAG = "CalendarFragment";
    private RecyclerView EventRecyclerView;
    private EventAdapter eventAdapter;
    private ArrayList<String> daysInMonth;
    private int previouslyClickedPosition = -1; // 初始化为无效位置

    int spacingInPixels = 1; // 间距值（以像素为单位）









    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        initWidgets(view);
        selectedDate = LocalDate.now();
        dbHelper = new DBHelper(requireContext());


        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(getContext(), spacingInPixels);
        EventRecyclerView.addItemDecoration(itemSpacingDecoration);
//        dbHelper.addEvent(new Event("Title1 today", LocalDateTime.now(), "Today now "));
//        dbHelper.addEvent(new Event("Title2 today", LocalDateTime.now(), "Today now "));
//
//        LocalDateTime currentTime = LocalDateTime.now();
//
//        // 获取过去一天的时间
//        LocalDateTime pastTime = currentTime.minus(1, ChronoUnit.DAYS);
//        dbHelper.addEvent(new Event("Title1 yesterday", pastTime, "yesterday now "));
//        dbHelper.addEvent(new Event("Title2 yesterday", pastTime, "yesterday now "));

        setMonthView();
        setEventView(selectedDate);
        return view;
    }

    private void initWidgets(View view) {
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        MonthText = view.findViewById(R.id.monthtxt);
        YearText = view.findViewById(R.id.yeartxt);
        previousButton = view.findViewById(R.id.previousButton);
        nextButton = view.findViewById(R.id.nextButton);

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMonthAction(v);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonthAction(v);
            }
        });


        EventRecyclerView = view.findViewById(R.id.EventRecyclerView);


    }

    private void setMonthView(){
        MonthText.setText(MonthFromDate(selectedDate));
        YearText.setText(YearFromDate(selectedDate));
        daysInMonth = daysInMonthArray(selectedDate);


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this, monthYearFromDate(selectedDate), requireContext());
        calendarRecyclerView.setAdapter(calendarAdapter);
        calendarAdapter.notifyDataSetChanged();


    }


    private void setEventView(LocalDate Date){

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        EventRecyclerView.setLayoutManager(layoutManager);

        List<Event> eventList = dbHelper.getEventsByDate(Date);
        eventAdapter = new EventAdapter(eventList, new EventAdapter.OnItemListener() {
            @Override
            public void onItemClick(int Position, String text, String eventTimeText, String eventTitleText, long id) {
            }
        }, false);


        EventRecyclerView.setAdapter(eventAdapter);

    }



    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = date.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        int dayCounter = 1;
        int totalDays = 42;

        for (int i = 1; i <= totalDays; i++) {
            if (i <= dayOfWeek || dayCounter > daysInMonth) {
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf(dayCounter));
                dayCounter++;
            }
        }

        int lastIndex = daysInMonthArray.size() - 1;
        int emptyCount = 0;
        ListIterator<String> iterator = daysInMonthArray.listIterator(lastIndex + 1);

        while (iterator.hasPrevious() && emptyCount < 7) {
            String previous = iterator.previous();
            if (previous.isEmpty()) {
                iterator.remove();
                emptyCount++;
            } else {
                break;
            }
        }

        return daysInMonthArray;
    }
    private String YearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        return date.format(formatter);
    }

    private String MonthFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");
        return date.format(formatter);
    }

    private String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public void previousMonthAction(View view)
    {
        selectedDate = selectedDate.minusMonths(1);
        selectedDate = LocalDate.from(selectedDate.atStartOfDay());
        setMonthView();
        setEventView(selectedDate);

    }

    public void nextMonthAction(View view)
    {
        selectedDate = selectedDate.plusMonths(1);
        selectedDate = LocalDate.from(selectedDate.atStartOfDay());
        setMonthView();
        setEventView(selectedDate);
    }


    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.equals("")) {
//            String message = "选择的日期：" + dayText + " " + monthYearFromDate(selectedDate);
//            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            calendarRecyclerView.getChildAt(position).setBackgroundColor(Color.GRAY);
            // 恢复先前点击项目的背景颜色
            if (previouslyClickedPosition != -1 && previouslyClickedPosition != position) {
                calendarRecyclerView.getChildAt(previouslyClickedPosition).setBackgroundColor(Color.TRANSPARENT);
            }

            previouslyClickedPosition = position;

            // 在这里执行数据库操作
            String dateText = dayText + " " + monthYearFromDate(selectedDate);
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendPattern("[d][dd] MMMM yyyy")
                    .toFormatter();
            LocalDate date = LocalDate.parse(dateText, formatter);
            setEventView(date);
        }
    }




    //Event Part


}
