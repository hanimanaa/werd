package com.dimatechs.werd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.dimatechs.werd.Model.Messages;

public class messageDetailsActivity extends AppCompatActivity {
    private TextView SenderName,Date,Message,GroupName;
    private String senderName,date,message,groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        SenderName=(TextView)findViewById(R.id.tv_sender_name_message_details);
        Date=(TextView)findViewById(R.id.tv_time_message_details);
        Message=(TextView)findViewById(R.id.tv_message_message_details);
        GroupName=(TextView)findViewById(R.id.tv_group_name_message_details);

        senderName=getIntent().getStringExtra("SenderName");
        date=getIntent().getStringExtra("Date");
        message=getIntent().getStringExtra("Message");
        groupName=getIntent().getStringExtra("GroupName");

        SenderName.setText(senderName);
        Date.setText(date);
        Message.setText(message);
        GroupName.setText(groupName);

    }
}
