package com.dimatechs.werd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dimatechs.werd.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class AddGroupAdminActivity extends AppCompatActivity {

    private Button AddGroupBtn;
    private EditText EdgroupName;
    private ProgressDialog loadingBar;
    DatabaseReference groupListRef;

    private String saveCurrentDate,saveCurrentTime;
    private String RandomKey;
    String groupNum="0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_admin);

        AddGroupBtn=(Button)findViewById(R.id.add_group_admin_btn);
        EdgroupName=(EditText)findViewById(R.id.add_groupName_admin);

        loadingBar=new ProgressDialog(this);

        AddGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddGroup();
            }
        });

        // Get Group Auto Num
        groupListRef=FirebaseDatabase.getInstance().getReference().child("GroupAutoNum");
        groupListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    //new group num
                    groupNum = (dataSnapshot.getValue().toString());
                    int x= Integer.parseInt(groupNum)+1;
                    groupListRef.setValue(x);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void AddGroup()
    {
        String groupName =EdgroupName.getText().toString();

        if(TextUtils.isEmpty(groupName))
        {
            Toast.makeText(this, "ادخل اسم الجموعه . . .", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("انشاء مجموعه");
            loadingBar.setMessage("انتظر");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateGroup(groupNum,groupName);

        }
    }

    private void ValidateGroup(final String groupNum,final String groupName)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        Query query = RootRef.orderByChild("groupName").equalTo(groupName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    HashMap<String,Object> userdataMap = new HashMap<>();
                    userdataMap.put("groupNum",groupNum);
                    userdataMap.put("groupName",groupName);
                    userdataMap.put("locked","no");

                    RootRef.child(groupNum).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        AddGroupAdmin(groupNum);
                                        Toast.makeText(AddGroupAdminActivity.this, "تمت اضافة المجموعه بنجاح", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent=new Intent(AddGroupAdminActivity.this,UsersGroupActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(AddGroupAdminActivity.this, "שגיאת רשת", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(AddGroupAdminActivity.this, "اسم المجموعه موجود. اختر اسم اخر اذا سمحت !!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void AddGroupAdmin(final String group)
    {
        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        RandomKey=saveCurrentDate+saveCurrentTime;

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child("Groups").child(group).exists())
                {
                    // dataSnapshot.child("Groups").child(group).child("groupName").getValue(String.class);
                    HashMap<String,Object> groupdataMap = new HashMap<>();
                    groupdataMap.put("id",RandomKey);
                    groupdataMap.put("groupNum",group);
                    groupdataMap.put("groupName",dataSnapshot.child("Groups").child(group).child("groupName").getValue(String.class));
                    groupdataMap.put("partNum","1");
                    groupdataMap.put("done","no");
                    groupdataMap.put("userName",dataSnapshot.child("Users").child(Prevalent.currentOnlineUser.getPhone()).child("name").getValue(String.class));
                    groupdataMap.put("userPhone",Prevalent.currentOnlineUser.getPhone());
                    groupdataMap.put("admin","yes");
                    groupdataMap.put("groupNumPhone",group+"_"+Prevalent.currentOnlineUser.getPhone());

                    RootRef.child("UsersGroups").child(RandomKey)
                            .updateChildren(groupdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        loadingBar.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(AddGroupAdminActivity.this, "שגיאת רשת", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(AddGroupAdminActivity.this, "المجموعه غير موجودة", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

    }
}
