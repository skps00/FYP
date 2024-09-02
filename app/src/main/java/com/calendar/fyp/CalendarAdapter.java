package com.calendar.fyp;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>{
    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;
    private final String selectedDate;
    private Context context;
    private DBHelper dbHelper;
    private String TAG = "CalendarAdapter";
    private CalendarViewHolder holder;


    public CalendarAdapter(ArrayList<String> daysOfMonth, OnItemListener onItemListener, String selectedDate, Context context){
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
        this.selectedDate = selectedDate;
        this.context = context;
        this.dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        int currentPosition = holder.getAdapterPosition();
        this.holder = holder;
        String dayText = daysOfMonth.get(currentPosition);
        holder.dayOfMonth.setText(dayText);

        // 获取日期对应的星期几
        Calendar calendar = Calendar.getInstance();



        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");

        try {
            // 将月份名称解析为Date对象
            Date date = sdf.parse(selectedDate);
            // 创建一个Calendar实例，并将其设置为解析后的月份
            calendar.setTime(date);
        } catch (ParseException e) {
            try{
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy", Locale.CHINA);
                Date date = dateFormat.parse(selectedDate);
                calendar.setTime(date);
            }catch (ParseException e2){
            }
        }
        // Attempt to parse the text as an integer
        if (dayText != null && dayText != "" && !dayText.isEmpty()) {
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayText));
        }
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // 设置星期天的日期为红色
        if (dayOfWeek == Calendar.SUNDAY) {
            holder.dayOfMonth.setTextColor(Color.RED);
        }

        if (dayText != null && dayText != "" && !dayText.isEmpty()){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            String day = dayText;
            if (dayText.toCharArray().length == 1){
                day = "0" + dayText;
            }
            String mon = ""+month;
            if (mon.toCharArray().length ==1){
                mon = "0" + month;
            }

            LocalDate date = LocalDate.parse(day + "-" + mon + "-" + year, formatter);
            List<Event> eventArrayList = dbHelper.getEventsByDate(date);
            int numberOfevent = eventArrayList.size();
//            Log.v(TAG, "number of event: " + numberOfevent);
//            Log.v(TAG, "event list: " + dbHelper.getEventsByDate(date));
            if (numberOfevent > 0){
                View event = holder.itemView.findViewById(R.id.Calendar_Event_display);
                event.setVisibility(View.VISIBLE);
                switch (numberOfevent){
                    case 1:
                        event = holder.itemView.findViewById(R.id.event1);
                        event.setVisibility(View.GONE);

                        event = holder.itemView.findViewById(R.id.spaceInCell1);
                        event.setVisibility(View.GONE);

                        event = holder.itemView.findViewById(R.id.event2);
                        event.setVisibility(View.GONE);

                        event = holder.itemView.findViewById(R.id.spaceInCell2);
                        event.setVisibility(View.GONE);

                        chargeColor(holder.event3, eventArrayList.get(0).getCategory());
                        break;
                    case 2:
                        event = holder.itemView.findViewById(R.id.event1);
                        event.setVisibility(View.GONE);

                        event = holder.itemView.findViewById(R.id.spaceInCell1);
                        event.setVisibility(View.GONE);

                        chargeColor(holder.event2, eventArrayList.get(0).getCategory());
                        chargeColor(holder.event3, eventArrayList.get(1).getCategory());
                        break;
                    case 3:
                        chargeColor(holder.event1, eventArrayList.get(0).getCategory());
                        chargeColor(holder.event2, eventArrayList.get(1).getCategory());
                        chargeColor(holder.event3, eventArrayList.get(2).getCategory());
                        break;
                    default:
                        chargeColor(holder.event1, eventArrayList.get(0).getCategory());
                        chargeColor(holder.event2, eventArrayList.get(1).getCategory());
                        chargeColor(holder.event3, eventArrayList.get(2).getCategory());
                        break;
                }
            }else {
                View event = holder.itemView.findViewById(R.id.Calendar_Event_display);
                event.setVisibility(View.GONE);
            }


        }

        //高亮顯示今天
        try {
            int day = LocalDate.now().getDayOfMonth();
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            if (dayText != null &&
                    dayText != "" &&
                    Integer.parseInt(dayText) == day &&
                    !dayText.isEmpty() &&
                    month == LocalDate.now().getMonth().getValue() &&
                    year == LocalDate.now().getYear()) {
                holder.dayOfMonth.setBackgroundResource(R.drawable.rectangle_bg_deep_purple_a200_radius_7);
                holder.dayOfMonth.setTextColor(Color.WHITE);
//                Log.v(TAG, month + "");
            }
        } catch (Exception e) {
            // Handle the case where dayText cannot be parsed as an integer
            Log.v(TAG, "Error:" + e);
        }


        //click event
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理日期点击事件
                onItemListener.onItemClick(currentPosition, dayText);
            }
        });

    }


    public void chargeColor(ImageView view, String category){
        switch (category){
            case "study":
                view.setColorFilter(Color.RED);
                break;
            case "work":
                view.setColorFilter(Color.YELLOW);
                break;
            case "diet":
                view.setColorFilter(Color.GREEN);
                break;
            case "sport":
                view.setColorFilter(Color.BLUE);
                break;
            case "entertainment":
                view.setColorFilter(Color.MAGENTA);
                break;
            case "other":
                view.setColorFilter(Color.LTGRAY);
                break;

        }
    }

    @Override
    public int getItemCount(){
        return daysOfMonth.size();
    }

    public interface OnItemListener{
        void onItemClick(int position, String dayText);
    }

}