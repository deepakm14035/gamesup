package com.iiitd.purusharth.projectx;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Purusharth on 28-11-2016.
 */


public class MessageAdapter extends BaseAdapter {
    Context messageContext;
    ArrayList<Message> messageList;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        messageList = messages;
        messageContext = context;
    }

    public void add(Message message) {
        messageList.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageViewHolder holder;
        LayoutInflater messageInflater = (LayoutInflater) messageContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message thisMessage = messageList.get(position);
        if (thisMessage.name.toLowerCase().equalsIgnoreCase("shit")) {
            convertView = messageInflater.inflate(R.layout.message_layout, parent, false);
        } else {
            convertView = messageInflater.inflate(R.layout.message_layout_2, parent, false);
        }
        holder = new MessageViewHolder();
        holder.senderView = (TextView) convertView.findViewById(R.id.message_sender);
        holder.bodyView = (TextView) convertView.findViewById(R.id.message_body);
        holder.time = (TextView) convertView.findViewById(R.id.time_body);
        convertView.setTag(holder);
        Message message = (Message) getItem(position);
        holder.bodyView.setText(message.text);
        holder.senderView.setText(message.name);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, HH:mm a");
        String strDate = sdf.format(c.getTime());
        holder.time.setText(strDate);
        return convertView;
    }

    private static class MessageViewHolder {
        public TextView senderView;
        public TextView bodyView;
        public TextView time;
    }


}
