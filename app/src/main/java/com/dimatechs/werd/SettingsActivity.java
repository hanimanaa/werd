package com.dimatechs.werd;


import android.app.ProgressDialog;
import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dimatechs.werd.Model.Users;
import com.dimatechs.werd.Model.UsersGroups;
import com.dimatechs.werd.Prevalent.Prevalent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;

public class SettingsActivity extends AppCompatActivity
{
    private EditText nameEditText, passswordEditText;
    private TextView closeTextBtn, saveTextButton;
    private ProgressDialog loadingBar;
    DatabaseReference ugRef,UsersRef;
    String userPhone,name="",password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nameEditText = (EditText) findViewById(R.id.settings_name);
        passswordEditText = (EditText) findViewById(R.id.settings_password);
        closeTextBtn = (TextView) findViewById(R.id.close_settings_btn);
        saveTextButton = (TextView) findViewById(R.id.update_account_settings_btn);
        loadingBar=new ProgressDialog(this);

        ugRef = FirebaseDatabase.getInstance().getReference().child("UsersGroups");
        userPhone=Prevalent.currentOnlineUser.getPhone();


        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
                UpdateUserNameInUsergroups(name);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(userPhone!=null) {
            getUserPhoneDetails(userPhone);
        }
    }

    private void getUserPhoneDetails(String userPhone) {
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");

        UsersRef.child(userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    Users users=dataSnapshot.getValue(Users.class);
                    nameEditText.setText(users.getName());
                    passswordEditText.setText(users.getPassword());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void UpdateUserNameInUsergroups(final String userName)
    {
        final String userPhone =Prevalent.currentOnlineUser.getPhone();
        ugRef.orderByChild("userPhone").equalTo(userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<UsersGroups> usersGroups = new ArrayList<>();
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    UsersGroups ug = areaSnapshot.getValue(UsersGroups.class);
                    usersGroups.add(ug);
                }

                for (int i = 0; i < usersGroups.size(); i++) {
                    usersGroups.get(i).setUserName(userName);
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
                                                Toast.makeText(SettingsActivity.this, "تم التحديث بنجاح", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                Toast.makeText(SettingsActivity.this, "للاسف لم يتم التحديث !!!", Toast.LENGTH_SHORT).show();
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
    private void updateUserInfo() {
        name =nameEditText.getText().toString();
        password =passswordEditText.getText().toString();

        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "الرجاء ادخال الاسم", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "الرجاء ادخال كلمة السر", Toast.LENGTH_SHORT).show();
        } else
            {

            loadingBar.setTitle("עדכון חשבון");
            loadingBar.setMessage("המתן בבקשה");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

                UsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (userPhone != null || !dataSnapshot.child("Users").child(userPhone).exists()) {
                            HashMap<String, Object> userdataMap = new HashMap<>();
                            userdataMap.put("name", nameEditText.getText().toString());
                            userdataMap.put("password", passswordEditText.getText().toString());


                            UsersRef.child(userPhone).updateChildren(userdataMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SettingsActivity.this, "לקוח נרשם בהצלחה", Toast.LENGTH_SHORT).show();
                                                Paper.book().write("fullName",name);
                                                loadingBar.dismiss();
                                                Intent intent = new Intent(SettingsActivity.this, UsersGroupActivity.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(SettingsActivity.this, "שגיאת רשת", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });

                        }
                    }

                        @Override
                        public void onCancelled (DatabaseError databaseError)
                        {

                        }

                });


        }
    }



}
