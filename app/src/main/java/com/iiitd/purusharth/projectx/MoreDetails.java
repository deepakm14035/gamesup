package com.iiitd.purusharth.projectx;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MoreDetails extends AppCompatActivity {


    TextView ac_id;
    TextView venue;
    TextView name;
    TextView desc;
    TextView date;
    TextView par;

    RequestQueue queue;
    String pos = "";
    String _id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_details);
        ac_id = (TextView) findViewById(R.id.activityId);
        name = (TextView) findViewById(R.id.name);
        venue = (TextView) findViewById(R.id.venue);
        date = (TextView) findViewById(R.id.date);
        desc = (TextView) findViewById(R.id.desc);
        par = (TextView) findViewById(R.id.par);

        queue = Volley.newRequestQueue(this);
        _id = getIntent().getStringExtra("activityId");
        fetchActivityDetails();
    }

    void fetchActivityDetails() {
        String url = "http://192.168.55.245:3000/users/activity/" + _id;
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading info...");
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("ERROR", response.toString());
                        processResponse(response);
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("ERROR", "Error: " + error.getMessage());
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
                // hide the progress dialog
                pDialog.hide();
            }
        });
        queue.add(jsonObjReq);
    }

    void processResponse(JSONObject response) {
        try {
            ac_id.setText(_id);
            date.setText(response.getString("post_time"));
            venue.setText(response.getString("venue_name"));
            desc.setText(response.getString("desc"));
            name.setText(response.getString("name"));
            pos = response.getString("venue");
            par.setText(response.getInt("numberJoined"));
        } catch (Exception e) {
            //Toast.makeText(getBaseContext(),e.toString(),Toast.LENGTH_LONG).show();
        }
    }


}