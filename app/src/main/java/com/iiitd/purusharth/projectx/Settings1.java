package com.iiitd.purusharth.projectx;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;


public class Settings1 extends AppCompatActivity {

    AlarmManager alarm_mgr;
    PendingIntent pending_intent;
    Switch myswitch;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings_main);
        myswitch = (Switch) findViewById(R.id.myswitch);
        myswitch.setChecked(false);
        myswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    setUpAlarm(getApplication());
                    Log.e("ERROR", "CALLED");
                } else {
                    StopService();
                    Log.e("ERROR", "NOO");
                }

            }
        });

    }


    public void setUpAlarm(Application context) {
        alarm_mgr = null;
        pending_intent = null;
        Intent intent = new Intent(context, MyIntentService.class);
        pending_intent = PendingIntent.getService(context, 0, intent, 0);
        alarm_mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm_mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1000 * 30, pending_intent);
    }

    void StopService() {
        alarm_mgr.cancel(pending_intent);
        alarm_mgr = null;
        pending_intent = null;
    }
}
