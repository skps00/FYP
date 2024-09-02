package com.calendar.fyp;


import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
public class EventViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{
    public final TextView eventTitle;
    public final TextView eventTime;
    public final TextView eventDescription;
    public final ImageView eventIcon;
    public long id;

    private final EventAdapter.OnItemListener onItemListener;
    public EventViewHolder(@NonNull View itemView, EventAdapter.OnItemListener onItemListener) {
        super(itemView);

        this.eventTitle = itemView.findViewById(R.id.event_title);
        this.eventTime = itemView.findViewById(R.id.event_time_period_text);
        this.eventDescription = itemView.findViewById(R.id.event_description);
        this.eventIcon = itemView.findViewById(R.id.event_icon);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View view)
    {
        onItemListener.onItemClick(getAdapterPosition(), (String) eventDescription.getText(), (String) eventTime.getText(), (String) eventTitle.getText(), (long) id);
    }
}
