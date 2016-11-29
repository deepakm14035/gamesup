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
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;


public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    static final String T = MainActivity.class.getSimpleName() + ".tag";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;
    RequestQueue queue;
    private String thisHeader = "";
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private SignInButton btnSignIn;
    private AppCompatButton btn_login;
    private TextView link_signup;
    private EditText input_username;
    private EditText input_password;
    private String[] RESULT = {"", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);

        input_username = (EditText) findViewById(R.id.input_username);
        input_password = (EditText) findViewById(R.id.input_password);
        link_signup = (TextView) findViewById(R.id.link_signup);
        btn_login = (AppCompatButton) findViewById(R.id.btn_login);

        btnSignIn.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        link_signup.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());

        Log.e("HEADER", "" + getIntent().getIntExtra("auth", -1));

        if (getIntent().getIntExtra("auth", -1) == 1) {
            queue = Volley.newRequestQueue(this);
            thisHeader = getIntent().getStringExtra("token");
            checkValidity();
        }

    }

    private void checkValidity() {
        String url = "http://192.168.55.245:3000/checktoken";
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Checking Login...");
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("ERROR", response.toString());

                        try {
                            Thread.sleep(2000);
                            if (response.getString("status").equals("ok")) {
                                skipLogin();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), "Please login", Toast.LENGTH_LONG).show();
                VolleyLog.d("ERROR", "Error: " + error.getMessage());
                // hide the progress dialog
                pDialog.hide();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-token", thisHeader);
                return params;
            }
        };
        queue.add(jsonObjReq);
    }

    void skipLogin() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("token", thisHeader);
        i.putExtra("uid", preferences.getString("uid", null));
        startActivity(i);
        finish();
    }


    private void logIntent(String[] d) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("uid", d[0]);
        intent.putExtra("token", d[1]);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", d[1]);
        editor.putString("uid", d[0]);
        editor.apply();
        startActivity(intent);
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


    private void post(String url, final String request, final RequestParams params) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        Log.d(TAG, params.toString());
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d(TAG, "entered on success");
                    if (response.getString("status") != null) {
                        Log.d(TAG, "success");
                        RESULT[0] = response.getString("userid");
                        RESULT[1] = response.getString("token");
                        progressDialog.hide();
                        logIntent(new String[]{RESULT[0], RESULT[1], null});
                    } else {
                        Log.d(TAG, "not success");
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "jsonexception");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(
                                    getApplicationContext(),
                                    "Something went wrong :(",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, final JSONObject responseString) {
                Log.d(TAG, "failure");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        boolean error = true;
                        progressDialog.hide();
                        if (responseString != null) {
                            try {
                                JSONObject e = responseString.getJSONObject("err");
                                if (e != null) {
                                    if (e.getString("name") != null && e.getString("name").equals("UserExistsError")) {
                                        error = false;
                                        post("http://192.168.55.245:3000/users/login", "login", params);

                                    }
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        }
                        if (error) {
                            Log.d(TAG, "failure " + error);
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Something went wrong +error:(",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                });
            }
        });

    }

    private void logIn() {


        if (!validate()) {
            Toast.makeText(getBaseContext(), "Login failed!(enter valid Username and Password )", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = input_username.getText().toString();
        String password = input_password.getText().toString();

        RequestParams params = new RequestParams();

        // set our JSON object
        params.put("username", username);
        params.put("password", password);

        post("http://192.168.55.245:3000/users/login", "login", params);


    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        //updateUI(false);
                    }
                });
    }


    private void handleSignInResult(GoogleSignInResult result) {

        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getEmail());

            String personName = acct.getDisplayName();
            final String[] personPhotoUrl = new String[1];
            try {
                personPhotoUrl[0] = acct.getPhotoUrl().toString();
            } catch (NullPointerException e) {
                personPhotoUrl[0] = "";
            }

            String username = acct.getEmail() + "user";
            final List<String> p = new ArrayList();
            File fl = new File(getFilesDir().getAbsoluteFile(), "image");
            fl.delete();
            SimpleTarget target = new SimpleTarget<Bitmap>() {

                public void write(String img) {
                    File file = new File(getFilesDir(), "image");

                    try {
                        FileOutputStream fout = new FileOutputStream(String.valueOf(file), false);
                        BufferedWriter br = new BufferedWriter(new OutputStreamWriter(fout));
                        br.write(img);
                        br.newLine();


                        br.close();


                    } catch (FileNotFoundException e) {
                        Log.d(TAG, "write:file not found");
                        e.printStackTrace();

                    } catch (IOException e) {
                        Log.d(TAG, "write:i/o exception");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                    // do something with the bitmap
                    // for demonstration purposes, let's just set it to an ImageView

                    p.add(BitMapToString(bitmap));
                    write(BitMapToString(bitmap));
                    Log.d(TAG, "bitmap  ");
                }


            };

            if (!personPhotoUrl[0].equals("")) {
                Glide.with(getApplicationContext())
                        .load(personPhotoUrl[0])
                        .asBitmap()
                        .thumbnail(0.5f)
                        .into(target);
                try {
                    FileInputStream f = openFileInput("image");
                    BufferedReader br = new BufferedReader(new InputStreamReader(f));
                    p.add(br.readLine());

                    br.close();

                } catch (Exception e) {
                    Log.d(TAG, "not read file");
                }

            }
            Log.e(TAG, "Name: " + personName + ", username: " + username
                    + ", Image: " + personPhotoUrl[0]);
            RequestParams params = new RequestParams();

            // set our JSON object
            params.put("username", username);
            params.put("password", username);
            params.put("name", personName);
            params.put("age", "");
            params.put("aboutme", "");
            params.put("gender", "");
            params.put("isgmail", "true");
            if (!p.isEmpty())
                personPhotoUrl[0] = p.get(0);
            else
                personPhotoUrl[0] = "";

            params.put("photo", personPhotoUrl[0]);
            signOut();

            post("http://192.168.55.245:3000/users/register", "register", params);


        }


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_sign_in:
                signIn();
                break;

            case R.id.btn_login:
                logIn();
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
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] arr = baos.toByteArray();
        String result = Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }

}

