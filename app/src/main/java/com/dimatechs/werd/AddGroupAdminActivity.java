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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddGroupAdminActivity extends AppCompatActivity {

    private Button AddGroupBtn;
    private EditText Edgroup;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_admin);

        AddGroupBtn=(Button)findViewById(R.id.add_group_admin_btn);
        Edgroup=(EditText)findViewById(R.id.add_group_admin_Edgroup);

        loadingBar=new ProgressDialog(this);

        AddGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddGroup();
            }
        });
    }

    private void AddGroup()
    {
        String group =Edgroup.getText().toString();


        if(TextUtils.isEmpty(group))
        {
            Toast.makeText(this, "ادخل رقم الجموعه . . .", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("انشاء مجموعه");
            loadingBar.setMessage("انتظر");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateGroup(group);

        }
    }

    private void ValidateGroup(final String group)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.child("Groups").child(group).exists())
                {
                    HashMap<String,Object> userdataMap = new HashMap<>();
                    userdataMap.put("group",group);



                    RootRef.child("Groups").child(group).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(AddGroupAdminActivity.this, "تمت الاضافه بنجاح", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent=new Intent(AddGroupAdminActivity.this,MainActivity.class);
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
                    Toast.makeText(AddGroupAdminActivity.this, "المجموعه مسجله", Toast.LENGTH_SHORT).show();
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
