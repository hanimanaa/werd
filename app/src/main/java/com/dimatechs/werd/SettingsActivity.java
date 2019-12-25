package com.dimatechs.werd;


import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dimatechs.werd.Prevalent.Prevalent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
;


import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity
{
    private EditText nameEditText, passswordEditText;
    private TextView closeTextBtn, saveTextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nameEditText = (EditText) findViewById(R.id.settings_name);
        passswordEditText = (EditText) findViewById(R.id.settings_password);
        closeTextBtn = (TextView) findViewById(R.id.close_settings_btn);
        saveTextButton = (TextView) findViewById(R.id.update_account_settings_btn);


        userInfoDisplay(nameEditText,passswordEditText);


        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOnlyUserInfo();
            }
        });

    }


    private void updateOnlyUserInfo() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())) {
            Toast.makeText(this, "الرجاء ادخال الاسم", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(passswordEditText.getText().toString())) {
            Toast.makeText(this, "الرجاء ادخال كلمة السر", Toast.LENGTH_SHORT).show();
        } else {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("name", nameEditText.getText().toString());
            userMap.put("password", passswordEditText.getText().toString());

            ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

            startActivity(new Intent(SettingsActivity.this, GroupMainActivity.class));
            Toast.makeText(SettingsActivity.this, "تم التحديث بنجاح", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void userInfoDisplay(final EditText nameEditText, final EditText passswordEditText) {
        {
            DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

            UsersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.child("phone").exists()) {

                            String name = dataSnapshot.child("name").getValue().toString();
                            String password = dataSnapshot.child("password").getValue().toString();


                            nameEditText.setText(name);
                            passswordEditText.setText(password);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
