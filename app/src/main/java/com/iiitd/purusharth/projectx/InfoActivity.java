package com.iiitd.purusharth.projectx;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.github.florent37.diagonallayout.DiagonalLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.iiitd.purusharth.projectx.MainActivity.token;

public class InfoActivity extends AppCompatActivity {
    Button join;
    RequestQueue queue;
    String uid = "";
    String aid = "";
    ExtendedEventActivity thisEvent;

    TextView nameView;
    TextView gender;
    TextView age;
    TextView gameCount;
    TextView connect;
    TextView aboutme;
    TextView desc;
    TextView pCount;

    CircleImageView avatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DiagonalLayout dl = (DiagonalLayout) findViewById(R.id.diagonalLayout);
        KenBurnsView kv = (KenBurnsView) findViewById(R.id.kv);

        nameView = (TextView) findViewById(R.id.name);
        avatar = (CircleImageView) findViewById(R.id.avatar);
        gender = (TextView) findViewById(R.id.gender);
        age = (TextView) findViewById(R.id.age);
        gameCount = (TextView) findViewById(R.id.games);
        connect = (TextView) findViewById(R.id.connect);
        aboutme = (TextView) findViewById(R.id.aboutme);
        desc = (TextView) findViewById(R.id.desc);
        pCount = (TextView) findViewById(R.id.pCount);
        join = (Button) findViewById(R.id.join);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!join.getText().toString().equalsIgnoreCase("event joined"))
                    confirmJoinAction();
            }
        });
        join.setVisibility(View.GONE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                i.putExtra("activityid", aid);
                startActivity(i);
            }
        });
        thisEvent = (ExtendedEventActivity) getIntent().getSerializableExtra("event");
        kv.setImageResource(ActivityImages.getImageResource(thisEvent.getActivityName().toLowerCase()));
        uid = thisEvent.getUserID();
        aid = thisEvent.getActivityID();
        queue = Volley.newRequestQueue(this);
        FetchUserData();

    }

    void confirmJoinAction() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("To Join is to Commit. An event once joined cannot be Un-Joined. Are you up for it?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendJoinRequest();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void sendJoinRequest() {
        queue = Volley.newRequestQueue(this);
        makeJoinReq();
    }

    private void success() {
        join.setText("Event Joined");
        Drawable drawable = getResources().getDrawable(R.color.colorAccent);
        join.setBackground(drawable);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void makeJoinReq() {
        String url = "http://192.168.55.245:3000/users/" + uid + "/joinactivity/" + aid;
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Joining Activity...");
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("ERROR", response.toString());
                        pDialog.hide();
                        success();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("ERROR", "Error: " + error.getMessage());
                Log.e("ERROR", error.toString());
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
                pDialog.hide();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-token", token);
                return params;
            }
        };
        queue.add(jsonObjReq);
    }

    void FetchUserData() {
        String url = "http://192.168.55.245:3000/users/" + uid + "/info";
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Fetching user data...");
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("ERROR", response.toString());
                        pDialog.hide();
                        setUserData(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("ERROR", "Error: " + error.getMessage());
                Log.e("ERROR", error.toString());
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
                pDialog.hide();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-token", token);
                return params;
            }
        };
        queue.add(jsonObjReq);
    }

    public void setUserData(JSONObject obj) {
        try {
            if (obj.getString("name") != null)
                nameView.setText(obj.getString("name"));
            if (obj.getString("name").equalsIgnoreCase(MainActivity.uname)) {
                join.setVisibility(View.GONE);
            } else {
                join.setVisibility(View.VISIBLE);
            }
            if (obj.getString("gender") != null) {
                String g = obj.getString("gender").toLowerCase();
                if (g.equals("m"))
                    gender.setText("Male ");
                else {
                    gender.setText("Female ");
                }
            }
            if (obj.getString("age") != null)
                age.setText(" Age: " + obj.getString("age"));
            if (obj.getString("aboutme") != null)
                aboutme.setText(obj.getString("aboutme"));
            else {
                aboutme.setText("*This user likes to keep an air of mystery around themselves*");
            }

            if (obj.getString("photo") != null) {
                String photoString = obj.getString("photo");
                Log.e("PHOTO", photoString);
                Bitmap photo = getImage(photoString);
                if (photo == null) {
                    avatar.setImageResource(R.drawable.profile_icon_web);
                } else {
                    avatar.setImageBitmap(photo);
                }
            } else {
                avatar.setImageResource(R.drawable.profile_icon_web);
            }
            connect.setText("" + obj.getInt("popularity"));
            gameCount.setText("" + obj.getInt("noOfActivitiesPosted"));
            desc.setText(thisEvent.getDesc());
            pCount.setText("" + thisEvent.getNum_joined());
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
            Log.e("YOLO", e.toString());
        }
    }

    public Bitmap getImage(String photo) {
        try {
            byte[] encodeByte = Base64.decode(photo, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
    }
}
