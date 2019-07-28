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

import com.dimatechs.werd.Model.Groups;
import com.dimatechs.werd.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddGroupsToUserActivity extends AppCompatActivity {

    private Button AddGroupToUserBtn;
    private EditText EdgroupNum,EdPartNum;
    private ProgressDialog loadingBar;
  //  String st="no user";
  //  private String groupN="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_groups_to_user);

        AddGroupToUserBtn=(Button)findViewById(R.id.add_group_Add_btn);
        EdgroupNum=(EditText)findViewById(R.id.add_group_Edgroup);
        EdPartNum=(EditText)findViewById(R.id.add_group_EdPartnum);

        loadingBar=new ProgressDialog(this);

        AddGroupToUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Addgroup();
            }
        });

    }

    private void Addgroup()
    {
        String groupNum =EdgroupNum.getText().toString();
        String partNum =EdPartNum.getText().toString();

        if(TextUtils.isEmpty(groupNum))
        {
            Toast.makeText(this, "ادخل رقم المجموعه . . .", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(partNum))
        {
            Toast.makeText(this, "ادخل رقم الجزء . . .", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("اضافة مجموعه");
            loadingBar.setMessage("انتظر");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            //String groupName = GetGroupNameFromFireBase(groupNum);


           // GetGroupNameFromFireBase("10");
            ValidateGroupNumber(groupNum,partNum);

        }

    }
/*
    private void GetGroupNameFromFireBase(final String groupNum)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.orderByChild("Groups").equalTo(groupNum).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Groups groups= dataSnapshot.getChildren().iterator().next().getValue(Groups.class);
                Toast.makeText(AddGroupsToUserActivity.this, groups.getGroupName().toString(), Toast.LENGTH_SHORT).show();


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
*/
    private void ValidateGroupNumber(final String group,final String partNum)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child("Groups").child(group).exists())
                {
                    dataSnapshot.child("Groups").child(group).child("groupName").getValue(String.class);
                    HashMap<String,Object> groupdataMap = new HashMap<>();
                    groupdataMap.put("groupNum",group);
                    groupdataMap.put("groupName",dataSnapshot.child("Groups").child(group).child("groupName").getValue(String.class));
                    groupdataMap.put("partNum",partNum);


                    RootRef.child("UsersGroups").child(Prevalent.currentOnlineUser.getPhone())
                            .child("group").child(group)
                            .updateChildren(groupdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(AddGroupsToUserActivity.this, "تمت الاضافه بنجاح", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent=new Intent(AddGroupsToUserActivity.this,UsersGroupActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(AddGroupsToUserActivity.this, "שגיאת רשת", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(AddGroupsToUserActivity.this, "المجموعه غير موجودة", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    //   Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                    //    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

    }
}
