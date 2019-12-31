package com.dimatechs.werd;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.dimatechs.werd.Model.Users;
import com.dimatechs.werd.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class SplashScreenActivity extends AppCompatActivity {
    private String userPhone,password,firstLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Paper.init(this);
        userPhone = Paper.book().read(Prevalent.UserPhoneKey);
        password = Paper.book().read(Prevalent.UserPasswordKey);
        Thread myThread=new Thread(){
            @Override
            public void run() {
                try {
                    sleep(500);
                    if(userPhone != null && password != null)
                    {
                        if (userPhone != "" && password != "") {
                        final DatabaseReference RootRef;
                        RootRef = FirebaseDatabase.getInstance().getReference();

                        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child("Users").child(userPhone).exists()) {

                                    Users usersData = dataSnapshot.child("Users").child(userPhone).getValue(Users.class);

                                    if (usersData.getPhone().equals(userPhone)) {
                                        if (usersData.getPassword().equals(password)) {
                                            String fullName = usersData.getName();
                                            Toast.makeText(SplashScreenActivity.this, "السلام عليكم " + fullName, Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(SplashScreenActivity.this, UsersGroupActivity.class);
                                            Paper.book().write("fullName", fullName);
                                            Prevalent.currentOnlineUser = usersData;
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();

                                        }
                                    }
                                } else {
                                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        }else{
                            // userPhone == "" || password == ""
                            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else
                    {
                        // userPhone == null || password == null
                        Paper.book().write(Prevalent.UserPhoneKey, "");
                        Paper.book().write(Prevalent.UserPasswordKey, "");
                        Intent intent = new Intent(SplashScreenActivity.this, FirstLogActivity.class);
                            startActivity(intent);
                            finish();
                    }

                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}
