package com.calendar.fyp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.ViewTreeObserver;
import android.graphics.Rect;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;


import org.threeten.bp.LocalDate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BottomDialog extends Dialog implements ChatMessageAdapter.DataCallback{

    private int KeyBoardHeight;
    private int KeyBoardHeight_Hidden;
    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private ImageButton SendButton;
    private ImageButton ClearButton;
    private List<Message> messageList = null;
    private ChatMessageAdapter chatMessageAdapter = null;
    private Context context;
    private response Response;
    private String TAG = "Dialog";
    private Event newEvent;
    private long AddedEventId;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener;
    private ScrollView scrollView;
    private LinearLayout contentLayout;
    private DBHelper dbHelper;
    private String userInput;
    public BottomDialog(Context context) {
        super(context);
        this.context = context;
    }
    private OnDialogUpdateListener listener;

    // 在合适的位置定义接口
    public interface OnDialogUpdateListener {
        void onUpdateData(String newData);
    }

    // 设置监听器的方法
    public void setOnDialogUpdateListener(OnDialogUpdateListener listener) {
        this.listener = listener;
    }

    // 在合适的位置调用接口方法
    private void updateCalendarFragment(String newData) {
        if (listener != null) {
            listener.onUpdateData(newData);
        } else {
            Log.v(TAG, "Test123");
        }

    }


    @Override
    public void dismiss() {
        super.dismiss();
        MessageListManager.saveMessageList(messageList); // 將消息列表保存到 MessageListManager

        updateCalendarFragment(LocalDate.now().toString());
        DialogUtils.setDialogShowing(false);
    }



    public response getDialog(String Message){
        DialogflowManager dialogflowManager = new DialogflowManager(context);
        response response = dialogflowManager.sendQuery(Message, "en-US");
//        Log.v(TAG, "getDialog: " + (response.getClass().getSimpleName().toString().equals("response")));
//        Log.v(TAG, "getDialog: " + response);
        return response;
    }

    public String getCurrentTime(){
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        return currentTime.format(formatter);
    }
    public void showBottomDialog() {
        if (DialogUtils.isDialogShowing()){
            Log.v(TAG, "test");
            return;
        }else {
            Log.v(TAG, "test2");
            DialogUtils.setDialogShowing(true);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        recyclerView = dialog.findViewById(R.id.ChatBox);
        editTextMessage = dialog.findViewById(R.id.TextMessage);
        SendButton = dialog.findViewById(R.id.sendBtn);
        ClearButton = dialog.findViewById(R.id.clearBtn);



        if(!MessageListManager.getMessageList().isEmpty() || MessageListManager.getMessageList() != null){
            messageList = MessageListManager.getMessageList();
        }

        Resources resources = context.getResources();

        ClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Cleaning the Message");
                builder.setMessage("Are you sure want to clean message?");
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        MessageListManager.clearMessage();
                        messageList.clear();
                        chatMessageAdapter.notifyDataSetChanged();

                        dialog.dismiss(); // 關閉對話框
                    }
                });

                builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // 關閉對話框
                    }
                });

                // 顯示對話框
                builder.create().show();


            }
        });
        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "SendButton pressed");
                String message = editTextMessage.getText().toString().trim();
                userInput = message;
                if (!message.isEmpty() && message.toCharArray().length < 256) {


                    editTextMessage.setText("");
                    messageList.add(new Message("User", message, getCurrentTime(), 0));
                    chatMessageAdapter.notifyItemInserted(chatMessageAdapter.getItemCount()-1);
                    Response = getDialog(message);
//                    Log.v(TAG, "response: " + Response);
                    if (Response != null) {  // 添加空值检查
//                        Log.v(TAG, Response.getAction()-1);
                        handleResponse(Response, message);
                    }else {
                        messageList.add(new Message("System", "something go wrong", getCurrentTime(), 1));
                    }

                    recyclerView.smoothScrollToPosition(chatMessageAdapter.getItemCount()-1);
                }else if (message.toCharArray().length >= 256){

                    Toast.makeText(context, resources.getString(R.string.Error_message_long), Toast.LENGTH_LONG).show();
                } else if (message.isEmpty()) {
                    Toast.makeText(context, resources.getString(R.string.Error_message_null), Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(context,  resources.getString(R.string.Error_message), Toast.LENGTH_LONG).show();
                }
            }
        });


        if (chatMessageAdapter == null){
            chatMessageAdapter = new ChatMessageAdapter(messageList, context);
        }
        recyclerView.setAdapter(chatMessageAdapter);
        recyclerView.setLayoutManager(layoutManager);






        Window dialogWindow = dialog.getWindow();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialogWindow.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.8);
        dialogWindow.setAttributes(layoutParams);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        final View decorView = dialog.getWindow().getDecorView();
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean isKeyboardShowing = false;

            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();

                decorView.getWindowVisibleDisplayFrame(rect);


                int keyboardHeight = screenHeight - rect.bottom;
                Log.v(TAG, "screenHeight: " + screenHeight + " - " + "rect.bottom: " + rect.bottom);

                if (keyboardHeight > screenHeight * 0.04) {
                    isKeyboardShowing = true;
                    KeyBoardHeight = keyboardHeight;

                } else{
                    if (keyboardHeight < 0){
                        KeyBoardHeight_Hidden = keyboardHeight * -1;
                    }else {
                        KeyBoardHeight_Hidden = keyboardHeight;
                    }
                    isKeyboardShowing = false;
                }


                int dialogHeight = 0;
                WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();

                if (isKeyboardShowing && layoutParams.height >= (int) (screenHeight * 0.8)) {
                    Log.v(TAG, "showing");
//                    int desiredHeight = (int) (screenHeight * 0.8);
                    dialogHeight = layoutParams.height - (KeyBoardHeight + KeyBoardHeight_Hidden);
                    layoutParams.height = dialogHeight;
                    dialog.getWindow().setAttributes(layoutParams);
                }


            }
        });



        dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWindow.getAttributes().windowAnimations = R.style.DialogAnimation;
        dialogWindow.setGravity(Gravity.BOTTOM);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // Code to handle the dialog cancellation

                updateCalendarFragment(LocalDate.now().toString());
                DialogUtils.setDialogShowing(false);
            }
        });
        updateCalendarFragment(LocalDate.now().toString());

        dialogWindow.getDecorView().setOnTouchListener(new View.OnTouchListener() {
            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float deltaY = event.getRawY() - startY;
                        if (deltaY > 10) {
                            dialog.dismiss();

                            DialogUtils.setDialogShowing(false);
                            updateCalendarFragment(LocalDate.now().toString());
                            return true;
                        }
                        break;
                }
                return false;
            }
        });
        dialog.show();

    }

    public void handleResponse(response response, String message){
        Event event;
        ArrayList<Event> result = null;

        dbHelper = new DBHelper(context);
        Log.v(TAG, response.getAction());
        switch (response.getAction()){

            case "Adding_New_Event_Intent":

                Log.v(TAG, "Adding Event");




                if (response.getStartDateTime() != null && response.getTitle() != null){



//                    Fuck this shit I'm OUT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//                    if (message.toLowerCase().contains("today")){
//                        ZoneId ZID = ZoneId.of(TimeZone.getDefault().getID());
//
//                        ZonedDateTime zonedStartDateTime = response.getStartDateTimeWithZone();
//                        zonedStartDateTime = zonedStartDateTime.withZoneSameInstant(ZID);
//                        Log .v(TAG, "Zoned Date Time: " + zonedStartDateTime);
//                        Log.v(TAG, "before Zoned Date Time: " + response.getStartDateTimeWithZone());
////                        String DateTime = zonedDateTime.toLocalDate() + "T" + response.getStartDateTime().toLocalTime();
//                        LocalDateTime DateTime = LocalDateTime.of(zonedStartDateTime.toLocalDate(),response.getStartDateTime().toLocalTime());
//                        Log.v(TAG, "Final Date Time: " + DateTime);
//                        response.setStartDateTime(DateTime);
//
//                        if (response.getEndDateTime())
//
//
//                    }
//                    Log.v(TAG, "testing response: " +response.getTitle());
                    event = new Event(response.getTitle(), response.getStartDateTime());

                    if (response.getEndDateTime()!=null && !response.getEndDateTime().equals("null")){
                        event.setEndTime(response.getEndDateTime());
                    }
                    if (response.getDescription()!=null && !response.getDescription().isEmpty() && !response.getDescription().equals("null")){
                        event.setDescription(response.getDescription());
                    }


//                event.setCategory();

                    Long eventId = dbHelper.addEvent(event);
//                    Log.v(TAG, response.getStartDateTime() + "test");

                    this.AddedEventId = eventId;



                    chatMessageAdapter.setDataCallback(this);
                    messageList.add(new Message("System"
                            , "Please choose a Category. If you don't choose, it will default \"other\" Category!"
                            , getCurrentTime(), 3));
                    chatMessageAdapter.notifyItemInserted(chatMessageAdapter.getItemCount()-1);

                    if (LocalDateTime.now().isBefore(response.getStartDateTime()) || response.getStartDateTime().isEqual(LocalDateTime.now().withHour(12).withMinute(0).withSecond(0).withNano(0))){
                        // 創建PendingIntent對象
                        int requestCode = (int) (eventId ^ (eventId >>> 32)); // 使用哈希值生成唯一的requestCode
                        // 将LocalDateTime转换为Instant
                        Instant instant = response.getStartDateTimeWithZone().toInstant();
                        // 获取毫秒时间戳
                        long eventTimeInMillis = instant.toEpochMilli();
                        // 設置鬧鐘，觸發時間為事件的時間戳
                        Alarm newAlarm = new Alarm(requestCode, eventTimeInMillis, eventId);
                        dbHelper.addAlarm(newAlarm);
                    }


                }else {
                    Log.v(TAG, "Fuck up");
                    Message reply = new Message("DiaLogFlow", response.getFulfillmentText(), getCurrentTime(), 1);
                    messageList.add(reply);
//                    Log.v(TAG, "message list: " + messageList);
                    chatMessageAdapter.notifyItemInserted(chatMessageAdapter.getItemCount()-1);
                    chatMessageAdapter.setDataCallback(this);
                }

                break;


            case "Deleting_Event_Intent":
                ChatEventUtils.setIsEventClick(false);
                result = getEventFromDB(response);
                Log.v(TAG, "event size: " + result.size());
                if (result.size() == 1){
                    event = result.get(0);
                    dbHelper.deleteEventById(event.getId());
                    messageList.add(new Message("DiaLogFlow", response.getFulfillmentText(), getCurrentTime(), 1));
                    Log.v(TAG, response.getFulfillmentText());
                    chatMessageAdapter.notifyItemInserted(chatMessageAdapter.getItemCount()-1);
                }else if (result.size() > 0){
                    Message reply = new Message("DiaLogFlow", "Which event you want to delete?", getCurrentTime(), 2);
                    reply.setEventArrayList(result);
                    messageList.add(reply);
//                    Log.v(TAG, "message list: " + messageList);
                    chatMessageAdapter.notifyItemInserted(chatMessageAdapter.getItemCount()-1);
                    chatMessageAdapter.setDataCallback(this);
                }else{
                    Message reply = new Message("DiaLogFlow", "There have not result that fulfill your requirement", getCurrentTime(), 1);
                    messageList.add(reply);
//                    Log.v(TAG, "message list: " + messageList);
                    chatMessageAdapter.notifyItemInserted(chatMessageAdapter.getItemCount()-1);
                    chatMessageAdapter.setDataCallback(this);
                }

                break;


            case "Editing_Event_Intent":
                ChatEventUtils.setIsEventClick(false);
                Log.v(TAG, "Editing Event");

                result = getEventFromDB(response);
                Event preEvent;
                Event UpdateEvent;

//                Log.v(TAG, "event size: " + result.size());
                if (result.size() == 1){
                    preEvent = result.get(0);
                    UpdateEvent = preEvent;
                    long eventId = result.get(0).getId();
                    if (response.getUpdateTitle() != null && response.getUpdateTitle() != ""){
                        UpdateEvent.setTitle(response.getUpdateTitle());
                    }
                    if (response.getUpdateStartDateTime() != null){
                        UpdateEvent.setStartTime(response.getUpdateStartDateTime());


                        // 将LocalDateTime转换为Instant
                        Instant instant = response.getUpdateStartDateTimeWithZone().toInstant();
                        // 获取毫秒时间戳
                        long eventTimeInMillis = instant.toEpochMilli();



                        // 創建PendingIntent對象
                        int requestCode = (int) (eventId ^ (eventId >>> 32)); // 使用哈希值生成唯一的requestCode



                        // 設置鬧鐘，觸發時間為事件的時間戳
                        dbHelper.EditAlarm(result.get(0).getId(), new Alarm(requestCode, eventTimeInMillis, eventId));



                    }
                    if ( response.getUpdateEndDateTime() != null){
                        UpdateEvent.setEndTime(response.getUpdateEndDateTime());
                    }
                    if (response.getUpdateDescription() != null && response.getUpdateDescription() != ""){
                        UpdateEvent.setDescription(response.getUpdateDescription());
                    }
                    if (response.getUpdateCategory() != null && response.getUpdateCategory() != ""){
                        UpdateEvent.setCategory(response.getUpdateCategory());
                    }
                    dbHelper.EditEvent(eventId, UpdateEvent);




                    messageList.add(new Message("DiaLogFlow", response.getFulfillmentText(), getCurrentTime(), 1));
                    Log.v(TAG, response.getFulfillmentText());
                    chatMessageAdapter.notifyItemInserted(chatMessageAdapter.getItemCount()-1);

                }else if (result.size() > 1){
                    Message reply = new Message("DiaLogFlow", "Which event you want to edit?", getCurrentTime(), 2);
                    reply.setEventArrayList(result);
                    messageList.add(reply);
//                    Log.v(TAG, "message list: " + messageList);
                    chatMessageAdapter.notifyItemInserted(chatMessageAdapter.getItemCount()-1);
                    chatMessageAdapter.setDataCallback(this);
                }else{
                    Message reply = new Message("DiaLogFlow", "There have not result that fulfill your requirement", getCurrentTime(), 1);
                    messageList.add(reply);
    //                    Log.v(TAG, "message list: " + messageList);
                    chatMessageAdapter.notifyItemInserted(chatMessageAdapter.getItemCount()-1);
                    chatMessageAdapter.setDataCallback(this);
                }
                break;


            case "Searching_event_intent":
                List<String> category = Arrays.asList("work", "sport", "study", "diet", "entertainment", "other");
                if (category.contains(response.getTitle())){
                    response newResponse = response;
                    newResponse.setCategory(response.getTitle());
                    newResponse.setTitle(null);
                    result = getEventFromDB(newResponse);
                    if (result == null || result.size() == 0){
                        result = getEventFromDB(response);
                    }
                }else {
                    result = getEventFromDB(response);
                }
                Message reply = null;
                Log.v(TAG, "result: " + result);
                if (result == null || result.size() == 0){
                    reply = new Message("DiaLogFlow", "There is no event that fulfill your requirement", getCurrentTime(), 1);
                }else {
                    reply = new Message("DiaLogFlow", "Here is/are the result(s):", getCurrentTime(), 2);
                    reply.setEventArrayList(result);
                }
                messageList.add(reply);
//                Log.v(TAG, "message list: " + messageList);
                chatMessageAdapter.notifyItemInserted(chatMessageAdapter.getItemCount()-1);
                break;





            case "Default Fallback Intent":
                Log.v(TAG, "Default Fallback");
                messageList.add(new Message("DiaLogFlow", response.getFulfillmentText(), getCurrentTime(), 1));
                Log.v(TAG, response.getFulfillmentText());
                chatMessageAdapter.notifyItemInserted(chatMessageAdapter.getItemCount()-1);
                break;

            case "Default Welcome Intent":
                Log.v(TAG, "Default Welcome");
                messageList.add(new Message("DiaLogFlow", response.getFulfillmentText(), getCurrentTime(), 1));
                Log.v(TAG, response.getFulfillmentText());
                chatMessageAdapter.notifyItemInserted(chatMessageAdapter.getItemCount()-1);
                break;
            default:
                Log.v(TAG, response.getAction());
        }

        recyclerView.smoothScrollToPosition(chatMessageAdapter.getItemCount()-1);
    }
    @Override
    public void onDataReceived(long id, int Action) {
        switch (Action){
            case 0:
                newEvent = dbHelper.getEventById(id);
                if (Response.getUpdateTitle() != null && Response.getUpdateTitle() != ""){
                    newEvent.setTitle(Response.getUpdateTitle());
                }
                if (Response.getUpdateStartDateTime() != null){
                    newEvent.setStartTime(Response.getUpdateStartDateTime());


                    // 創建PendingIntent對象
                    int requestCode = (int) (id ^ (id >>> 32)); // 使用哈希值生成唯一的requestCode


                    // 将LocalDateTime转换为Instant
                    Instant instant = Response.getUpdateStartDateTimeWithZone().toInstant();
                    // 获取毫秒时间戳
                    long eventTimeInMillis = instant.toEpochMilli();

                    dbHelper.EditAlarm(id, new Alarm(requestCode, eventTimeInMillis, id));
                }
                if ( Response.getUpdateEndDateTime() != null){
                    newEvent.setEndTime(Response.getUpdateEndDateTime());
                }
                if (Response.getUpdateDescription() != null && Response.getUpdateDescription() != ""){
                    newEvent.setDescription(Response.getUpdateDescription());
                }
                Log.v(TAG, "update Title: " + Response.getUpdateTitle());
                newEvent.show(TAG);
                dbHelper.EditEvent(id, newEvent);


                break;
            case 1:
                dbHelper.deleteEventById(id);
                break;
            case 2:

                break;
        }
    }

    @Override
    public void onCategoryReceived(String category) {
        Event event = dbHelper.getEventById(AddedEventId);
        event.setCategory(category);
        dbHelper.EditEvent(AddedEventId, event);
//        Log.v(TAG, "Category Received");
//        Log.v(TAG, dbHelper.getEventById(AddedEventId).getTitle());
        Event event1 = dbHelper.getEventById(AddedEventId);
        messageList.add(new Message("DiaLogFlow", "The event  \"" + event1.getTitle() + "\" has been added on " + event1.getStartTime().toLocalDate() + " at " + event1.getStartTime().toLocalTime() + " as " + category + " category." , getCurrentTime(), 1));
        chatMessageAdapter.notifyItemInserted(chatMessageAdapter.getItemCount()-1);
        chatMessageAdapter.notifyItemInserted(chatMessageAdapter.getItemCount()-1);
        chatMessageAdapter.notifyItemInserted(chatMessageAdapter.getItemCount()-1);
    }


    public ArrayList<Event> getEventFromDB(response response){


        Boolean Title = false;
        Boolean startTime = false;
        Boolean endTime =  false;
        Boolean category = false;
        ArrayList<Event> result = null;
//        Log.v(TAG, response.getTitle());

        Title = response.getTitle() != null && response.getTitle() != "";
        startTime = response.getStartDateTime() != null;
        endTime =  response.getEndDateTime() != null;
        category = response.getCategory() != null && response.getCategory() != "";

        Log.v(TAG, "title test: " + Title + " title: " + response.getTitle() + "\nstartTime test: " + startTime + " start time: " + response.getStartDateTime() + "\nendTime test: " + endTime + " end time: " + response.getEndDateTime() +  "\ncategory test: " + category + " category: " + response.getCategory());
        if (Title && startTime && endTime && !category){



            LocalDateTime startDateTime;
            LocalDateTime endDateTime;
            if(response.getStartDateTime() ==
                    response.getStartDateTime().withHour(12).withMinute(0).withNano(0).withSecond(0)){
                startDateTime = response.getStartDateTime().withHour(0).withMinute(0).withNano(0).withSecond(0);
                endDateTime = response.getStartDateTime().withHour(0).withMinute(0).withNano(0).withSecond(0).plusDays(1);
                Log.v(TAG, "Time per: " + startDateTime + " - " + endDateTime);
            }else {
                startDateTime = response.getStartDateTime();
                endDateTime = null;
            }



            result = dbHelper.getEventByTitleAndDateTime(response.getTitle(), startDateTime, endDateTime);
            Log.v(TAG, "title + start time + end time");
        } else if (Title && startTime && !category) {


            LocalDateTime startDateTime;
            LocalDateTime endDateTime;
            if(response.getStartDateTime() ==
                    response.getStartDateTime().withHour(12).withMinute(0).withNano(0).withSecond(0)){
                startDateTime = response.getStartDateTime().withHour(0).withMinute(0).withNano(0).withSecond(0);
                endDateTime = response.getStartDateTime().withHour(0).withMinute(0).withNano(0).withSecond(0).plusDays(1);
                Log.v(TAG, "Time per: " + startDateTime + " - " + endDateTime);
            }else {
                startDateTime = response.getStartDateTime();
                endDateTime = null;
            }

            result = dbHelper.getEventByTitleAndDateTime(response.getTitle(), startDateTime, endDateTime);
            Log.v(TAG, "title + start time");
        }else if(startTime && endTime && !category){


            LocalDateTime startDateTime;
            LocalDateTime endDateTime;
            if(response.getStartDateTime() ==
                    response.getStartDateTime().withHour(12).withMinute(0).withNano(0).withSecond(0)){
                startDateTime = response.getStartDateTime().withHour(0).withMinute(0).withNano(0).withSecond(0);
                endDateTime = response.getStartDateTime().withHour(0).withMinute(0).withNano(0).withSecond(0).plusDays(1);
                Log.v(TAG, "Time per: " + startDateTime + " - " + endDateTime);
            }else {
                startDateTime = response.getStartDateTime();
                endDateTime = null;
            }

            result = dbHelper.getEventsByDateTime(startDateTime, endDateTime);
            Log.v(TAG, ""+category + ": " + response.getCategory());
            Log.v(TAG, "start time + end time");
        } else if (Title && !category) {
            result = dbHelper.getEventByTitle(response.getTitle());
            Log.v(TAG, "title");
        } else if (startTime && !category) {


            LocalDateTime startDateTime;
            LocalDateTime endDateTime;
            if(response.getStartDateTime() ==
                    response.getStartDateTime().withHour(12).withMinute(0).withNano(0).withSecond(0)){
                startDateTime = response.getStartDateTime().withHour(0).withMinute(0).withNano(0).withSecond(0);
                endDateTime = response.getStartDateTime().withHour(0).withMinute(0).withNano(0).withSecond(0).plusDays(1);
                Log.v(TAG, "Time per: " + startDateTime + " - " + endDateTime);
            }else {
                startDateTime = response.getStartDateTime();
                endDateTime = null;
            }


            result = dbHelper.getEventsByDateTime(startDateTime, endDateTime);
            Log.v(TAG, "start time");
        } else if (Title && startTime && endTime && category){


            LocalDateTime startDateTime;
            LocalDateTime endDateTime;
            if(response.getStartDateTime() ==
                    response.getStartDateTime().withHour(12).withMinute(0).withNano(0).withSecond(0)){
                startDateTime = response.getStartDateTime().withHour(0).withMinute(0).withNano(0).withSecond(0);
                endDateTime = response.getStartDateTime().withHour(0).withMinute(0).withNano(0).withSecond(0).plusDays(1);
                Log.v(TAG, "Time per: " + startDateTime + " - " + endDateTime);
            }else {
                startDateTime = response.getStartDateTime();
                endDateTime = null;
            }


            result = dbHelper.getEventByTitleAndDateTimeAndCalegory(response.getTitle(), startDateTime, endDateTime, response.getCategory());
            Log.v(TAG, "title + start time + end time + category");
        } else if (Title && startTime && category) {



            LocalDateTime startDateTime;
            LocalDateTime endDateTime;
            if(response.getStartDateTime() ==
                    response.getStartDateTime().withHour(12).withMinute(0).withNano(0).withSecond(0)){
                startDateTime = response.getStartDateTime().withHour(0).withMinute(0).withNano(0).withSecond(0);
                endDateTime = response.getStartDateTime().withHour(0).withMinute(0).withNano(0).withSecond(0).plusDays(1);
                Log.v(TAG, "Time per: " + startDateTime + " - " + endDateTime);
            }else {
                startDateTime = response.getStartDateTime();
                endDateTime = null;
            }

            result = dbHelper.getEventByTitleAndDateTimeAndCalegory(response.getTitle(), startDateTime, endDateTime, response.getCategory());
            Log.v(TAG, "title + start time + category");
        }else if(startTime && endTime && category){


            LocalDateTime startDateTime;
            LocalDateTime endDateTime;
            if(response.getStartDateTime() ==
                    response.getStartDateTime().withHour(12).withMinute(0).withNano(0).withSecond(0)){
                startDateTime = response.getStartDateTime().withHour(0).withMinute(0).withNano(0).withSecond(0);
                endDateTime = response.getStartDateTime().withHour(0).withMinute(0).withNano(0).withSecond(0).plusDays(1);
                Log.v(TAG, "Time per: " + startDateTime + " - " + endDateTime);
            }else {
                startDateTime = response.getStartDateTime();
                endDateTime = null;
            }

            result = dbHelper.getEventsByDateTimeAndCalegory(startDateTime, endDateTime, response.getCategory());
            Log.v(TAG, "start time + end time + category");
        } else if (Title && category) {
            result = dbHelper.getEventByTitleAndCalegory(response.getTitle(), response.getCategory());
            Log.v(TAG, "title + category");
        } else if (startTime && category) {


            LocalDateTime startDateTime;
            LocalDateTime endDateTime;
            if(response.getStartDateTime() ==
                    response.getStartDateTime().withHour(12).withMinute(0).withNano(0).withSecond(0)){
                startDateTime = response.getStartDateTime().withHour(0).withMinute(0).withNano(0).withSecond(0);
                endDateTime = response.getStartDateTime().withHour(0).withMinute(0).withNano(0).withSecond(0).plusDays(1);
                Log.v(TAG, "Time per: " + startDateTime + " - " + endDateTime);
            }else {
                startDateTime = response.getStartDateTime();
                endDateTime = null;
            }

            result = dbHelper.getEventsByDateTimeAndCalegory(startDateTime, endDateTime, response.getCategory());
            Log.v(TAG, "start time + category");
        } else if (category){
            result = dbHelper.getEventByCalegory(response.getCategory());
            Log.v(TAG, "category");
        }



        return result;
    }



}
