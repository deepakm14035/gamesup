package com.iiitd.purusharth.projectx;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

//Fragment for various user related button

public class PastActivities extends Fragment {

    LinearLayout myEvents;
    View myView;

    List<EventActivity> list;

    HashMap<String, String> named_address;
    SwipeRefreshLayout swipeRefreshLayout;

    String token, userID;

    public PastActivities() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PastActivities newInstance() {
        PastActivities fragment = new PastActivities();
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
        View view = inflater.inflate(R.layout.fragment_past_activities, container, false);
        myView = view;
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        TextView tv = (TextView) view.findViewById(R.id.textView1);
        tv.setText("Past activities");

        userID = ((UserActivities) getActivity()).userID;
        token = ((UserActivities) getActivity()).token;


        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("asd", "onRefresh called from SwipeRefreshLayout");
                        getPastActivities();
                    }
                }
        );

        this.named_address = new HashMap<String, String>();
        //"28.547126,77.273158","28.609027,77.035058","28.749967,77.117674"
        named_address.put("IIIT Delhi", "28.547126,77.273158");
        named_address.put("NSIT", "28.609027,77.035058");
        named_address.put("DTU", "28.749967,77.117674");
        myEvents = (LinearLayout) view.findViewById(R.id.eventLayout);
        Log.e("ERROR", "onCreateView called");


        //Dynamically add views-----------------------------------------------------
        //setUpEventList(Hot1.dBase.getEvents());
        getPastActivities();
        //--------------------------------------------------------------------------

        return view;
    }

    void getPastActivities() {
        RequestQueue queue;
        queue = Volley.newRequestQueue(getContext());
        String url = "http://192.168.55.245:3000/users" + "/activity";

        JSONArray reqObj = new JSONArray();
        JSONObject ob;
        try {
            ob = new JSONObject();
            ob.put("userid", userID);
            ob.put("token", token);
            reqObj.put(ob);
            Log.d("asd", reqObj.toString());
            //Toast.makeText(getContext().getApplicationContext(), reqObj.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception error) {
            Toast.makeText(getContext().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
        }

        Log.d("asd", "sending message");

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, reqObj, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                getMyActivities(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        queue.add(jsonArrayRequest);
    }

    void getMyActivities(JSONArray response) {
        List<EventActivity> eventList;
        try {
            eventList = new ArrayList<>();
            Log.d("asd", eventList.toString() + "-length");
            for (int i = 0; i < response.length(); i++) {
                ExtendedEventActivity event = new ExtendedEventActivity();
                JSONObject jsonobject = response.getJSONObject(i);
                Log.d("asd", jsonobject.toString());
                event.setName(jsonobject.getString("ac_name"));
                event.setActivityName(jsonobject.getString("name"));
                event.setActivityID(jsonobject.getString("_id"));
                event.setUserID(jsonobject.getString("userid"));
                event.setPostedDate(jsonobject.getString("post_time"));


                event.setVenue(jsonobject.getString("venue"));
                event.setDesc(jsonobject.getString("desc"));
                event.setVenue_name(jsonobject.getString("venue_name"));
                try {
                    event.setNum_joined(jsonobject.getInt("numberJoined"));
                } catch (Exception e) {
                    event.setNum_joined(0);
                }

                SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy, HH:mm a");
                Date date = new Date();
                try {
                    date = format.parse(jsonobject.getString("post_time"));
                } catch (Exception e) {
                    Log.d("asd", "format incorrect");
                }
                Calendar current = Calendar.getInstance();
                current.add(Calendar.HOUR_OF_DAY, -1);
                Date curr = current.getTime();
                Log.d("asd", curr.toString() + ",-" + date.toString());
                if (date.compareTo(curr) < 0) {
                    Log.d("asd", "added");
                    eventList.add(event);

                }


            }
            setUpEventList(eventList);
        } catch (org.json.JSONException e) {
            Toast.makeText(getContext().getApplicationContext(), "JSON Exception raised!!", Toast.LENGTH_LONG).show();
            Log.e("ERROR", e.toString());
        }
        swipeRefreshLayout.setRefreshing(false);
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
            setUpEventList(Hot1.dBase.getEvents());
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


    public void setUpEventList(List<EventActivity> list1) {
        list = list1;
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
