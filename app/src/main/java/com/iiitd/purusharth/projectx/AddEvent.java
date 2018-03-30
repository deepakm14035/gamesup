package com.iiitd.purusharth.projectx;

import android.Manifest;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

//Add activity to server

public class AddEvent extends AppCompatActivity implements OnMapReadyCallback {

    Button createBtn;
    GoogleMap myMap;
    EventActivity thisEvent;
    MapFragment mMapFragment = MapFragment.newInstance();
    Marker marker = null;
    Spinner s;
    Spinner s1;
    EditText desc;
    LinearLayout ll;
    ImageView img;
    ScrollView nestedScrollView;
    TextView ea1;
    EditText ea2;
    RequestQueue queue;
    String userID, token, username;
    private String[] activitiesArray;
    private String[] venueArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Intent intent = getIntent();
        userID = intent.getStringExtra("uid");
        token = intent.getStringExtra("token");
        username = intent.getStringExtra("username");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create Event");
        setSupportActionBar(toolbar);
        createBtn = (Button) findViewById(R.id.create);

        this.activitiesArray = new String[]{
                "Cricket", "Football", "Basketball", "Tennis", "Karate"
        };

        this.venueArray = new String[]{
                "IIIT Delhi", "NSIT", "DTU", "Use Maps"
        };

        s = (Spinner) findViewById(R.id.spinner);
        s1 = (Spinner) findViewById(R.id.spinner1);
        desc = (EditText) findViewById(R.id.desc);
        ll = (LinearLayout) findViewById(R.id.ll1);
        img = (ImageView) findViewById(R.id.transparent_image);
        img.setAlpha(0.0f);
        ea1 = (TextView) findViewById(R.id.ea1);
        ea2 = (EditText) findViewById(R.id.ea2);
        ea1.setVisibility(View.GONE);
        ea2.setVisibility(View.GONE);
        nestedScrollView = (ScrollView) findViewById(R.id.nsv);
        ll.setVisibility(View.GONE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, activitiesArray);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.spinner_layout, venueArray);

        adapter.setDropDownViewResource(R.layout.dropdown_spinner);
        adapter1.setDropDownViewResource(R.layout.dropdown_spinner);

        s.setAdapter(adapter);
        s1.setAdapter(adapter1);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createActivity();
            }
        });

        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == venueArray.length - 1) {
                    Log.e("ERROR", "MAPS");
                    ll.setVisibility(View.VISIBLE);
                    ea1.setVisibility(View.VISIBLE);
                    ea2.setVisibility(View.VISIBLE);
                    addMapToFragment();

                } else {
                    ll.setVisibility(View.GONE);
                    ea1.setVisibility(View.GONE);
                    ea2.setVisibility(View.GONE);
                    Log.e("ERROR", "NO MAPS");
                    removeMap();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        img.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        nestedScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        nestedScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        nestedScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container1, mMapFragment);
        fragmentTransaction.hide(mMapFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);
    }


    public void addMapToFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.show(mMapFragment);
        fragmentTransaction.commit();
    }

    public void removeMap() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.hide(mMapFragment);
        fragmentTransaction.commit();
    }

    public void createActivity() {

        thisEvent = new EventActivity();
        thisEvent.setActivityName(activitiesArray[s.getSelectedItemPosition()]);
        if (desc.getText().toString() == "") {
            Toast.makeText(getBaseContext(), "Enter event details!!", Toast.LENGTH_LONG).show();
            return;
        }
        thisEvent.setDesc(desc.getText().toString());


        if (s1.getSelectedItemPosition() == venueArray.length - 1) {
            if (marker == null) {
                showSnackBar("Tap, to add Marker on the Map");
                return;
            } else {
                LatLng pos = marker.getPosition();
                thisEvent.setVenue("" + pos.latitude + "," + pos.longitude);
                thisEvent.setVenue_name(ea2.getText().toString());
            }
        } else {
            String venueName1 = venueArray[s1.getSelectedItemPosition()];
            thisEvent.setVenue_name(venueName1);
            thisEvent.setVenue(VenueLocation.getVenueLocation(venueName1.toLowerCase()));
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, HH:mm a");
        String strDate = sdf.format(c.getTime());
        thisEvent.setPostedDate(strDate);


        postToServer(thisEvent);
        //AddEventToDatabase();
    }

    void postToServer(EventActivity e) {
        queue = Volley.newRequestQueue(this);

        JSONObject reqObj = new JSONObject();

        try {
            reqObj.put("ac_name", username);
            reqObj.put("name", e.getActivityName());
            reqObj.put("userid", userID);
            reqObj.put("venue", e.getVenue());
            reqObj.put("venue_name", e.getVenue_name());
            reqObj.put("post_time", e.getPostedDate());
            reqObj.put("desc", e.getDesc());
            reqObj.put("token", token);
            Log.d("asd", reqObj.toString());
            Toast.makeText(getBaseContext(), reqObj.toString(), Toast.LENGTH_LONG).show();
            makeJsonArrayReq(reqObj, e.getUserID());
        } catch (Exception error) {
            Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
        }
    }


    private void makeJsonArrayReq(JSONObject req, String user) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        String url = "http://192.168.55.245:3000/users" + "/addactivity";
        progressDialog.setMessage("Pushing to Server...");
        progressDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, req,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        processResponse(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("ERROR", "Error: " + error.getMessage());
                showPostError(error.toString());
            }
        });
        queue.add(jsonObjReq);
    }

    void processResponse(JSONObject _id) {
        try {
            thisEvent.setActivityID(_id.getString("activityid"));
            AddEventToDatabase();
        } catch (Exception error) {
            Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
        }
    }

    void showPostError(String error) {
        showSnackBar(error);
    }

    void AddEventToDatabase() {
        Hot1.dBase.addEntry(thisEvent);
        Intent returnIntent = getIntent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.myMap = map;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            addListenerToMap();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                myMap.setMyLocationEnabled(true);
                onMapReady(myMap);
            } else {
                showSnackBar("Permissions are required for functioning");
                onMapReady(myMap);
            }
        }
    }

    public void showSnackBar(String mssg) {
        Snackbar.make(findViewById(android.R.id.content), mssg, Snackbar.LENGTH_LONG).show();
    }

    public void addListenerToMap() {
        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (marker != null) {
                    marker.remove();
                }
                marker = myMap.addMarker(new MarkerOptions()
                        .position(point)
                        .title("Venue"));
            }
        });
    }
}