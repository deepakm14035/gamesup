package com.iiitd.purusharth.projectx;

/**
 * Created by Purusharth on 18-10-2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomGrid extends BaseAdapter {
    private final List<ExtendedEventActivity> eventList;
    private Context mContext;


    public CustomGrid(Context c, List<ExtendedEventActivity> eventList) {
        mContext = c;
        this.eventList = eventList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_single, null);
        } else {
            grid = convertView;
        }
        TextView textView = (TextView) grid.findViewById(R.id.grid_text);
        TextView date = (TextView) grid.findViewById(R.id.grid_date);
        ImageView imageView = (ImageView) grid.findViewById(R.id.picture);
        textView.setText(eventList.get(position).getName());
        date.setText(eventList.get(position).getPostedDate());
        imageView.setImageResource(ActivityImages.getImageResource(eventList.get(position).getActivityName().toLowerCase()));
        return grid;
    }
}