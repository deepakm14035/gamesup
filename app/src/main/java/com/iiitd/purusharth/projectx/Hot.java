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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Hot extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipeRefreshLayout;
    LayoutAnimationController controller;
    ExpandedGridView gridView;
    CustomGrid adapter;
    List<ExtendedEventActivity> eventList;

    RequestQueue queue;
    String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyIkX18iOnsic3RyaWN0TW9kZSI6dHJ1ZSwic2VsZWN0ZWQiOnt9LCJnZXR0ZXJzIjp7fSwid2FzUG9wdWxhdGVkIjpmYWxzZSwiYWN0aXZlUGF0aHMiOnsicGF0aHMiOnsiYWRtaW4iOiJpbml0IiwiX192IjoiaW5pdCIsInVzZXJuYW1lIjoiaW5pdCIsImhhc2giOiJpbml0Iiwic2FsdCI6ImluaXQiLCJfaWQiOiJpbml0In0sInN0YXRlcyI6eyJpZ25vcmUiOnt9LCJkZWZhdWx0Ijp7fSwiaW5pdCI6eyJfX3YiOnRydWUsImFkbWluIjp0cnVlLCJ1c2VybmFtZSI6dHJ1ZSwiaGFzaCI6dHJ1ZSwic2FsdCI6dHJ1ZSwiX2lkIjp0cnVlfSwibW9kaWZ5Ijp7fSwicmVxdWlyZSI6e319LCJzdGF0ZU5hbWVzIjpbInJlcXVpcmUiLCJtb2RpZnkiLCJpbml0IiwiZGVmYXVsdCIsImlnbm9yZSJdfSwiZW1pdHRlciI6eyJkb21haW4iOm51bGwsIl9ldmVudHMiOnt9LCJfZXZlbnRzQ291bnQiOjAsIl9tYXhMaXN0ZW5lcnMiOjB9fSwiaXNOZXciOmZhbHNlLCJfZG9jIjp7ImFkbWluIjpmYWxzZSwiX192IjowLCJ1c2VybmFtZSI6ImRlZXBhayIsImhhc2giOiJkNjNiYTc5ZThlZDJjMDIyZTRmOGQ3ODg0ZjAxMWU5M2FkNDdlYWI2ZWE0MmRhNjUxNmU4NmIwOGFmYTlhNmY3MDNmMGVmMGNjY2M4MjcxMWJiNzA4NGU3ZDY2OWM5ZjNiZGJjMmZhNDRkMjI3ODU5MDdlNWQ5MWZhYmFjYzdjNDQwMDRiZTliZTg5YTIxNzhkNTExYjcyYzM3Y2UwYTFjYmQwYTk1NTIyYzZhOWIwYTEwMWMwYTE5OTFmMDI3ZjgzNGE0ZjM3NTRkYTRkNzdlNjQ1NjY4ZWNhNWNiZjI0ODQ4YjZiZDBlYTUzZjdhNGIwMzBkOWU0YjY4Y2U1NDg4MmVhMzJhNjI2MzAxMjNjMTlhOTQ3NzNlYmYzYTAwN2IwODIyYTM5ZTA1Y2NiYWEyZGFhZGUxZGQzMTYxZDY4NDQ0Y2FjODY3MTYxYTU4ZDU2NTA4MWFiN2MxNGFjZTY2ZDRlMjY4MzVlNTBlZTZhN2NmOWVlNDQ2MjNjZDdlMDMwN2VlZjIzNjA4MGU3ZWY0MDAwYThmYmE0MjM4ZjVkM2Q1YzBhOTY5MTRkODAzZGFkNjZlZmJkNzMyNTQzOTljNWJiMzM4ZGU5OTMyOTlmNjcyZjc2YzljZTI0MjQ1ZjA4MGJiYjBkNjBjMjNhMDQ5MTI1MDNhZDJjZGMxYTFlNjJiZjUyMjkzMjAyZDVjNmQ5MDdmOWNlYTc0ZmNkOTVlN2E0MTBhYjFlODk2NzQ0YzM5YjkzZTRkYWYzY2IzZmZlM2VmYzg1NWJkMGVkZjI3OTY0MjY4YzkwNDVhZTM4ODU4YzA0YzE4OGQ5MDI3OTk3NzMxNDM4NTA2YjgxNTVjZDZhOGNiOTU3ZWIwMDFjN2M3MDZiNmI0ZmQzOGUxMmEzOGI3NGQ0N2FkZTEyZTM3YzA2YWI3YjQ1MGU5NjJmMzc5Y2FlZDdmZWQ0ZmU3M2VkYWY3NjIyZWY3NWRkMDFmOTI3ZTEyOWNiYjE2MTk1YzEyNWI3NDAzYWFlMGQ4YzM1YTQ5YWY3MzM2OTdjYTUwOGI0ZTM0MGZmNThmOTVlNjk5NTY1MWRmZjFlMTRjODBjYjMyMjAxNjI5ZDg3NjJiMjMyMjgzNGY0N2RkMDE5NzI5YzZhMDI4YjFjNGYwZThkZmZiMTI4NDY1NmQwM2NiN2VjNWNhZTUyNDMzOTQ1Y2Q3ZjRiYzgyYTE5ZmQ3Nzc4ZjA2ODkwYWVkYWI2YTVlNjQ0NmIxZWI0ZDlmMDJkYzc2NTAzMTk0Y2Y2ZWU3Y2FjNzM5NmY3MDRmYjFmNWZkN2U1M2NiMDBiZTg5Y2Q5NDBhMzkzYjAxNGEzNDU3NzdhZjhjZjIwMzI0NDMzNWQ5Iiwic2FsdCI6IjE3NTlmNWE5Zjg3YTAwNzMyYjIxYzA2MzZlNDA4Y2IzNGRmZDM0MjhlMTg0MTJlOTMwZGY3ZThiYTZkOGRlMWUiLCJfaWQiOiI1ODFjZGE3ZWY1ZDZmYTRjNWM2MmU3YjYifSwiX3ByZXMiOnsiJF9fb3JpZ2luYWxfc2F2ZSI6W251bGwsbnVsbCxudWxsXSwiJF9fb3JpZ2luYWxfcmVtb3ZlIjpbbnVsbF19LCJfcG9zdHMiOnsiJF9fb3JpZ2luYWxfc2F2ZSI6W10sIiRfX29yaWdpbmFsX3JlbW92ZSI6W119LCJpYXQiOjE0ODAyMjk5MDAsImV4cCI6MTQ4MDIzMzUwMH0.d3kQseauqv81k91UDr-dFoOK7rECU0Io8ErOgE9MhK0";


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
            MainActivity.size = jsonarray.length();
            Log.e("NUM", "" + MainActivity.size);
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
        String url = "http://192.168.55.245:3000/activities/" + MainActivity.uid;
        JSONArray req = null;
        requestGetServer(url, req);

    }

    public void requestGetServer(String url, JSONArray reqObj) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Array...");
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
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-token", token);
                return params;
            }
        };
        queue.add(jsonArrayRequest);
    }
}
