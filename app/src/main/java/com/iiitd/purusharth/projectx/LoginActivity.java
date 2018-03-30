package com.iiitd.purusharth.projectx;

/**
 * Created by Purusharth on 29-11-2016.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import java.io.ByteArrayOutputStream;


public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener {

    RequestQueue queue;
    String userID, token;
    private AppCompatButton btn_login;
    private TextView link_signup;
    private EditText input_username;
    private EditText input_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        input_username = (EditText) findViewById(R.id.input_username);
        input_password = (EditText) findViewById(R.id.input_password);
        link_signup = (TextView) findViewById(R.id.link_signup);
        btn_login = (AppCompatButton) findViewById(R.id.btn_login);

        btn_login.setOnClickListener(this);
        link_signup.setOnClickListener(this);

    }


    void skipLogin() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent i = new Intent(this, Hot1.class);
        //i.putExtra("token", thisHeader);
        Log.d("asd", userID);
        i.putExtra("uid", userID);
        i.putExtra("token", token);
        i.putExtra("username", input_username.getText().toString());


        startActivity(i);
        finish();
    }


    public boolean validate() {
        boolean valid = true;

        String username = input_username.getText().toString();
        String password = input_password.getText().toString();

        if (username.isEmpty()) {
            input_username.setError("enter a valid username");
            valid = false;
        } else {
            input_username.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            input_password.setError("more than 4 alphanumeric characters");
            valid = false;
        } else {
            input_password.setError(null);
        }

        return valid;
    }

    void checkUser() {
        postToServer();
    }

    void postToServer() {
        queue = Volley.newRequestQueue(this);

        JSONObject reqObj = new JSONObject();

        try {
            reqObj.put("username", input_username.getText().toString());
            reqObj.put("password", input_password.getText().toString());
            Log.d("asd", input_username.getText().toString());
            Toast.makeText(getBaseContext(), reqObj.toString(), Toast.LENGTH_LONG).show();
            makeJsonArrayReq(reqObj);
        } catch (Exception error) {
            Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
        }
    }

    void showPostError(String error) {
        showSnackBar(error);
    }

    public void showSnackBar(String mssg) {
        Snackbar.make(findViewById(android.R.id.content), mssg, Snackbar.LENGTH_LONG).show();
    }

    private void makeJsonArrayReq(JSONObject req) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        String url = "http://192.168.55.245:3000/users" + "/login";
        progressDialog.setMessage("Pushing to Server...");
        progressDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, req,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("asd", response.toString());
                        try {
                            userID = response.getString("userid");
                            token = response.getString("token");
                            skipLogin();
                        } catch (Exception e1) {
                        }
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


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_login:
                checkUser();
                break;

            case R.id.link_signup:
                Intent intent = new Intent(getApplicationContext(), Signup.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] arr = baos.toByteArray();
        String result = Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }

}

