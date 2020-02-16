package com.dimatechs.werd;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.dimatechs.werd.BroadcastReceiver.AutoUpdate;
import com.dimatechs.werd.BroadcastReceiver.MyBroadcastReceiver;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

    AlarmManager notificationAlarmManager,AutoAlarmManager;
    TimePicker timePicker;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        notificationAlarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
        AutoAlarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);

        timePicker=(TimePicker)findViewById(R.id.timePicker);
        final Calendar calendar=Calendar.getInstance();


        final Intent NotificationIntent=new Intent(AlarmActivity.this, MyBroadcastReceiver.class);
        final Intent AutoIntent=new Intent(AlarmActivity.this, AutoUpdate.class);
        AutoIntent.putExtra("groupNum","65");

        Button alarm_on=(Button)findViewById(R.id.alarm_on);
        Button alarm_off=(Button)findViewById(R.id.alarm_off);

        Button auto=(Button)findViewById(R.id.AutoBtn);
        Button disAuto=(Button)findViewById(R.id.AutoDisBtn);

        alarm_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
                calendar.set(Calendar.MINUTE,timePicker.getMinute());

                pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this,0,NotificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

                notificationAlarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);


            }
        });

        alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationAlarmManager.cancel(pendingIntent);
            }
        });


        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
                calendar.set(Calendar.MINUTE,timePicker.getMinute());

                pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this,0,AutoIntent,PendingIntent.FLAG_UPDATE_CURRENT);

                AutoAlarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);


            }
        });

        disAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoAlarmManager.cancel(pendingIntent);

            }
        });
    }


}
