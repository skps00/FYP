package com.calendar.fyp;

import android.health.connect.datatypes.StepsRecord;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public final TextView dayOfMonth;
    public final ImageView event1;
    public final ImageView event2;
    public final ImageView event3;



    private final CalendarAdapter.OnItemListener onItemListener;
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener){
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        event1 = itemView.findViewById(R.id.event1);
        event2 = itemView.findViewById(R.id.event2);
        event3 = itemView.findViewById(R.id.event3);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);


    }

    @Override
    public void onClick(View view)
    {
        onItemListener.onItemClick(getAdapterPosition(), (String) dayOfMonth.getText());
    }
}