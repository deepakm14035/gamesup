package com.iiitd.purusharth.projectx;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Purusharth on 28-11-2016.
 */

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String MESSAGES_ENDPOINT = "http://192.168.55.245:3000";
    static final String username = "shit";
    EditText messageInput;
    String activityid;
    Button sendButton;
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);
        //  Toast.makeText(this, "Welcome, " + username + "!", Toast.LENGTH_LONG).show();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Message Room");
        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);
        messageInput = (EditText) findViewById(R.id.message_input);
        messageAdapter = new MessageAdapter(this, new ArrayList<Message>());
        final ListView messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        Pusher pusher = new Pusher("c1bedc1ab08f5f49f2d8");

        pusher.connect();

        Intent intent = getIntent();
        activityid = intent.getStringExtra("activityid");
        Channel channel = pusher.subscribe(activityid);

        channel.bind("new_message", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        Message message = gson.fromJson(data, Message.class);
                        messageAdapter.add(message);
                        messagesView.setSelection(messageAdapter.getCount() - 1);
                    }

                });
            }

        });
        chatRoomconfirm();
    }

    void chatRoomconfirm() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Please note that Administrators/Moderators reserve the right to change/edit/delete/move/merge any content at any time if they feel it is inappropriate, abusive, or incorrectly categorized.");
        alertDialogBuilder.setPositiveButton("Yes, I understand", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                welcomeCall();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    void welcomeCall() {
        Toast.makeText(this, "Welcome, " + username + "!", Toast.LENGTH_LONG).show();
        postMessage("*JOINED*");
    }

    @Override
    public void onClick(View v) {
        String text = messageInput.getText().toString();
        postMessage(text);
    }

    private void postMessage(String text) {
        if (text.equals("")) {
            return;
        }

        RequestParams params = new RequestParams();

        params.put("text", text);
        params.put("name", username);
        params.put("time", new Date().getTime());

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(MESSAGES_ENDPOINT + "/messages" + "/" + activityid, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageInput.setText("");
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Something went wrong :(", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
