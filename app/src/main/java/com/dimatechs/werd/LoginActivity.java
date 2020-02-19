package com.dimatechs.werd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dimatechs.werd.Model.Users;
import com.dimatechs.werd.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private Button LoginButton,RegisterButton;
    private EditText InputNumber,InputPassword;
    private ProgressDialog loadingBar;
    private String parentDbName="Users";
    private CheckBox chkBoxRememberMe;
    String phone="",password="";
    DatabaseReference UserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_btn);
        RegisterButton= (Button) findViewById(R.id.register_btn);
        InputNumber = (EditText) findViewById(R.id.login_phone_number_input);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        loadingBar = new ProgressDialog(this);

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        chkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        // check if user saved
        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if (UserPhoneKey != "" && UserPasswordKey != "")
        {
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey))
            {
                InputNumber.setText(UserPhoneKey);
                InputPassword.setText(UserPasswordKey);
                chkBoxRememberMe.setChecked(true);
            }

        }
        chkBoxRememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(chkBoxRememberMe.isChecked()){
                    Paper.book().write(Prevalent.UserPhoneKey, phone);
                    Paper.book().write(Prevalent.UserPasswordKey, password);
                }else{
                    Paper.book().write(Prevalent.UserPhoneKey, "");
                    Paper.book().write(Prevalent.UserPasswordKey, "");
                }
            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    private void LoginUser()
    {
        phone=InputNumber.getText().toString();
        password=InputPassword.getText().toString();

        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "ادخل رقم الهاتف اذا سمحت . . .", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "ادخل الكلمه السريه اذا سمحت . . .", Toast.LENGTH_SHORT).show();
        }
        else
        {

            AllowAccessToAccount(phone, password);
            loadingBar.setTitle("دخول المستخدم");
            loadingBar.setMessage("انتظر");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

        }
    }

    private void AllowAccessToAccount(final String phone, final String password)
    {

        if(chkBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child(parentDbName).child(phone).exists())
                {

                   final Users usersData =dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if(usersData.getPhone().equals(phone))
                    {
                        if(usersData.getPassword().equals(password))
                        {


                            final String fullName = usersData.getName() ;
                            Toast.makeText(LoginActivity.this, "السلام عليكم " + fullName, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            FirebaseInstanceId.getInstance().getInstanceId()
                                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                            if (!task.isSuccessful()) {
                                                return;
                                            }
                                            // Get new Instance ID token
                                            String deviceToken = task.getResult().getToken();

                                            UserRef.child(phone).child("device_token")
                                                    .setValue(deviceToken)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if(task.isSuccessful())
                                                            {
                                                               Intent intent=new Intent(LoginActivity.this,UsersGroupActivity.class);
                                                             //  Intent intent=new Intent(LoginActivity.this,AlarmActivity.class);

                                                                intent.putExtra("phone",phone);
                                                                Paper.book().write("fullName",fullName);
                                                                Prevalent.currentOnlineUser = usersData;
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }
                                                    });
                                        }
                                    });

                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "الكلمه السريه غير صحيحه", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "المستخدم غير مسجل", Toast.LENGTH_SHORT).show();
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
