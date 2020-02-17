package com.dimatechs.werd.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dimatechs.werd.Model.UsersGroups;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference().child("UsersGroups");
    private DatabaseReference MessagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");
    private String groupNum, senderUserID, fullName, body, receiver,receiverUserID;

    public void onReceive(Context context, Intent intent) {
        Log.d("h", "we are in receiver");


        groupNum = intent.getStringExtra("groupNum");
        senderUserID = intent.getStringExtra("senderUserID");
        fullName = intent.getStringExtra("fullName");
        body = intent.getStringExtra("body");
        receiver = intent.getStringExtra("receiver");


        RootRef.orderByChild("groupNum").equalTo(groupNum).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<UsersGroups> usersGroups = new ArrayList<>();
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    UsersGroups ug = areaSnapshot.getValue(UsersGroups.class);
                    usersGroups.add(ug);
                }

                if (receiver.equals("all")) {
                    for (int i = 0; i < usersGroups.size(); i++)
                    {
                        receiverUserID = usersGroups.get(i).getUserPhone();
                        SendMessage(receiverUserID);
                    }
                }
                else if(receiver.equals("read")){
                    for (int i = 0; i < usersGroups.size(); i++)
                    {
                        if(usersGroups.get(i).getDone().equals("done")) {
                            receiverUserID = usersGroups.get(i).getUserPhone();
                            SendMessage(receiverUserID);
                        }
                    }
                }
                else {
                    for (int i = 0; i < usersGroups.size(); i++)
                    {
                        if(usersGroups.get(i).getDone().equals("no")) {
                            receiverUserID = usersGroups.get(i).getUserPhone();
                            SendMessage(receiverUserID);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SendMessage (String receiverUserID) {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        String CurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String CurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, String> NotificationMap = new HashMap<>();
        NotificationMap.put("from", senderUserID);
        NotificationMap.put("body", body);
        NotificationMap.put("senderName", fullName);
        NotificationMap.put("time", CurrentTime);
        NotificationMap.put("date", CurrentDate);

        MessagesRef.child(receiverUserID).push()
                .setValue(NotificationMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("han", "sent notification Auto");
                        }
                    }
                });

    }
}


