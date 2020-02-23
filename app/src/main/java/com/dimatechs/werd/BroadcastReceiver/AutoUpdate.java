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

public class AutoUpdate extends BroadcastReceiver {

    private DatabaseReference RootRef= FirebaseDatabase.getInstance().getReference().child("UsersGroups");
    private DatabaseReference MessagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");
    private String  groupNum,senderUserID,fullName;


    public void onReceive(Context context, Intent intent) {

        Log.d("han","we here Auto ");

       groupNum=intent.getStringExtra("groupNum");
       senderUserID=intent.getStringExtra("senderUserID");
       fullName=intent.getStringExtra("fullName");
       AscUpdate(groupNum);

    }

    private void AscUpdate(String groupNum) {
        Log.d("han","we here Auto - in AScUpdate ");
        RootRef.orderByChild("groupNum").equalTo(groupNum).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<UsersGroups> usersGroups = new ArrayList<>();
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    UsersGroups ug = areaSnapshot.getValue(UsersGroups.class);
                    usersGroups.add(ug);
                }

                for (int i = 0; i < usersGroups.size(); i++) {
                    int oldPart =Integer.parseInt(usersGroups.get(i).getPartNum());
                    int newPart;
                    if(oldPart!=0) {
                        if (oldPart == 30)
                            newPart = 1;
                        else
                            newPart = oldPart + 1;
                    }else{
                        newPart = oldPart;
                    }
                    usersGroups.get(i).setPartNum(String.valueOf(newPart));
                }

                for (int i = 0; i < usersGroups.size(); i++) {

                    final int finalI = i;
                    RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            HashMap<String,Object> groupdataMap = new HashMap<>();
                            groupdataMap.put("id",usersGroups.get(finalI).getId());
                            groupdataMap.put("groupNum",usersGroups.get(finalI).getGroupNum());
                            groupdataMap.put("groupName",usersGroups.get(finalI).getGroupName());
                            groupdataMap.put("partNum",usersGroups.get(finalI).getPartNum());
                            groupdataMap.put("done","no");
                            groupdataMap.put("userName",usersGroups.get(finalI).getUserName());
                            groupdataMap.put("userPhone",usersGroups.get(finalI).getUserPhone());
                            groupdataMap.put("admin",usersGroups.get(finalI).getAdmin());
                            groupdataMap.put("groupNumPhone",usersGroups.get(finalI).getGroupNum()+"_"+usersGroups.get(finalI).getUserPhone());

                            RootRef.child(usersGroups.get(finalI).getId())
                                    .updateChildren(groupdataMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {

                                                Calendar calendar = Calendar.getInstance();

                                                SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                                                String CurrentDate = currentDate.format(calendar.getTime());

                                                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                                                String CurrentTime = currentTime.format(calendar.getTime());

                                                    HashMap<String, String> NotificationMap = new HashMap<>();
                                                    NotificationMap.put("groupNum", usersGroups.get(finalI).getGroupNum());
                                                    NotificationMap.put("from", senderUserID);
                                                    NotificationMap.put("body", "تم تحديث الاجزاء");
                                                    NotificationMap.put("senderName", fullName);
                                                    NotificationMap.put("time", CurrentTime);
                                                    NotificationMap.put("date", CurrentDate);

                                                    String receiverUserID = usersGroups.get(finalI).getUserPhone();

                                                    MessagesRef.child(receiverUserID).push()
                                                            .setValue(NotificationMap)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.d("han","sent notification Auto");
                                                                    }
                                                                }
                                                            });


                                            }
                                            else
                                            {
                                                Log.d("han","not successful ");
                                            }
                                        }
                                    });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
