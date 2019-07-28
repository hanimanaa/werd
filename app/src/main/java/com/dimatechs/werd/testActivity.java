package com.dimatechs.werd;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dimatechs.werd.Model.Groups;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class testActivity extends AppCompatActivity {

    private DatabaseReference RootRef;
    private Button Bt1;
    private EditText Ed1;
    private TextView Tv1;
    private String groupName="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Bt1=(Button)findViewById(R.id.button2) ;
        Ed1=(EditText)findViewById(R.id.editText1) ;
        Tv1=(TextView)findViewById(R.id.textView1) ;


        RootRef = FirebaseDatabase.getInstance().getReference();
        Bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Find(Ed1.getText().toString());
            }
        });
    }

    private void Find(final String groupNum)
    {
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Groups").child(groupNum).exists())
                {
                    Groups groupData =dataSnapshot.child("Groups").child(groupNum).getValue(Groups.class);

                    Tv1.setText(groupData.getGroupName().toString());
                    groupName = groupData.getGroupName().toString();
                    Toast.makeText(testActivity.this,groupName, Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(testActivity.this, "no group", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

