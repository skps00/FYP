package com.calendar.fyp;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.time.LocalTime;

public class Alarm implements Parcelable {
    private int id;
    private long timeInMillis;
    private long eventId;
    private Context context;

    public Alarm(int id, long timeInMillis, long eventId) {
        this.id = id;
        this.timeInMillis = timeInMillis;
        this.eventId = eventId;
    }
    protected Alarm(Parcel in) {
        id = in.readInt();
        timeInMillis = in.readLong();
        eventId = in.readLong();
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public long getEventId() {
        return eventId;
    }
    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }


    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(timeInMillis);
        dest.writeLong(eventId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void show(String TAG) {
        Log.v(TAG, "id: " + id);
        Log.v(TAG, "event id: " + eventId);
        Log.v(TAG, "timeInMillis: " + timeInMillis);

    }
}