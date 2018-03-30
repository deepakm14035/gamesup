package com.iiitd.purusharth.projectx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Hot extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipeRefreshLayout;
    LayoutAnimationController controller;
    ExpandedGridView gridView;
    CustomGrid adapter;
    List<ExtendedEventActivity> eventList;

    RequestQueue queue;

    public static Hot newInstance() {
        Hot fragment = new Hot();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hot, container, false);
        gridView = (ExpandedGridView) view.findViewById(R.id.gridview);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        set.addAnimation(animation);

        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(700);
        set.addAnimation(animation);
        controller = new LayoutAnimationController(set, 0.25f);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent i = new Intent(getActivity().getApplicationContext(), InfoActivity.class);
                i.putExtra("event", eventList.get(position));
                startActivity(i);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            gridView.setNestedScrollingEnabled(true);
        }
        fetchData();
        return view;
    }

    @Override
    public void onRefresh() {
        fetchData();
    }

    public void addList() {
        adapter = new CustomGrid(getContext(), eventList);
        gridView.setAdapter(adapter);
        gridView.setLayoutAnimation(controller);
    }

    private void processJsonArray(JSONArray jsonarray) {
        try {
            eventList = new ArrayList<>();
            for (int i = 0; i < jsonarray.length(); i++) {
                ExtendedEventActivity event = new ExtendedEventActivity();
                JSONObject jsonobject = jsonarray.getJSONObject(i);
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
                eventList.add(event);
            }
        } catch (org.json.JSONException e) {
            Toast.makeText(getContext(), "JSON Exception raised!!", Toast.LENGTH_LONG).show();
            Log.e("ERROR", e.toString());
        }
        addList();
    }

    void fetchData() {
        queue = Volley.newRequestQueue(getContext());
        String url = "http://192.168.55.245:3000/activities";
        JSONArray req = null;
        requestGetServer(url, req);

    }

    public void requestGetServer(String url, JSONArray reqObj) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Feed");
        //progressDialog.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, reqObj, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.hide();
                Toast.makeText(getContext(), "Retrieval Successful", Toast.LENGTH_SHORT).show();
                processJsonArray(response);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(getContext(), "Retrieval Failed", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        queue.add(jsonArrayRequest);
    }
}
