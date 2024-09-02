package com.calendar.fyp;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "events";
    private static final int DATABASE_VERSION = 1;
    private final String TAG = "DBHelper";
    private Context context;
    private AlarmService alarmServiceInstance;


    public interface OnDatabaseChangedListener {
        void onDatabaseChanged();
    }

    private OnDatabaseChangedListener databaseChangedListener;

    public void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {
        Log.v(TAG, "listener seted ");
        Log.v(TAG, "service: " + alarmServiceInstance);
        this.databaseChangedListener = listener;
    }


    private ServiceConnection serviceConnection;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;



    }


    @Override
    public void onCreate(SQLiteDatabase db) {
//        try {
//            String databasePath = context.getDatabasePath("your_database_name").getAbsolutePath();
//            Log.v(TAG, databasePath);
//        }catch (Exception e){
//            Log.e(TAG, "Error: " + e );
//        }
//        deleteAllEvents();
//        deleteAllAlarm();
        // 创建表结构
        String createTableQuery = "CREATE TABLE events ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT,"
                + "startTime DATETIME,"
                + "endTime DATETIME default NULL,"
                + "description TEXT default NULL,"
                + "category TEXT default 'other'"
                + ")";
        db.execSQL(createTableQuery);
        // 创建表结构
        createTableQuery = "CREATE TABLE Alarms ("
                + "id INTEGER PRIMARY KEY,"
                + "timeInMillis LONG,"
                + "eventId INTEGER"
                + ")";
        db.execSQL(createTableQuery);

        String databasePath = context.getDatabasePath("events").getAbsolutePath();
        File databaseFile = new File(databasePath);
        boolean databaseExists = databaseFile.exists();
        Log.v(TAG, "Event DataBase: " + databaseExists);

        databasePath = context.getDatabasePath("Alarms").getAbsolutePath();
        databaseFile = new File(databasePath);
        databaseExists = databaseFile.exists();
        Log.v(TAG, "Alarm DataBase: " + databaseExists);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * From events", null, null);

        Log.v(TAG, "Boolean get all: " + cursor.moveToFirst());
        Log.v(TAG, "Column name: " + Arrays.toString(cursor.getColumnNames()) + " count: " + cursor.getColumnCount());
        Log.v(TAG, "entry: " + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int titleIndex = cursor.getColumnIndex("title");
                int startTimeIndex = cursor.getColumnIndex("startTime");
                int endTimeIndex = cursor.getColumnIndex("endTime");
                int descriptionIndex = cursor.getColumnIndex("description");
                int categoryIndex = cursor.getColumnIndex("category");

//                Log.v(TAG, "id: " + idIndex + " title: " + titleIndex + " Time: " + startTimeIndex + " End: " + endTimeIndex + " description: " + descriptionIndex + " category: " + categoryIndex);
                if (idIndex != -1 && titleIndex != -1 && startTimeIndex != -1 && endTimeIndex != -1 && descriptionIndex != -1 && categoryIndex != -1) {
                    String title = cursor.getString(titleIndex);
                    LocalDateTime startTime = LocalDateTime.parse(cursor.getString(startTimeIndex));
                    Event event = new Event(title, startTime);
                    event.setId(Long.parseLong(cursor.getString(idIndex)));
                    if (cursor.getString(endTimeIndex) != null && !cursor.getString(endTimeIndex).isEmpty() && !cursor.getString(endTimeIndex).equals("null")) {
                        LocalDateTime endTime = LocalDateTime.parse(cursor.getString(endTimeIndex));
                        event.setEndTime(endTime);
                    }
                    if (cursor.getString(descriptionIndex) != null && !cursor.getString(descriptionIndex).isEmpty() && !cursor.getString(descriptionIndex).equals("null")) {
                        String description = cursor.getString(descriptionIndex);
                        event.setDescription(description);
                    }
                    event.setCategory(cursor.getString(categoryIndex));
                    event.show(TAG);
                    eventList.add(event);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.v(TAG, "result Event: " + eventList);
        return eventList;
    }
    public List<Event> getEventsByDate(LocalDate date) {
        List<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

//        String query = "SELECT * FROM events WHERE startTime >= ? AND endTime <= ? ";
//        String[] selectionArgs = {date.atStartOfDay().toString(), date.plusDays(1).atStartOfDay().toString()};


        String query = "SELECT * FROM events WHERE DATE(startTime) = ? ORDER BY startTime ASC";
        String[] selectionArgs = {date.toString()};
        Cursor cursor = db.rawQuery(query, selectionArgs);
//        Log.v(TAG, "query: " + query);
//        Log.v(TAG, "Date: " + date);
//        Log.v(TAG, "Boolean get by date: " + cursor.moveToFirst());
//        Log.v(TAG, "Column name: " + Arrays.toString(cursor.getColumnNames()) + " count: " + cursor.getColumnCount());
//        Log.v(TAG, "entry: " + cursor.getCount());
//        Log.v(TAG, "\n\n\n\n\n");
//        getAllEvents();
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int titleIndex = cursor.getColumnIndex("title");
                int startTimeIndex = cursor.getColumnIndex("startTime");
                int endTimeIndex = cursor.getColumnIndex("endTime");
                int descriptionIndex = cursor.getColumnIndex("description");
                int categoryIndex = cursor.getColumnIndex("category");

//                Log.v(TAG, "id: " + idIndex + " title: " + titleIndex + " Time: " + startTimeIndex + " End: " + endTimeIndex + " description: " + descriptionIndex + " category: " + categoryIndex);
                if (idIndex != -1 && titleIndex != -1 && startTimeIndex != -1 && endTimeIndex != -1 && descriptionIndex != -1 && categoryIndex != -1) {
                    String title = cursor.getString(titleIndex);
                    LocalDateTime startTime = LocalDateTime.parse(cursor.getString(startTimeIndex));
                    Event event = new Event(title, startTime);
                    event.setId(Long.parseLong(cursor.getString(idIndex)));
                    if (cursor.getString(endTimeIndex) != null && !cursor.getString(endTimeIndex).isEmpty() && !cursor.getString(endTimeIndex).equals("null")) {
                        LocalDateTime endTime = LocalDateTime.parse(cursor.getString(endTimeIndex));
                        event.setEndTime(endTime);
                    }
                    if (cursor.getString(descriptionIndex) != null && !cursor.getString(descriptionIndex).isEmpty() && !cursor.getString(descriptionIndex).equals("null")) {
                        String description = cursor.getString(descriptionIndex);
                        event.setDescription(description);
                    }
//                    Log.v(TAG, "Title: " + event.getTitle() + " \nTime start: " + event.getStartTime() + " \nTime End: " + event.getEndTime());
                    event.setCategory(cursor.getString(categoryIndex));
                    eventList.add(event);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
//        Log.v(TAG, "result Event: " + eventList);
        return eventList;
    }

    public ArrayList<Event> getEventByTitleAndDateTime(String title, LocalDateTime startTime, LocalDateTime endTime) {
        ArrayList<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"id", "title", "startTime", "endTime", "description", "category"};
        String selection;
        String[] selectionArgs;
        if (endTime != null && !endTime.equals(null)) {
            Log.v(TAG, "Time per");
            selection = "title=? AND startTime >= ? AND startTime <= ?";
            selectionArgs = new String[]{title, startTime.toString(), endTime.toString()};
            Log.v(TAG, "Time per: " + startTime + " - " + endTime);
        }else {
            selection = "title=? AND startTime >= ?";
            selectionArgs = new String[]{title, startTime.toString()};

        }
        Cursor cursor = db.query("events", columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int titleIndex = cursor.getColumnIndex("title");
                int startTimeIndex = cursor.getColumnIndex("startTime");
                int endTimeIndex = cursor.getColumnIndex("endTime");
                int descriptionIndex = cursor.getColumnIndex("description");
                int categoryIndex = cursor.getColumnIndex("category");
//                Log.v(TAG, "id: " + idIndex + " title: " + titleIndex + " Time: " + startTimeIndex + " End: " + endTimeIndex + " description: " + descriptionIndex + " category: " + categoryIndex);

                if (idIndex != -1 && titleIndex != -1 && startTimeIndex != -1 && endTimeIndex != -1 && descriptionIndex != -1 && categoryIndex != -1) {
                    String eventtitle = cursor.getString(titleIndex);
                    LocalDateTime eventstartTime = LocalDateTime.parse(cursor.getString(startTimeIndex));
                    Event event = new Event( eventtitle, eventstartTime);
                    event.setId(Long.parseLong(cursor.getString(idIndex)));
                    if (cursor.getString(endTimeIndex) != null && !cursor.getString(endTimeIndex).isEmpty() && !cursor.getString(endTimeIndex).equals("null")) {
                        LocalDateTime eventendTime = LocalDateTime.parse(cursor.getString(endTimeIndex));
                        event.setEndTime(eventendTime);
                    }
                    if (cursor.getString(descriptionIndex) != null && !cursor.getString(descriptionIndex).isEmpty() && !cursor.getString(descriptionIndex).equals("null")) {
                        String description = cursor.getString(descriptionIndex);
                        event.setDescription(description);
                    }
                    event.setCategory(cursor.getString(categoryIndex));
                    eventList.add(event);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        getAllEvents();
        return eventList;
    }

    public ArrayList<Event> getEventByTitle(String title) {
        ArrayList<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {"id", "title", "startTime", "endTime", "description", "category"};
        String selection = "title=?";
        String[] selectionArgs = {title};

        Cursor cursor = db.query("events", columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int titleIndex = cursor.getColumnIndex("title");
                int startTimeIndex = cursor.getColumnIndex("startTime");
                int endTimeIndex = cursor.getColumnIndex("endTime");
                int descriptionIndex = cursor.getColumnIndex("description");
                int categoryIndex = cursor.getColumnIndex("category");
//                Log.v(TAG, "id: " + idIndex + " title: " + titleIndex + " Time: " + startTimeIndex + " End: " + endTimeIndex + " description: " + descriptionIndex + " category: " + categoryIndex);

                if (idIndex != -1 && titleIndex != -1 && startTimeIndex != -1 && endTimeIndex != -1 && descriptionIndex != -1 && categoryIndex != -1) {
                    String eventtitle = cursor.getString(titleIndex);
                    LocalDateTime eventstartTime = LocalDateTime.parse(cursor.getString(startTimeIndex));
                    Event event = new Event( eventtitle, eventstartTime);
                    event.setId(Long.parseLong(cursor.getString(idIndex)));
                    if (cursor.getString(endTimeIndex) != null && !cursor.getString(endTimeIndex).isEmpty() && !cursor.getString(endTimeIndex).equals("null")) {
                        LocalDateTime eventendTime = LocalDateTime.parse(cursor.getString(endTimeIndex));
                        event.setEndTime(eventendTime);
                    }
                    if (cursor.getString(descriptionIndex) != null && !cursor.getString(descriptionIndex).isEmpty() && !cursor.getString(descriptionIndex).equals("null")) {
                        String description = cursor.getString(descriptionIndex);
                        event.setDescription(description);
                    }
                    event.setCategory(cursor.getString(categoryIndex));
                    eventList.add(event);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return eventList;
    }




    public ArrayList<Event> getEventByTitleAndCalegory(String title, String category) {
        ArrayList<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {"id", "title", "startTime", "endTime", "description", "category"};
        String selection = "title=? AND category=?";
        String[] selectionArgs = {title, category};

        Cursor cursor = db.query("events", columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int titleIndex = cursor.getColumnIndex("title");
                int startTimeIndex = cursor.getColumnIndex("startTime");
                int endTimeIndex = cursor.getColumnIndex("endTime");
                int descriptionIndex = cursor.getColumnIndex("description");
                int categoryIndex = cursor.getColumnIndex("category");
//                Log.v(TAG, "id: " + idIndex + " title: " + titleIndex + " Time: " + startTimeIndex + " End: " + endTimeIndex + " description: " + descriptionIndex + " category: " + categoryIndex);

                if (idIndex != -1 && titleIndex != -1 && startTimeIndex != -1 && endTimeIndex != -1 && descriptionIndex != -1 && categoryIndex != -1) {
                    String eventtitle = cursor.getString(titleIndex);
                    LocalDateTime eventstartTime = LocalDateTime.parse(cursor.getString(startTimeIndex));
                    Event event = new Event( eventtitle, eventstartTime);
                    event.setId(Long.parseLong(cursor.getString(idIndex)));
                    if (cursor.getString(endTimeIndex) != null && !cursor.getString(endTimeIndex).isEmpty() && !cursor.getString(endTimeIndex).equals("null")) {
                        LocalDateTime eventendTime = LocalDateTime.parse(cursor.getString(endTimeIndex));
                        event.setEndTime(eventendTime);
                    }
                    if (cursor.getString(descriptionIndex) != null && !cursor.getString(descriptionIndex).isEmpty() && !cursor.getString(descriptionIndex).equals("null")) {
                        String description = cursor.getString(descriptionIndex);
                        event.setDescription(description);
                    }
                    event.setCategory(cursor.getString(categoryIndex));
                    eventList.add(event);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return eventList;
    }


    public ArrayList<Event> getEventsByDateTime(LocalDateTime startTime, LocalDateTime endTime) {
        ArrayList<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
//        String[] columns = {"id", "title", "startTime", "endTime", "description", "category"};
        String sql;
        String[] selectionArgs;
        if (endTime != null) {
            sql = "SELECT * FROM events WHERE startTime >= ? AND startTime <= ?";
            selectionArgs = new String[]{startTime.toString(), endTime.toString()};
            Log.v(TAG, "Test");
        }else {
            sql = "SELECT * FROM events WHERE startTime >= ?";
            selectionArgs = new String[]{startTime.toString()};

        }
        Cursor cursor = db.rawQuery(sql, selectionArgs);

//        Log.v(TAG, "Date: " + startTime + " - " + endTime);
//        Log.v(TAG, "Boolean get by date: " + cursor.moveToFirst());
//        Log.v(TAG, "Column name: " + Arrays.toString(cursor.getColumnNames()) + " count: " + cursor.getColumnCount());
//        Log.v(TAG, "entry: " + cursor.getCount());
//        Log.v(TAG, "\n\n\n\n\n");
//        getAllEvents();

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int titleIndex = cursor.getColumnIndex("title");
                int startTimeIndex = cursor.getColumnIndex("startTime");
                int endTimeIndex = cursor.getColumnIndex("endTime");
                int descriptionIndex = cursor.getColumnIndex("description");
                int categoryIndex = cursor.getColumnIndex("category");
//                Log.v(TAG, "id: " + idIndex + " title: " + titleIndex + " Time: " + startTimeIndex + " End: " + endTimeIndex + " description: " + descriptionIndex + " category: " + categoryIndex);

                if (idIndex != -1 && titleIndex != -1 && startTimeIndex != -1 && endTimeIndex != -1 && descriptionIndex != -1 && categoryIndex != -1) {
                    String eventtitle = cursor.getString(titleIndex);
                    LocalDateTime eventstartTime = LocalDateTime.parse(cursor.getString(startTimeIndex));
                    Event event = new Event( eventtitle, eventstartTime);
                    event.setId(Long.parseLong(cursor.getString(idIndex)));
                    if (cursor.getString(endTimeIndex) != null && !cursor.getString(endTimeIndex).isEmpty() && !cursor.getString(endTimeIndex).equals("null")) {
                        LocalDateTime eventendTime = LocalDateTime.parse(cursor.getString(endTimeIndex));
                        event.setEndTime(eventendTime);
                    }
                    if (cursor.getString(descriptionIndex) != null && !cursor.getString(descriptionIndex).isEmpty() && !cursor.getString(descriptionIndex).equals("null")) {
                        String description = cursor.getString(descriptionIndex);
                        event.setDescription(description);
                    }
                    event.setCategory(cursor.getString(categoryIndex));
                    eventList.add(event);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return eventList;
    }
    public ArrayList<Event> getEventsByDateTimeAndCalegory(LocalDateTime startTime, LocalDateTime endTime, String category) {
        ArrayList<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
//        String[] columns = {"id", "title", "startTime", "endTime", "description", "category"};
        String sql;
        String[] selectionArgs;
        if (endTime != null) {
            sql = "SELECT * FROM events WHERE startTime >= ? AND startTime <= ? AND category = ?";
            selectionArgs = new String[]{startTime.toString(), endTime.toString(), category};
        }else {
            sql = "SELECT * FROM events WHERE startTime >= ? AND category = ?";
            selectionArgs = new String[]{startTime.toString(), category};

        }
        Log.v(TAG, "Date: " + startTime + " - " + endTime);
        Cursor cursor = db.rawQuery(sql, selectionArgs);

//        Log.v(TAG, "Date: " + startTime + " - " + endTime);
//        Log.v(TAG, "Boolean get by date: " + cursor.moveToFirst());
//        Log.v(TAG, "Column name: " + Arrays.toString(cursor.getColumnNames()) + " count: " + cursor.getColumnCount());
//        Log.v(TAG, "entry: " + cursor.getCount());
//        Log.v(TAG, "\n\n\n\n\n");
//        getAllEvents();

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int titleIndex = cursor.getColumnIndex("title");
                int startTimeIndex = cursor.getColumnIndex("startTime");
                int endTimeIndex = cursor.getColumnIndex("endTime");
                int descriptionIndex = cursor.getColumnIndex("description");
                int categoryIndex = cursor.getColumnIndex("category");
//                Log.v(TAG, "id: " + idIndex + " title: " + titleIndex + " Time: " + startTimeIndex + " End: " + endTimeIndex + " description: " + descriptionIndex + " category: " + categoryIndex);

                if (idIndex != -1 && titleIndex != -1 && startTimeIndex != -1 && endTimeIndex != -1 && descriptionIndex != -1 && categoryIndex != -1) {
                    String eventtitle = cursor.getString(titleIndex);
                    LocalDateTime eventstartTime = LocalDateTime.parse(cursor.getString(startTimeIndex));
                    Event event = new Event( eventtitle, eventstartTime);
                    event.setId(Long.parseLong(cursor.getString(idIndex)));
                    if (cursor.getString(endTimeIndex) != null && !cursor.getString(endTimeIndex).isEmpty() && !cursor.getString(endTimeIndex).equals("null")) {
                        LocalDateTime eventendTime = LocalDateTime.parse(cursor.getString(endTimeIndex));
                        event.setEndTime(eventendTime);
                    }
                    if (cursor.getString(descriptionIndex) != null && !cursor.getString(descriptionIndex).isEmpty() && !cursor.getString(descriptionIndex).equals("null")) {
                        String description = cursor.getString(descriptionIndex);
                        event.setDescription(description);
                    }
                    event.setCategory(cursor.getString(categoryIndex));
                    eventList.add(event);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return eventList;
    }

    public ArrayList<Event> getEventByCalegory(String category){

        ArrayList<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql;
        String[] selectionArgs;
        sql = "SELECT * FROM events WHERE category=?";
        selectionArgs = new String[]{category};
        Cursor cursor = db.rawQuery(sql, selectionArgs);

//        Log.v(TAG, "Date: " + startTime + " - " + endTime);
//        Log.v(TAG, "Boolean get by date: " + cursor.moveToFirst());
//        Log.v(TAG, "Column name: " + Arrays.toString(cursor.getColumnNames()) + " count: " + cursor.getColumnCount());
//        Log.v(TAG, "entry: " + cursor.getCount());
//        Log.v(TAG, "\n\n\n\n\n");
//        getAllEvents();

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int titleIndex = cursor.getColumnIndex("title");
                int startTimeIndex = cursor.getColumnIndex("startTime");
                int endTimeIndex = cursor.getColumnIndex("endTime");
                int descriptionIndex = cursor.getColumnIndex("description");
                int categoryIndex = cursor.getColumnIndex("category");
//                Log.v(TAG, "id: " + idIndex + " title: " + titleIndex + " Time: " + startTimeIndex + " End: " + endTimeIndex + " description: " + descriptionIndex + " category: " + categoryIndex);

                if (idIndex != -1 && titleIndex != -1 && startTimeIndex != -1 && endTimeIndex != -1 && descriptionIndex != -1 && categoryIndex != -1) {
                    String eventtitle = cursor.getString(titleIndex);
                    LocalDateTime eventstartTime = LocalDateTime.parse(cursor.getString(startTimeIndex));
                    Event event = new Event( eventtitle, eventstartTime);
                    event.setId(Long.parseLong(cursor.getString(idIndex)));
                    if (cursor.getString(endTimeIndex) != null && !cursor.getString(endTimeIndex).isEmpty() && !cursor.getString(endTimeIndex).equals("null")) {
                        LocalDateTime eventendTime = LocalDateTime.parse(cursor.getString(endTimeIndex));
                        event.setEndTime(eventendTime);
                    }
                    if (cursor.getString(descriptionIndex) != null && !cursor.getString(descriptionIndex).isEmpty() && !cursor.getString(descriptionIndex).equals("null")) {
                        String description = cursor.getString(descriptionIndex);
                        event.setDescription(description);
                    }
                    event.setCategory(cursor.getString(categoryIndex));
                    event.show(TAG);
                    eventList.add(event);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return eventList;
    }


    public Event getEventById(long eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"id", "title", "startTime", "endTime", "description", "category"};
        String selection = "id=?";
        String[] selectionArgs = {String.valueOf(eventId)};

        Cursor cursor = db.query("events", columns, selection, selectionArgs, null, null, null);

        Event event = null;
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");
            int titleIndex = cursor.getColumnIndex("title");
            int startTimeIndex = cursor.getColumnIndex("startTime");
            int endTimeIndex = cursor.getColumnIndex("endTime");
            int descriptionIndex = cursor.getColumnIndex("description");
            int categoryIndex = cursor.getColumnIndex("category");

            if (idIndex != -1 && titleIndex != -1 && startTimeIndex != -1 && endTimeIndex != -1 && descriptionIndex != -1 && categoryIndex != -1) {
                String title = cursor.getString(titleIndex);
                if (cursor.getString(startTimeIndex) != null && !cursor.getString(startTimeIndex).isEmpty() && !cursor.getString(startTimeIndex).equals("null")) {
                    LocalDateTime startTime = LocalDateTime.parse(cursor.getString(startTimeIndex));
                    event = new Event(title, startTime);
                }else {
                    Log.v(TAG, "FUCK MY LIFE Twice");
                }
                event.setId(cursor.getInt(idIndex));
                if (cursor.getString(endTimeIndex) != null && !cursor.getString(endTimeIndex).isEmpty() && !cursor.getString(endTimeIndex).equals("null")) {
                    LocalDateTime endTime = LocalDateTime.parse(cursor.getString(endTimeIndex));
                    event.setEndTime(endTime);
                }
                if (cursor.getString(descriptionIndex) != null && !cursor.getString(descriptionIndex).isEmpty() && !cursor.getString(descriptionIndex).equals("null")) {
                    String description = cursor.getString(descriptionIndex);
                    event.setDescription(description);
                }
                String category = cursor.getString(categoryIndex);
                event.setCategory(category);
            }
        }

        cursor.close();
        db.close();

        return event;
    }




    public ArrayList<Event> getEventByTitleAndDateTimeAndCalegory(String title, LocalDateTime startTime, LocalDateTime endTime, String category) {
        ArrayList<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"id", "title", "startTime", "endTime", "description", "category"};
        String selection;
        String[] selectionArgs;
        if (endTime != null && !endTime.equals(null)) {
            selection = "title=? AND startTime >= ? AND startTime <= ? AND category=?";
            selectionArgs = new String[]{title, startTime.toString(), endTime.toString(), category};
        }else {
            selection = "title=? AND startTime >= ? AND category=?";
            selectionArgs = new String[]{title, startTime.toString(), category};
        }
        Cursor cursor = db.query("events", columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int titleIndex = cursor.getColumnIndex("title");
                int startTimeIndex = cursor.getColumnIndex("startTime");
                int endTimeIndex = cursor.getColumnIndex("endTime");
                int descriptionIndex = cursor.getColumnIndex("description");
                int categoryIndex = cursor.getColumnIndex("category");
//                Log.v(TAG, "id: " + idIndex + " title: " + titleIndex + " Time: " + startTimeIndex + " End: " + endTimeIndex + " description: " + descriptionIndex + " category: " + categoryIndex);

                if (idIndex != -1 && titleIndex != -1 && startTimeIndex != -1 && endTimeIndex != -1 && descriptionIndex != -1 && categoryIndex != -1) {
                    String eventtitle = cursor.getString(titleIndex);
                    LocalDateTime eventstartTime = LocalDateTime.parse(cursor.getString(startTimeIndex));
                    Event event = new Event( eventtitle, eventstartTime);
                    event.setId(Long.parseLong(cursor.getString(idIndex)));
                    if (cursor.getString(endTimeIndex) != null && !cursor.getString(endTimeIndex).isEmpty() && !cursor.getString(endTimeIndex).equals("null")) {
                        LocalDateTime eventendTime = LocalDateTime.parse(cursor.getString(endTimeIndex));
                        event.setEndTime(eventendTime);
                    }
                    if (cursor.getString(descriptionIndex) != null && !cursor.getString(descriptionIndex).isEmpty() && !cursor.getString(descriptionIndex).equals("null")) {
                        String description = cursor.getString(descriptionIndex);
                        event.setDescription(description);
                    }
                    event.setCategory(cursor.getString(categoryIndex));
                    eventList.add(event);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return eventList;
    }

    public long addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", event.getTitle());
        values.put("startTime", event.getStartTime().toString());
        if (event.getEndTime() != null){
            values.put("endTime", event.getEndTime().toString());
        }
        if (event.getDescription() != null){
            values.put("description", event.getDescription());
        }
        if (event.getCategory() == null){
            values.put("category", "other");
        }else {
            values.put("category", event.getCategory());
        }

        long eventId = db.insert("events", null, values);
//        event.show(TAG);
//        Log.v(TAG, "Add Event: " + eventId);
        db.close();
//        Log.v(TAG, eventId + ": " + getEventById(eventId).getTitle() );
//        getAllEvents();
        return eventId;
    }





    public void EditEvent(long id, Event new_event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", new_event.getTitle());
        values.put("startTime", String.valueOf(new_event.getStartTime()));
        values.put("endTime", String.valueOf(new_event.getEndTime()));
        values.put("description", new_event.getDescription());
        values.put("category", new_event.getCategory());

        String whereClause = "id=?";
        String[] whereArgs = {String.valueOf(id)};

        db.update("events", values, whereClause, whereArgs);
        db.close();
    }



    public void deleteEventById(long eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "id=?";
        String[] whereArgs = {String.valueOf(eventId)};
        db.delete("events", whereClause, whereArgs);
        db.close();
        deleteAlarmByEventId(eventId);
    }
    public void deleteAllEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("events", null, null);
        db.close();
        context.deleteDatabase("events");
    }

    public void deleteAllAlarm() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Alarm", null, null);
        db.close();
        context.deleteDatabase("events");
        DBChange();
    }



    public ArrayList<Alarm> getAllAlarm(){
        ArrayList<Alarm> alarmArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * From Alarms", null, null);

//        Log.v(TAG, "Boolean get all: " + cursor.moveToFirst());
//        Log.v(TAG, "Column name: " + Arrays.toString(cursor.getColumnNames()) + " count: " + cursor.getColumnCount());
//        Log.v(TAG, "entry: " + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {

                int eventIdIndex = cursor.getColumnIndex("eventId");
                int idIndex = cursor.getColumnIndex("id");
                int timeInMillisIndex = cursor.getColumnIndex("timeInMillis");

//                Log.v(TAG, "id: " + idIndex + " timeInMillis: " + timeInMillisIndex);
                if (idIndex != -1 && timeInMillisIndex != -1 && eventIdIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    long eventId = cursor.getInt(eventIdIndex);
                    Long timeInMillis = cursor.getLong(timeInMillisIndex);

//                    Log.v(TAG, "Title: " + event.getTitle() + " \nTime start: " + event.getStartTime() + " \nTime End: " + event.getEndTime());
                    alarmArrayList.add(new Alarm(id, timeInMillis, eventId));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
//        Log.v(TAG, "result Event: " + eventList);
        return alarmArrayList;
    }


    public long addAlarm(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", alarm.getId());
        values.put("timeInMillis", alarm.getTimeInMillis());
        values.put("eventId", alarm.getEventId());

        long alarmId = db.insert("Alarms", null, values);
        Log.v(TAG, "Add Alarm: ");
        getEventById(alarm.getEventId()).show(TAG);
        db.close();
//        getAllEvents();
        DBChange();
        return alarmId;
    }

    public boolean deleteAlarm(int id) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            String whereClause = "id=?";
            String[] whereArgs = {String.valueOf(id)};
            db.delete("Alarms", whereClause, whereArgs);
            db.close();
            DBChange();
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public boolean deleteAlarmByEventId(long eventId) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            String whereClause = "eventId=?";
            String[] whereArgs = {String.valueOf(eventId)};
            db.delete("Alarms", whereClause, whereArgs);
            db.close();
            DBChange();
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public void EditAlarm(long eventId, Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", alarm.getId());
        values.put("timeInMillis", alarm.getTimeInMillis());
        values.put("eventId", alarm.getEventId());

        String whereClause = "eventId=?";
        String[] whereArgs = {String.valueOf(eventId)};

        db.update("Alarms", values, whereClause, whereArgs);
        db.close();
        DBChange();
    }

    public Alarm getAlarmByEventId(long eventId) {
        Alarm alarm = null;

        String whereClause = "eventId=?";

        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {"id", "timeInMillis", "eventId"};
        String selection = "eventId=?";
        String[] selectionArgs = {String.valueOf(eventId)};

        Cursor cursor = db.query("Alarms", columns, selection, selectionArgs, null, null, null);

//        Log.v(TAG, "Boolean get all: " + cursor.moveToFirst());
//        Log.v(TAG, "Column name: " + Arrays.toString(cursor.getColumnNames()) + " count: " + cursor.getColumnCount());
//        Log.v(TAG, "entry: " + cursor.getCount());

        if (cursor.moveToFirst()) {

            int eventIdIndex = cursor.getColumnIndex("eventId");
            int idIndex = cursor.getColumnIndex("id");
            int timeInMillisIndex = cursor.getColumnIndex("timeInMillis");

//                Log.v(TAG, "id: " + idIndex + " timeInMillis: " + timeInMillisIndex);
            if (idIndex != -1 && timeInMillisIndex != -1 && eventIdIndex != -1) {
                int id = cursor.getInt(idIndex);
                long eventid = cursor.getInt(eventIdIndex);
                Long timeInMillis = cursor.getLong(timeInMillisIndex);

//                    Log.v(TAG, "Title: " + event.getTitle() + " \nTime start: " + event.getStartTime() + " \nTime End: " + event.getEndTime());
                alarm = new Alarm(id, timeInMillis, eventid);
            }
        }
        cursor.close();
        db.close();
//        Log.v(TAG, "result Event: " + eventList);
        return alarm;
    }

    private void DBChange(){
        // 在数据库更改的地方发送通知
        Log.v(TAG, "running");
        // 创建Intent对象，指定要绑定的AlarmService
        Intent serviceIntent = new Intent(context, AlarmService.class);


        // 创建ServiceConnection对象
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                // 当服务绑定成功时，此方法会被调用
                // 在此方法中可以获取服务实例并进行相应的操作
                Log.v(TAG, "Service connected");
                AlarmService.MyBinder alarmBinder = (AlarmService.MyBinder) binder;
//                Log.v(TAG, "Service: " + alarmBinder.getService());
                alarmServiceInstance = alarmBinder.getService();
//                Log.v(TAG, "testing 123: " + alarmServiceInstance);
                setOnDatabaseChangedListener(alarmServiceInstance);
//                Log.v(TAG, "testing 101: " + databaseChangedListener);
                if (databaseChangedListener != null) {
                    Log.v(TAG, "listener running");
                    databaseChangedListener.onDatabaseChanged();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // 当服务断开连接时，此方法会被调用
                // 在此方法中可以进行相应的处理
                Log.v(TAG, "Service disconnected");
                alarmServiceInstance = null;
            }
        };


        // 绑定AlarmService
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);



    }



}