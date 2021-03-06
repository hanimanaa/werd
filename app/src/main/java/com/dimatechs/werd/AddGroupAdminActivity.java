package com.dimatechs.werd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dimatechs.werd.Model.UsersGroups;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddGroupAdminActivity extends AppCompatActivity {

    private Button AddGroupBtn;
    private EditText EdgroupName;
    private ProgressDialog loadingBar;
    DatabaseReference groupListRef,ugRef;
    private TextView title;

    private String saveCurrentDate,saveCurrentTime;
    private String RandomKey;
    String groupNum="0",oldGroupNum,oldgroupName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_admin);

        ugRef = FirebaseDatabase.getInstance().getReference().child("UsersGroups");


        AddGroupBtn=(Button)findViewById(R.id.add_group_admin_btn);
        EdgroupName=(EditText)findViewById(R.id.add_groupName_admin);
        title=(TextView) findViewById(R.id.add_group_admin_app_name);

        oldGroupNum=getIntent().getStringExtra("groupNum");
        oldgroupName=getIntent().getStringExtra("groupName");
        EdgroupName.setText(oldgroupName);

        loadingBar=new ProgressDialog(this);

        AddGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddGroup();
            }
        });

        if(oldGroupNum!=null) {
            groupNum = oldGroupNum;
            title.setText("تحديث اسم المجموعة");

        }else {
            // Get Group Auto Num
            groupListRef = FirebaseDatabase.getInstance().getReference().child("GroupAutoNum");
            groupListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //new group num
                        groupNum = (dataSnapshot.getValue().toString());
                        int x = Integer.parseInt(groupNum) + 1;
                        groupListRef.setValue(x);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void AddGroup()
    {
        String groupName =EdgroupName.getText().toString();

        if(TextUtils.isEmpty(groupName))
        {
            EdgroupName.setError("ادخل اسم المجموعه اذا سمحت");
        }
        else
        {
            loadingBar.setTitle("");
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
                    if(oldGroupNum==null) {
                        userdataMap.put("locked", "no");
                    }
                    userdataMap.put("auto","no");

                    RootRef.child(groupNum).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        if(oldGroupNum==null){
                                            //new group
                                            AddGroupAdmin(groupNum);
                                        }else{
                                            //update group
                                            UpdateGroupName(groupNum,groupName);
                                        }

                                    }
                                    else
                                    {
                                        MakeToast("تحديث","للاسف لم يتم التحديث !!!",R.drawable.error1);
                                        loadingBar.dismiss();

                                    }
                                }
                            });

                }
                else
                {
                    MakeToast("تحديث","اسم المجموعه مسجل. اختر اسم اخر اذا سمحت !!",R.drawable.error1);
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
                                        MakeToast("اضافة","تمت اضافة المجموعه بنجاح",R.drawable.ok);
                                        loadingBar.dismiss();
                                        Intent intent=new Intent(AddGroupAdminActivity.this,UsersGroupActivity.class);
                                        finish();
                                        startActivity(intent);

                                    }
                                    else
                                    {
                                        MakeToast("تحديث","للاسف لم يتم التحديث !!!",R.drawable.error1);
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

    private void UpdateGroupName(final String groupNum,final String groupName)
    {
        ugRef.orderByChild("groupNum").equalTo(groupNum).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<UsersGroups> usersGroups = new ArrayList<>();
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    UsersGroups ug = areaSnapshot.getValue(UsersGroups.class);
                    usersGroups.add(ug);
                }

                for (int i = 0; i < usersGroups.size(); i++) {
                    usersGroups.get(i).setGroupName(groupName);
                }

                for (int i = 0; i < usersGroups.size(); i++) {

                    final int finalI = i;
                    ugRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

                            ugRef.child(usersGroups.get(finalI).getId())
                                    .updateChildren(groupdataMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                MakeToast("تحديث","تم التحديث بنجاح",R.drawable.ok);
                                                loadingBar.dismiss();
                                                Intent intent=new Intent(AddGroupAdminActivity.this,UsersGroupActivity.class);
                                                finish();
                                                startActivity(intent);

                                            }
                                            else
                                            {
                                                MakeToast("تحديث","للاسف لم يتم التحديث !!!",R.drawable.error1);
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

    private void MakeToast(String headerText, String message,int image)
    {
        // MakeToast("hi","jhvgfxfhg",R.drawable.warning1);
        // image=R.id.toast_image
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);

        LayoutInflater ly=getLayoutInflater();
        View v=ly.inflate(R.layout.toast,(ViewGroup)findViewById(R.id.line1));
        TextView txt1=(TextView)v.findViewById(R.id.toast_text1);
        TextView txt2=(TextView)v.findViewById(R.id.toast_text2);
        ImageView img =(ImageView)v.findViewById(R.id.toast_image);
        txt1.setText(headerText);
        txt2.setText(message);
        img.setImageResource(image);

        toast.setView((v));
        toast.show();

    }

}
