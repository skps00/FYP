package com.calendar.fyp;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

class EventAdapter extends RecyclerView.Adapter<EventViewHolder>{
    List<Event> eventList;
    OnItemListener onItemListener;
    boolean isDate;

    private String TAG = "EventAdapter";
    public EventAdapter(List<Event> eventList, OnItemListener onItemListener, boolean isDate){
        this.onItemListener = onItemListener;
        this.eventList = eventList;
        this.isDate = isDate;
        Log.v(TAG, "isDate: " + isDate);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.event_item, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;




        return new EventViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        DateTimeFormatter DateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        DateTimeFormatter NewDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        if (event != null) {
            if (event.isTimePeriod() && event.getEndTime() != null){
                LocalDateTime stime = event.getStartTime();
                LocalDateTime etime = event.getEndTime();
                String[] sparts;
                String[] eparts;
                String displayTime;

                if (isDate){
                    sparts = stime.toString().split(":");
                    eparts = etime.toLocalTime().toString().split(":");
                    String smodifiedTimeString = sparts[0] + ":" + sparts[1];
                    String emodifiedTimeString = eparts[0] + ":" + eparts[1];

                    displayTime = LocalDateTime.parse(smodifiedTimeString, DateFormatter).format(NewDateFormatter) + " - " +
                            LocalTime.parse(emodifiedTimeString, formatter);
                }else {
                    sparts = stime.toLocalTime().toString().split(":");
                    eparts = etime.toLocalTime().toString().split(":");
                    String smodifiedTimeString = sparts[0] + ":" + sparts[1];
                    String emodifiedTimeString = eparts[0] + ":" + eparts[1];

                    displayTime = LocalTime.parse(smodifiedTimeString, formatter) + " - " +
                            LocalTime.parse(emodifiedTimeString, formatter);
                }




                holder.eventTime.setText(displayTime);
            }else {
                LocalDateTime time = event.getStartTime();
                String[] parts;
                if (isDate){
                    parts = time.toString().split(":");
                }else {
                    parts = time.toLocalTime().toString().split(":");
                }

                String modifiedTimeString = parts[0] + ":" + parts[1];

                String displayTime;
                if (isDate){;
                    displayTime = String.valueOf(LocalDateTime.parse(modifiedTimeString, DateFormatter).format(NewDateFormatter));
                }else {
                    displayTime = String.valueOf(LocalTime.parse(modifiedTimeString, formatter));
                }

                holder.eventTime.setText(displayTime);
            }

            if (event.getStartTime().isBefore(LocalDateTime.now())){
                holder.eventTitle.setTextColor(Color.GRAY);
            }else {
                holder.eventTitle.setTextColor(Color.BLACK);
            }

            switch (event.getCategory()){
                case "study":
                    holder.eventIcon.setColorFilter(Color.RED);
                    break;
                case "work":
                    holder.eventIcon.setColorFilter(Color.YELLOW);
                    break;
                case "diet":
                    holder.eventIcon.setColorFilter(Color.GREEN);
                    break;
                case "sport":
                    holder.eventIcon.setColorFilter(Color.BLUE);
                    break;
                case "entertainment":
                    holder.eventIcon.setColorFilter(Color.MAGENTA);
                    break;
                case "other":
                    holder.eventIcon.setColorFilter(Color.LTGRAY);
                    break;

            }
            holder.eventTitle.setText(event.getTitle());
            holder.eventDescription.setText(event.getDescription());
            holder.id = event.getId();
        }

    }


    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public interface OnItemListener {
        void onItemClick(int Position, String text, String eventTimeText, String eventTitleText, long id);
    }
}
