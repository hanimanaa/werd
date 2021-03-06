package com.dimatechs.werd;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dimatechs.werd.Model.UsersGroups;
import com.dimatechs.werd.Prevalent.Prevalent;
import com.dimatechs.werd.ViewHolder.NotificationViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class NotifcationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference RootRef;
    private FloatingActionButton btnSend;
    private ArrayList<UsersGroups> checkedUsers;
    private EditText edmessage;
    private String senderUserID,groupNum,groupname,IsAdmin;
    private DatabaseReference MessagesRef;
    private RadioButton rbNotSelect, rbSelectAll, rbRead, rbNotRead;
    private String fullName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifcation);


        Paper.init(this);
        fullName = Paper.book().read("fullName");

        MessagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        RootRef = FirebaseDatabase.getInstance().getReference().child("UsersGroups");
        checkedUsers = new ArrayList<>();


        //Paper.init(this);
        groupNum=Paper.book().read(Prevalent.GroupNum);
        groupname=Paper.book().read(Prevalent.GroupName);

        IsAdmin=getIntent().getStringExtra("IsAdmin");


        recyclerView = findViewById(R.id.recycler_users);
        //recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.requestFocus();


        edmessage = (EditText) findViewById(R.id.mess_bar);
        senderUserID = Prevalent.currentOnlineUser.getPhone();

        btnSend = (FloatingActionButton) findViewById(R.id.send_btn);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = edmessage.getText().toString();
                if (TextUtils.isEmpty(message))
                {
                    edmessage.setError("الرجاء كتابة الرسالة");
                }
                else {
                    Calendar calendar = Calendar.getInstance();

                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                    String CurrentDate = currentDate.format(calendar.getTime());

                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                    String CurrentTime = currentTime.format(calendar.getTime());

                    if (checkedUsers.size() == 0) {
                        MakeToast("","يجب اختيار المرسل لهم  !!!",R.drawable.error1);
                    } else {

                        // send copy to admin
                        HashMap<String, String> AdminMap = new HashMap<>();
                        AdminMap.put("groupNum", groupNum);
                        AdminMap.put("groupName", groupname);
                        AdminMap.put("from", senderUserID);
                        AdminMap.put("body", "ارسلت الى " +checkedUsers.size()+" اعضاء"+"\n \n" + message);
                        AdminMap.put("senderName"," نسخة للمدير-"+ fullName);
                        AdminMap.put("time", CurrentTime);
                        AdminMap.put("date", CurrentDate);
                        MessagesRef.child(senderUserID).push().setValue(AdminMap);


                        for (int i = 0; i < checkedUsers.size(); i++) {

                            HashMap<String, String> NotificationMap = new HashMap<>();
                            NotificationMap.put("groupNum", groupNum);
                            NotificationMap.put("groupName", groupname);
                            NotificationMap.put("from", senderUserID);
                            NotificationMap.put("body", message);
                            NotificationMap.put("senderName", fullName);
                            NotificationMap.put("time", CurrentTime);
                            NotificationMap.put("date", CurrentDate);


                            String receiverUserID = checkedUsers.get(i).getUserPhone();


                            MessagesRef.child(receiverUserID).push()
                                    .setValue(NotificationMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                MakeToast("ارسال رسالة", "تم ارسال الرسالة بنجاح", R.drawable.ok);
                                                Intent intent = new Intent(NotifcationActivity.this, GroupMainActivity.class);
                                                intent.putExtra("IsAdmin", IsAdmin);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                        }
                    }
                }
            }
        });

        // Sort By Radio Button

        rbSelectAll = (RadioButton) findViewById(R.id.RB_selectAll);
        rbNotSelect = (RadioButton) findViewById(R.id.RB_NotSelect);
        rbRead = (RadioButton) findViewById(R.id.RB_read);
        rbNotRead = (RadioButton) findViewById(R.id.RB_notRead);

        rbSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               FillRecyclerView(0);
            }
        });
        rbNotSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               FillRecyclerView(1);
            }
        });
        rbRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FillRecyclerView(2);            }
        });
        rbNotRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FillRecyclerView(3);            }
        });

    }



    private void FillRecyclerView(final int x) {
        Query query =RootRef.orderByChild("groupNum").equalTo(groupNum);

        FirebaseRecyclerOptions<UsersGroups> options =
                new FirebaseRecyclerOptions.Builder<UsersGroups>()
                        .setQuery(query, UsersGroups.class)
                        .build();

        FirebaseRecyclerAdapter<UsersGroups, NotificationViewHolder> adapter =
                new FirebaseRecyclerAdapter<UsersGroups, NotificationViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final NotificationViewHolder holder, int position, @NonNull final UsersGroups model) {
                        holder.txtPhone.setText(model.getUserPhone());
                        holder.txtName.setText(model.getUserName());
                        holder.txtNum.setText(" جزء رقم : " + model.getPartNum());

                        if (model.getDone().equals("done")) {
                            holder.doneImageView.setImageResource(R.drawable.ic_person_green);
                        } else {
                            holder.doneImageView.setImageResource(R.drawable.ic_person_red);
                        }

                        if (x == 0) {
                            //select All
                            holder.chBox.setChecked(true);
                            checkedUsers.add(model);
                        }
                        else if (x == 1) {
                            // not select
                            holder.chBox.setChecked(false);
                            checkedUsers.clear();
                        }
                        else if (x == 2) {
                            // read
                            if(model.getDone().equals("done"))
                            {
                                holder.chBox.setChecked(true);
                                checkedUsers.add(model);
                            }
                            else
                            {
                                holder.chBox.setChecked(false);
                                checkedUsers.clear();
                            }
                        }
                        else if (x == 3) {
                            // not read
                            if(model.getDone().equals("no"))
                            {
                                holder.chBox.setChecked(true);
                                checkedUsers.add(model);
                            }
                            else
                            {
                                holder.chBox.setChecked(false);
                                checkedUsers.clear();
                            }
                        }


                        holder.chBox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CheckBox myCheckBox = (CheckBox) v;

                                if (myCheckBox.isChecked()) {
                                    checkedUsers.add(model);
                                } else if (!myCheckBox.isChecked()) {
                                    checkedUsers.remove(model);
                                }
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item_layout, parent, false);
                        NotificationViewHolder holder = new NotificationViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query =RootRef.orderByChild("groupNum").equalTo(groupNum);

        FirebaseRecyclerOptions<UsersGroups> options =
                new FirebaseRecyclerOptions.Builder<UsersGroups>()
                        .setQuery(query, UsersGroups.class)
                        .build();

        FirebaseRecyclerAdapter<UsersGroups, NotificationViewHolder> adapter =
                new FirebaseRecyclerAdapter<UsersGroups, NotificationViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final NotificationViewHolder holder, int position, @NonNull final UsersGroups model) {
                        holder.txtPhone.setText(model.getUserPhone());
                        holder.txtName.setText(model.getUserName());
                        holder.txtNum.setText(" جزء رقم : " + model.getPartNum());

                        if (model.getDone().equals("done")) {
                            holder.doneImageView.setImageResource(R.drawable.ic_person_green);
                        } else {
                            holder.doneImageView.setImageResource(R.drawable.ic_person_red);
                        }


                        holder.chBox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CheckBox myCheckBox = (CheckBox) v;

                                if (myCheckBox.isChecked()) {
                                    checkedUsers.add(model);
                                } else if (!myCheckBox.isChecked()) {
                                    checkedUsers.remove(model);
                                }
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item_layout, parent, false);
                        NotificationViewHolder holder = new NotificationViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
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



