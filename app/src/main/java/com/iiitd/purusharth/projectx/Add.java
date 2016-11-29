package com.iiitd.purusharth.projectx;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

//Fragment for various user related button

public class Add extends Fragment {

    Button addEvent;
    Button openHelp;
    Button logout;
    LinearLayout myEvents;
    View myView;

    List<EventActivity> list;

    HashMap<String, String> named_address;


    public Add() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Add newInstance() {
        Add fragment = new Add();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("ERROR", "onCreate called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        addEvent = (Button) view.findViewById(R.id.newEvent);
        openHelp = (Button) view.findViewById(R.id.help);
        logout = (Button) view.findViewById(R.id.logout);
        myView = view;
        this.named_address = new HashMap<String, String>();
        //"28.547126,77.273158","28.609027,77.035058","28.749967,77.117674"
        named_address.put("IIIT Delhi", "28.547126,77.273158");
        named_address.put("NSIT", "28.609027,77.035058");
        named_address.put("DTU", "28.749967,77.117674");
        myEvents = (LinearLayout) view.findViewById(R.id.eventLayout);
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddEvent.class);
                startActivityForResult(i, 0);
            }

        });
        openHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), Help.class);
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutSession();
            }
        });
        Log.e("ERROR", "onCreateView called");

        //Dynamically add views-----------------------------------------------------
        setUpEventList();
        //--------------------------------------------------------------------------

        return view;
    }

    void logoutSession() {
        Intent intent = new Intent(getContext(), FirstActivity.class);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", null);
        editor.commit();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            setUpEventList();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("ERROR", "onStart called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("ERROR", "onStop called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("ERROR", "onPause called");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("ERROR", "onDestroyView called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("ERROR", "onSDestroy called");
    }


    public void setUpEventList() {
        list = MainActivity.dBase.getEvents();
        myEvents = (LinearLayout) myView.findViewById(R.id.eventLayout);
        myEvents.removeAllViews();
        if (list.size() != 0) {
            for (int i = list.size() - 1; i >= 0; i--) {
                View child = getActivity().getLayoutInflater().inflate(R.layout.event_child, null);
                EventActivity e = list.get(i);
                //Set activity Id
                TextView activityId = (TextView) child.findViewById(R.id.activityId);
                activityId.setText("   #" + e.getActivityID());
                //Set activity Date
                TextView date = (TextView) child.findViewById(R.id.date);
                date.setText(e.getPostedDate());
                //Set activity name
                TextView name = (TextView) child.findViewById(R.id.name);
                name.setText(e.getActivityName());
                //Set activity venue
                TextView venue = (TextView) child.findViewById(R.id.venue);
                venue.setText(e.getVenue_name());
                //Add button
                Button btn = (Button) child.findViewById(R.id.details);
                Button btn_map = (Button) child.findViewById(R.id.map);
                final String ac_id = e.getActivityID();
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getContext(), MoreDetails.class);
                        i.putExtra("activityId", ac_id);
                        startActivity(i);
                    }
                });
                final String pos = e.getVenue();
                btn_map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String fPos[] = pos.split(",");
                        if (fPos.length == 1) {
                            String address = named_address.get(fPos[0]);
                            fPos = address.split(",");
                        }
                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(Event Venue)", Double.parseDouble(fPos[0]), Double.parseDouble(fPos[1]), Double.parseDouble(fPos[0]), Double.parseDouble(fPos[1]));
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                    }
                });
                myEvents.addView(child);
            }
        }

    }


}
