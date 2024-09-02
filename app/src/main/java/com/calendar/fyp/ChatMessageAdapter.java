package com.calendar.fyp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import io.grpc.netty.shaded.io.netty.handler.codec.http2.Http2Connection;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder> {

    private List<Message> messageList;
    private Context context;
    private DataCallback dataCallback;
    private String TAG = "ChatMessageAdapter";
    private OnItemClickListener listener;
    public ChatMessageAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        int position;
        TextView senderTextView;
        TextView messageTextView;
        TextView timeTextView;
        RecyclerView eventListView;
        Button work;
        Button sport;
        Button entertainment;
        Button diet;
        Button other;
        Button study;


        ChatMessageViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            switch (viewType){
                case 2:
                    eventListView = itemView.findViewById(R.id.EventChatRecyclerView);

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(eventListView.getContext());
                    eventListView.setLayoutManager(layoutManager);
                    break;
                case 3:
                    work = itemView.findViewById(R.id.work);
                    sport = itemView.findViewById(R.id.sport);
                    entertainment = itemView.findViewById(R.id.entertainment);
                    diet = itemView.findViewById(R.id.diet);
                    other = itemView.findViewById(R.id.other);
                    study = itemView.findViewById(R.id.study);


                    work.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataCallback.onCategoryReceived("work");
                        }
                    });
                    sport.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataCallback.onCategoryReceived("sport");
                        }
                    });
                    entertainment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataCallback.onCategoryReceived("entertainment");
                        }
                    });
                    diet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataCallback.onCategoryReceived("diet");
                        }
                    });
                    other.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataCallback.onCategoryReceived("other");
                        }
                    });
                    study.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataCallback.onCategoryReceived("study");
                        }
                    });
                    break;
                default:
                    break;
            }
            senderTextView = itemView.findViewById(R.id.MessageSender);
            messageTextView = itemView.findViewById(R.id.messageText);
            timeTextView = itemView.findViewById(R.id.MessageTime);
        }
    }

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType){
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_message_cell, parent, false);
                break;
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialogflow_message_cell, parent, false);
                break;
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialogflow_message_cell_2, parent, false);
                break;
            case 3:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialogflow_message_cell_3, parent, false);
        }


        return new ChatMessageViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.position = holder.getAdapterPosition();
        final EventAdapter[] eventAdapter = new EventAdapter[1]; // 声明為一個final陣列
        if (message != null) {
            holder.senderTextView.setText(message.getSender());
            holder.messageTextView.setText(message.getMessage());
            holder.timeTextView.setText(message.getTime());
            int messageType = message.getType();

            switch (messageType) {
                case 2:


                    eventAdapter[0] = new EventAdapter(message.getEventArrayList(), new EventAdapter.OnItemListener() {
                        @Override
                        public void onItemClick(int position, String text, String eventTimeText, String eventTitleText, long id) {
                            String messageText = holder.messageTextView.getText().toString();
                            if (ChatEventUtils.isIsEventClick()){
                                return;
                            }
                            switch (messageText) {
                                case "Which event you want to edit?":
                                    messageList.add(new Message("System", "Event Updated", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:MM")), 1));
                                    dataCallback.onDataReceived(id, 0);
                                    eventAdapter[0].notifyDataSetChanged();
                                    break;
                                case "Which event you want to delete?":
                                    messageList.add(new Message("System", "Event Deleted", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:MM")), 1));
                                    dataCallback.onDataReceived(id, 1);
                                    eventAdapter[0].notifyDataSetChanged();
                                    break;
                                case "Here is result.":
                                    Log.v(TAG, "Searching event action is Done");
                                    break;
                            }
                            ChatEventUtils.setIsEventClick(true);
                        }
                    }, true);
                    holder.eventListView.setAdapter(eventAdapter[0]);
                    break;
                default:
                    // Handle other message types if needed
                    break;
            }
        }
        Log.v(TAG, message.getMessage());
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        return message.getType();
    }



    public void setDataCallback(DataCallback callback) {
        this.dataCallback = callback;
    }
    public interface DataCallback {
        void onDataReceived(long data, int Action);
        void onCategoryReceived(String category);
    }

}